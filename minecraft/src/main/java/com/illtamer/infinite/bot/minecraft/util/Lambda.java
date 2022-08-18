package com.illtamer.infinite.bot.minecraft.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@UtilityClass
public class Lambda {

    /**
     * @param func 结果参数函数式调用
     * @param result 结果参数
     * */
    @Nullable
    public static <Result, Return> Return nullableInvoke(Function<Result, Return> func, @Nullable Result result) {
        return result != null ? func.apply(result) : null;
    }

    /**
     * 三元运算
     * */
    public static <Type> Type ternary(boolean condition, Type pass, Type deny) {
        return condition ? pass : deny;
    }

}
