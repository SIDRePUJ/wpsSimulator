package org.wpsim.WellProdSim.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class wpsCSV {
    private static final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

    public static void log(String filePath, String message) {
        Lock lock = locks.computeIfAbsent(filePath, k -> new ReentrantLock());
        lock.lock();

        try {
            Path logFilePath = Paths.get("logs", filePath + ".csv");

            if (Files.notExists(logFilePath)) {
                Files.createDirectories(logFilePath.getParent());
                Files.createFile(logFilePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(logFilePath,
                    StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
