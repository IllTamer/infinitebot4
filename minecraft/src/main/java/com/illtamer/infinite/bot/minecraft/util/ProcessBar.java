package com.illtamer.infinite.bot.minecraft.util;

import com.illtamer.infinite.bot.minecraft.api.StaticAPI;
import com.illtamer.infinite.bot.minecraft.api.adapter.Bootstrap;

public class ProcessBar {

    private final String endStr;

    private int current;
    private final int total;

    private ProcessBar(int total) {
        this.total = total;
        this.current = 0;
        this.endStr = StaticAPI.getInstance().getType() == Bootstrap.Type.BUKKIT ? "]\n" : "]";
    }

    /**
     * 增加进度
     * */
    public synchronized void count() {
        current += 1;
        // print, check finish
        print();
    }

    private void print() {
        System.out.printf("\r%d/%d [", current, total);
        for (int j = 0; j < current; ++ j) {
            System.out.print("=");
        }
        for (int j = current; j < total; ++ j) {
            System.out.print(" ");
        }
        System.out.print(endStr);
        if (current == total) {
            System.out.println();
        }
    }

    public static ProcessBar create(int total) {
        return new ProcessBar(total);
    }

}
