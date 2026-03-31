package com.illtamer.infinite.bot.minecraft.expansion.manager;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.api.command.ExpansionCommand;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.util.BukkitUtil;
import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;
import com.illtamer.infinite.bot.minecraft.util.ReflectionUtil;
import com.illtamer.infinite.bot.minecraft.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 附属命令注册中心
 * <p>
 * 负责在附属启用时注册命令，在附属卸载时反注册命令。
 * */
public final class ExpansionCommandRegistry {

    private static final Map<IExpansion, Set<BukkitExpansionCommand>> EXPANSION_COMMAND_MAP = new ConcurrentHashMap<>();

    private static final Object COMMAND_CONTEXT_LOCK = new Object();
    private static volatile SimpleCommandMap commandMap;
    private static volatile Map<String, Command> knownCommands;

    private ExpansionCommandRegistry() {}

    /**
     * 注册附属指令
     * */
    public static void registerCommands(@NotNull IExpansion expansion) {
        final Collection<? extends ExpansionCommand> declared = expansion.getCommands();
        if (declared == null || declared.isEmpty()) {
            return;
        }

        final List<ExpansionCommand> commands = declared.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (commands.isEmpty()) {
            return;
        }

        BukkitUtil.callInMainThread(() -> {
            unregisterCommandsInternal(expansion);
            registerCommandsInternal(expansion, commands);
            return null;
        });
    }

    /**
     * 注销附属指令
     * */
    public static void unregisterCommands(@NotNull IExpansion expansion) {
        BukkitUtil.callInMainThread(() -> {
            unregisterCommandsInternal(expansion);
            return null;
        });
    }

    private static void registerCommandsInternal(@NotNull IExpansion expansion, @NotNull List<ExpansionCommand> commands) {
        ensureCommandContext();

        final Logger logger = BukkitBootstrap.getInstance().getLogger();
        final String fallbackPrefix = safeFallbackPrefix(expansion);

        final Set<BukkitExpansionCommand> wrappedCommands = new LinkedHashSet<>();
        final Set<String> localLabels = new HashSet<>();

        for (ExpansionCommand command : commands) {
            final String rawName = command.getName();
            final String name = StringUtil.normalizeLabel(rawName);
            if (name == null) {
                throw new IllegalArgumentException("Expansion command name can not be empty: " + command.getClass().getName());
            }

            final List<String> aliases = normalizeAliases(command.getAliases(), localLabels, name);
            if (!localLabels.add(name)) {
                throw new IllegalArgumentException("Duplicate expansion command label: " + name + " in " + expansion);
            }

            final BukkitExpansionCommand wrapped = new BukkitExpansionCommand(expansion, command, name, aliases);
            final boolean primaryRegistered = commandMap.register(fallbackPrefix, wrapped);
            if (!primaryRegistered) {
                logger.warning(String.format("Expansion command '/%s' from %s conflicts with existing labels. Use fallback namespace if needed.",
                        name, ExpansionUtil.formatIdentifier(expansion)));
            }
            wrappedCommands.add(wrapped);
        }

        if (!wrappedCommands.isEmpty()) {
            EXPANSION_COMMAND_MAP.put(expansion, wrappedCommands);
        }
    }

