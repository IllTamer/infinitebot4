package com.illtamer.infinite.bot.minecraft;

import com.illtamer.infinite.bot.minecraft.util.ExpansionUtil;

import java.util.Arrays;

public class BootstrapTests {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(ExpansionUtil.IDENTIFIER.split("ChatManager-2.0: IllTamer")));
//        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//        System.out.println(elements[elements.length - 1].getClassName());

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
