package com.illtamer.infinite.bot.minecraft.pojo;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TimedBlockingCache
 * @apiNote 基于LinkedHashMap实现FIFO容量控制，并使用 ReentrantLock 和 Condition 实现阻塞等待和超时机制
 * */
public class TimedBlockingCache<K, V> {

    @Getter
    private final int capacity;
    private final LinkedHashMap<K, V> map;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<K, Condition> waitConditions = new HashMap<>();

    public TimedBlockingCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public void put(K key, V value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        lock.lock();
        try {
            map.put(key, value);
            // 如果有线程在等待这个key，唤醒它们
            if (waitConditions.containsKey(key)) {
                waitConditions.get(key).signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public V get(K key, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        long deadlineMillis = System.currentTimeMillis() + unit.toMillis(timeout);
        lock.lock();
        try {
            // 循环等待，防止虚假唤醒
            while (!map.containsKey(key)) {
                long remaining = deadlineMillis - System.currentTimeMillis();
                if (remaining <= 0) {
                    waitConditions.remove(key);
                    throw new TimeoutException("Timeout waiting for key: " + key);
                }
                Condition condition = waitConditions.computeIfAbsent(key, k -> lock.newCondition());
                boolean timedOut = !condition.await(remaining, TimeUnit.MILLISECONDS);
                if (timedOut && !map.containsKey(key)) {
                    waitConditions.remove(key);
                    throw new TimeoutException("Timeout waiting for key: " + key);
                }
            }
            V value = map.get(key);
            map.remove(key);
            waitConditions.remove(key);
            return value;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(K key) {
        lock.lock();
        try {
            boolean exists = map.containsKey(key);
            if (exists) {
                map.remove(key);
                // 移除等待条件
                waitConditions.remove(key);
            }
            return exists;
        } finally {
            lock.unlock();
        }
    }

}