    private static List<String> normalizeAliases(@Nullable List<String> aliases,
                                                 @NotNull Set<String> localLabels,
                                                 @NotNull String mainName) {
        if (aliases == null || aliases.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> normalized = new ArrayList<>();
        final String normalizedMain = StringUtil.normalizeLabel(mainName);

        for (String alias : aliases) {
            final String normalizedAlias = StringUtil.normalizeLabel(alias);
            if (normalizedAlias == null) {
                continue;
            }
            if (normalizedAlias.equals(normalizedMain)) {
                continue;
            }
            if (localLabels.contains(normalizedAlias)) {
                throw new IllegalArgumentException("Duplicate expansion command alias: " + normalizedAlias);
            }
            localLabels.add(normalizedAlias);
            normalized.add(normalizedAlias);
        }

        return normalized;
    }

    private static void unregisterCommandsInternal(@NotNull IExpansion expansion) {
        final Set<BukkitExpansionCommand> commands = EXPANSION_COMMAND_MAP.remove(expansion);
        if (commands == null || commands.isEmpty()) {
            return;
        }

        ensureCommandContext();

        for (BukkitExpansionCommand command : commands) {
            try {
                command.unregister(commandMap);
            } catch (Throwable ex) {
                BukkitBootstrap.getInstance().getLogger().warning(String.format("Error while unregistering expansion command %s of %s: %s",
                        command.getName(), ExpansionUtil.formatIdentifier(expansion), ex.getMessage()));
            }
        }

        knownCommands.entrySet().removeIf(entry -> commands.contains(entry.getValue()));
    }

    private static String safeFallbackPrefix(@NotNull IExpansion expansion) {
        final String raw = ExpansionUtil.formatIdentifier(expansion).toLowerCase(Locale.ROOT);
        final String prefix = raw.replaceAll("[^a-z0-9_.-]", "_");
        return prefix.isEmpty() ? "expansion" : prefix;
    }

    private static void ensureCommandContext() {
        if (commandMap != null && knownCommands != null) {
            return;
        }

        synchronized (COMMAND_CONTEXT_LOCK) {
            if (commandMap != null && knownCommands != null) {
                return;
            }

            commandMap = resolveCommandMap();
            knownCommands = resolveKnownCommands(commandMap);
        }
    }

    @NotNull
    private static SimpleCommandMap resolveCommandMap() {
        final Object server = Bukkit.getServer();

        try {
            final Method method = server.getClass().getMethod("getCommandMap");
            final Object result = method.invoke(server);
            if (result instanceof SimpleCommandMap) {
                return (SimpleCommandMap) result;
            }
        } catch (Exception ignored) {}

        try {
            final Field field = ReflectionUtil.findField(server.getClass(), "commandMap");
            field.setAccessible(true);
            final Object value = field.get(server);
            if (value instanceof SimpleCommandMap) {
                return (SimpleCommandMap) value;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve Bukkit SimpleCommandMap", e);
        }

        throw new IllegalStateException("Cannot resolve Bukkit SimpleCommandMap instance");
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static Map<String, Command> resolveKnownCommands(@NotNull SimpleCommandMap commandMap) {
        try {
            final Field known = ReflectionUtil.findField(commandMap.getClass(), "knownCommands");
            known.setAccessible(true);
            final Object value = known.get(commandMap);
            if (value instanceof Map) {
                return (Map<String, Command>) value;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve Bukkit knownCommands map", e);
        }
        throw new IllegalStateException("Cannot resolve Bukkit knownCommands map");
    }

    private static final class BukkitExpansionCommand extends Command implements PluginIdentifiableCommand {

        private static final Logger LOGGER = BukkitBootstrap.getInstance().getLogger();
        private final IExpansion expansion;
        private final ExpansionCommand delegate;

        private BukkitExpansionCommand(@NotNull IExpansion expansion,
                                       @NotNull ExpansionCommand delegate,
                                       @NotNull String name,
                                       @NotNull List<String> aliases) {
            super(name, delegate.getDescription(), delegate.getUsage(), aliases);
            this.expansion = expansion;
            this.delegate = delegate;
            setPermission(delegate.getPermission());
            setPermissionMessage(delegate.getPermissionMessage());
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
            if (!testPermission(sender)) {
                return true;
            }
            try {
                return delegate.execute(sender, label, args);
            } catch (Throwable ex) {
                LOGGER.severe(String.format("Error executing expansion command '/%s' from %s",
                        getName(), ExpansionUtil.formatIdentifier(expansion)));
                ex.printStackTrace();
                return true;
            }
        }

        @NotNull
        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
            if (!testPermissionSilent(sender)) {
                return Collections.emptyList();
            }
            try {
                final List<String> complete = delegate.tabComplete(sender, alias, args);
                return complete == null ? Collections.emptyList() : complete;
            } catch (Throwable ex) {
                LOGGER.warning(String.format("Error tab-completing expansion command '/%s' from %s: %s",
                        getName(), ExpansionUtil.formatIdentifier(expansion), ex.getMessage()));
                return Collections.emptyList();
            }
        }

        @Override
        @NotNull
        public Plugin getPlugin() {
            return BukkitBootstrap.getInstance();
        }
    }

}
