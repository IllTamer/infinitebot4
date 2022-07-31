package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.minecraft.Bootstrap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class StringUtil {

    private static final Logger log = Bootstrap.getInstance().getLogger();

    /**
     * @return aaa\nbbb\nccc
     * */
    public static String toString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list)
            builder.append('\n').append(s);
        builder.deleteCharAt(0);
        return builder.toString();
    }

    public static String[] toArray(List<String> list) {
        String[] array = new String[list.size()];
        for(int i = 0; i < list.size(); ++i)
            array[i] = list.get(i);
        return array;
    }

    /**
     * @return [obj1, obj2]
     * */
    public static String parseString(List<String> list) {
        if (list == null) return null;
        if (list.size() == 0) return "[]";
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) builder.append(", ");
            builder.append(list.get(i));
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * @param list [obj1, obj2]
     * */
    @NotNull
    public static List<String> parseList(String list) {
        List<String> result = new ArrayList<>();
        if (list == null || list.length() <= 2) return result;
        try {
            final String[] split = list.substring(1, list.length() - 1).split(", ");
            result.addAll(Arrays.asList(split));
        } catch (Exception e) {
            log.warning("Some errors occurred in the process of parse '" + list + "'");
            e.printStackTrace();
        }
        return result;
    }

}
