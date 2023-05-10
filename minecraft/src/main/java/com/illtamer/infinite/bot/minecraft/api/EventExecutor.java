package com.illtamer.infinite.bot.minecraft.api;

import com.illtamer.infinite.bot.api.event.Cancellable;
import com.illtamer.infinite.bot.api.event.Event;
import com.illtamer.infinite.bot.api.event.MetaEvent;
import com.illtamer.infinite.bot.api.util.ClassUtil;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.infinite.bot.minecraft.api.event.EventHandler;
import com.illtamer.infinite.bot.minecraft.api.event.EventPriority;
import com.illtamer.infinite.bot.minecraft.api.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class EventExecutor {

    /**
     * 经注册的 Listener 实现类对象
     * */
    private static final HashMap<IExpansion, Set<Listener>> LISTENERS = new HashMap<>();

    /**
     * 附属注册的 Bukkit Event
     * */
    private static final Map<IExpansion, Set<org.bukkit.event.Listener>> EXPANSION_BUKKIT_LISTENERS = new ConcurrentHashMap<>();

    /**
     * 读取到的需要监听的方法
     * */
    private static final HashMap<Listener, HashMap<Method, Annotation>> METHODS = new HashMap<>();

    /**
     * 注册监听器
     * @param listener 实现Listener的监听对象
     * */
    public static void registerEvents(Listener listener, IExpansion instance) {
        LISTENERS.computeIfAbsent(instance,
                k -> new HashSet<>(Collections.singleton(listener))
        ).add(listener);
        // 加载注册方法
        final HashMap<Method, Annotation> methods = ClassUtil.getMethods(listener, EventHandler.class);
        METHODS.put(listener, methods);
    }

    /**
     * 以 IB3 的名义向 Bukkit 注册附属中的 Bukkit Event Listener
     * @apiNote 注册的监听会在附属卸载时自动注销
     * */
    public static void registerBukkitEvent(org.bukkit.event.Listener bukkitListener, IExpansion expansion) {
        if (EXPANSION_BUKKIT_LISTENERS.computeIfAbsent(expansion, k -> new HashSet<>()).add(bukkitListener)) {
            Bukkit.getPluginManager().registerEvents(bukkitListener, BukkitBootstrap.getInstance());
        } else {
            BukkitBootstrap.getInstance().getLogger().warning(String.format("The listener(%s) is repeatedly registered by the expansion(%s)", bukkitListener, expansion));
        }
    }

    /**
     * 注销附属注册的 Bukkit 监听
     * @apiNote 在插件卸载时自动调用
     * */
    public static void unregisterBukkitEventForExpansion(IExpansion expansion) {
        final Set<org.bukkit.event.Listener> remove = EXPANSION_BUKKIT_LISTENERS.remove(expansion);
        if (remove != null)
            remove.forEach(HandlerList::unregisterAll);
    }

    /**
     * 注销附属的监听
     * @apiNote 如需注销附属，请使用 {@link com.illtamer.infinite.bot.minecraft.expansion.ExpansionLoader#disableExpansion(String)}
     * */
    public static void unregisterListeners(IExpansion instance) {
        final Set<Listener> listeners = LISTENERS.remove(instance);
        if (listeners == null) return;
        // 卸载注册方法
        synchronized (METHODS) {
            listeners.forEach(METHODS::remove);
        }
    }

    /**
     * 轮询执行事件 调度异步线程
     * */
    public static void dispatchListener(Event event) {
        if (METHODS.size() == 0) return;
        BotScheduler.runTask(new Dispatcher(event));
    }

    /**
     * 事件调度类
     * */
    private static class Dispatcher implements Runnable {

        private final Event event;

        public Dispatcher(Event event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event instanceof MetaEvent) {
                MetaEvent meta = (MetaEvent) event;
                if (meta.isHeartbeat()) return;
            }
            final Logger logger = BukkitBootstrap.getInstance().getLogger();
            for (Map.Entry<Listener, HashMap<Method, Annotation>> map : METHODS.entrySet()) { // class
                Object instance = map.getKey();
                for (int i = EventPriority.values().length-1; i >= 0; i --) {
                    EventPriority current = EventPriority.values()[i];
                    for (Map.Entry<Method, Annotation> entry : map.getValue().entrySet()) { // method
                        if (current != ((EventHandler) entry.getValue()).priority()) continue;
                        if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) continue;
                        Method method = entry.getKey();
                        if (method.getParameterCount() != 1) {
                            logger.warning(String.format("Excepted parameter count: %d(%s)", method.getParameterCount(), method));
                            continue;
                        }
                        Class<?> paramType = method.getParameterTypes()[0];
                        if (!Event.class.isAssignableFrom(paramType))
                            logger.warning(String.format("Unknown param type(%s) in %s", paramType, method));
                        if (paramType.isInstance(event)) {
                            try {
                                method.invoke(instance, event);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }

}
