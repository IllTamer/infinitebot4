package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.minecraft.Bootstrap;

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

    public static class Version {

        public static final String VERSION;

        static {
            final String packageName = Bootstrap.getInstance().getServer().getClass().getPackage().getName();
            VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);
        }

        /**
         * @param number 中版本号，例如 v1_12_R1 -> 12
         * */
        public static boolean upper(int number) {
            final String[] split = VERSION.split("_");
            return Integer.parseInt(split[1]) > number;
        }

    }

}
