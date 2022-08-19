package com.illtamer.infinite.bot.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
public class Pair<K, V> implements Map.Entry<K, V> {

    @Getter(AccessLevel.NONE)
    private final K k;
    @Getter(AccessLevel.NONE)
    private final V v;

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
