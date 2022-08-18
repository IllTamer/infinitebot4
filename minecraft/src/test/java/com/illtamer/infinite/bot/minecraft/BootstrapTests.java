package com.illtamer.infinite.bot.minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BootstrapTests {

    public static void main(String[] args) {
//        ThreadPoolExecutor websocketExecutor = new ThreadPoolExecutor(1, 1,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<>());
//        Consumer<ThreadPoolExecutor> consumer;
//        try {
//
//            final Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//            field.setAccessible(true);
//            MethodHandles.Lookup IMPL_LOOKUP = (MethodHandles.Lookup) field.get(null);
//
////            final MethodHandles.Lookup privateLookupIn = MethodHandles.privateLookupIn(ThreadPoolExecutor.class, MethodHandles.lookup());
//            final MethodType methodType = MethodType.methodType(void.class);
//            final MethodHandle methodHandle = IMPL_LOOKUP.findVirtual(ThreadPoolExecutor.class, "interruptWorkers", methodType);
//            consumer = object -> {
//                try {
//                    methodHandle.invoke(object);
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//            };
//        } catch (Exception e) {
//            consumer = object -> {}; // do nothing
//            e.printStackTrace();
//        }
//
//        consumer.accept(websocketExecutor);
    }

}
