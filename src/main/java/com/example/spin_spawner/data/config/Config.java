package com.example.spin_spawner.data.config;

import java.util.HashSet;

public class Config {
    private static final ServerConfig SERVER = ModConfigs.server();
    public static float baseRpm;
    public static float minRpm;
    public static float stressPerRpm = 16;
    public static int generationSpeed = 64;
    public static boolean loadExternalEntity;
    public static HashSet<String> itemBlacklist;

    public static void load() {
        baseRpm = SERVER.baseRpm.getF();
        minRpm = SERVER.minRpm.getF();
        stressPerRpm = SERVER.stressPerRpm.getF();
        generationSpeed = SERVER.generationSpeed.get();
        loadExternalEntity = SERVER.loadExternalEntity.get();
        itemBlacklist = new HashSet<>(SERVER.itemBlacklist.get());
    }
}