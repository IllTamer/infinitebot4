package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.pojo.ExpansionIdentifier;
import com.illtamer.infinite.bot.minecraft.start.bukkit.BukkitBootstrap;
import com.illtamer.perpetua.sdk.util.Assert;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ExpansionUtil {

    public static final Pattern IDENTIFIER = Pattern.compile("([^-]*)-(.*)::(.*)");
    public static final String FORMAT = "%s-%s::%s";

    @NotNull
    public static String formatIdentifier(IExpansion expansion) {
        return formatIdentifier(expansion.getExpansionName(), expansion.getVersion(), expansion.getAuthor());
    }

    @NotNull
    public static String formatIdentifier(String name, String version, String author) {
        return String.format(FORMAT, name, version, author);
    }

    @Nullable
    public static ExpansionIdentifier parseIdentifier(String identifier) {
        final Matcher matcher = IDENTIFIER.matcher(identifier);
        if (matcher.find()) {
            return new ExpansionIdentifier(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        return null;
    }

    @Nullable
    public static InputStream getPluginResource(String name, ClassLoader classLoader) {
        URL url = classLoader.getResource(name);
        if (url == null) {
            return null;
        }
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static void savePluginResource(String path, boolean replace, File dataFolder, Function<String, InputStream> inputFunc) {
        Assert.isTrue(path != null && !path.isEmpty(), "The resource name can not be null !");
        path = path.replace("\\", "/");
        InputStream input = inputFunc.apply(path);
        Assert.notNull(input, String.format("Can't find the resource '%s'", path));

        File outFile = new File(dataFolder, path);
        int lastIndex = path.lastIndexOf('/');
        File outDir = new File(dataFolder, path.substring(0, Math.max(lastIndex, 0)));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = input.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                input.close();
            } else {
                BukkitBootstrap.getInstance().getLogger().warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists !");
            }
        } catch (IOException ex) {
            BukkitBootstrap.getInstance().getLogger().severe("Could not save " + outFile.getName() + " to " + outFile);
            ex.printStackTrace();
        }
    }

}
