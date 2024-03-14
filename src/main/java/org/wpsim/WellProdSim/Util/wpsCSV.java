package org.wpsim.WellProdSim.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class wpsCSV {
    private static final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

    public static void log(String filePath, String message) {
        Lock lock = locks.computeIfAbsent(filePath, k -> new ReentrantLock());
        lock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs/" + filePath + ".csv", true))) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}