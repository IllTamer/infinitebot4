package com.illtamer.infinite.bot.api;

import com.illtamer.infinite.bot.api.util.Assert;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Optional<T> {

    private T value;

    private Optional() {}

    private Optional(T value) {
        this.value = value;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    public void set(T value) {
        Assert.isNull(this.value, "Optional value exists!");
        this.value = value;
    }

    public T get() {
        Assert.notNull(value, "Unexpect null value.");
        return value;
    }

    public static <T> Optional<T> empty() {
        return new Optional<>();
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

}
