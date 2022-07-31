package com.illtamer.infinite.bot.minecraft.util;

public class PluginUtil {

    public static String parseColor(String s) {
        return s.replace('&', '§').replace("§§", "&");
    }

    public static String clearColor(String s) {
        char[] chars = s.toCharArray();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < chars.length; ++i) {
            if (chars[i] == 167 && i + 1 <= chars.length) {
                int next = chars[i + 1];
                if (next >= '0' && next <= '9' || next >= 'a' && next <= 'f' || next >= 'l' && next <= 'o' || next == 'r') {
                    ++i;
                    continue;
                }
            }
            builder.append(chars[i]);
        }
        return builder.toString();
    }

}
