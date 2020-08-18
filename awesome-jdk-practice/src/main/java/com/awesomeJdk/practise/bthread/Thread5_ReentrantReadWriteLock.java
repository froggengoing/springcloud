package com.awesomeJdk.practise.bthread;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Thread5_ReentrantReadWriteLock {
    private final Map<String, String> m = new TreeMap<String, String>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();    //读锁
    private final Lock w = rwl.writeLock();    //写锁

    public String get(String key) {
        r.lock();
        try {
            return m.get(key);
        } finally {
            r.unlock();
        }
    }

    public String[] allKeys() {
        r.lock();
        try {
            return (String[]) m.keySet().toArray();
        } finally {
            r.unlock();
        }
    }

    public String put(String key, String value) {
        w.lock();
        try {
            return m.put(key, value);
        } finally {
            w.unlock();
        }
    }

    public void clear() {
        w.lock();
        try {
            m.clear();
        } finally {
            w.unlock();
        }
    }

    public static void main(String[] args) {
        int SHARED_SHIFT   = 16;
        int SHARED_UNIT    = (1 << SHARED_SHIFT);
        int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
        System.out.println(SHARED_UNIT);
        System.out.println(MAX_COUNT);
        System.out.println(EXCLUSIVE_MASK);
        System.out.println(3>>SHARED_SHIFT);
    }
}
