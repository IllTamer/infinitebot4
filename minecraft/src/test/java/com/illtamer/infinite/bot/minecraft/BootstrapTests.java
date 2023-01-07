package com.illtamer.infinite.bot.minecraft;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import com.illtamer.infinite.bot.minecraft.expansion.automation.AutoLoadConfiguration;

public class BootstrapTests {


//    public static void main(String[] args) {
////        CQHttpWebSocketConfiguration.setHttpUri("http://47.117.136.149:5700");
////        CQHttpWebSocketConfiguration.setAuthorization("root765743073");
////        System.out.println(OpenAPIHandling.getMessage(1051692076));
//
//    }

    static ENUM aaa;

    public static void main(String[] args) throws Exception {
        System.out.println(Enum.class.isAssignableFrom(BootstrapTests.class.getDeclaredField("aaa").getType()));
    }

    enum ENUM {

    }

}
