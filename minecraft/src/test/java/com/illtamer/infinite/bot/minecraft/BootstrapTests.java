package com.illtamer.infinite.bot.minecraft;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BootstrapTests {

    public static void main(String[] args) {
        SimpleDateFormat FORMAT = new SimpleDateFormat("HH'h'mm'm'ss's'");
        System.out.println(FORMAT.format(new Date()));
    }

}
