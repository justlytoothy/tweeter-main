package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceExecutor {
    public static <T extends Runnable> void execute(T task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
