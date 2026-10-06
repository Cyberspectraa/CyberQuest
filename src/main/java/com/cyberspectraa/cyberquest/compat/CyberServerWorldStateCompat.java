package com.cyberspectraa.cyberquest.compat;

import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Method;

public final class CyberServerWorldStateCompat {
    private static final String API =
        "com.cyberspectraa.cyberserver.CyberServerApi";

    private static boolean resolved;
    private static Method storyActMethod;
    private static Method flagMethod;
    private static Method regionMethod;
    private static Method rewardMultiplierMethod;
    private static Method questBindingsMethod;

    private CyberServerWorldStateCompat() {
    }

    public static int storyAct(MinecraftServer server) {
        Object value = invoke(
            storyAct(),
            server
        );

        return value instanceof Number number
            ? Math.max(1, number.intValue())
            : 1;
    }

    public static boolean flag(
        MinecraftServer server,
        String flag
    ) {
        Object value = invoke(
            flag(),
            server,
            flag
        );

        return value instanceof Boolean bool
            && bool;
    }

    public static boolean regionUnlocked(
        MinecraftServer server,
        String region
    ) {
        Object value = invoke(
            region(),
            server,
            region
        );

        return value instanceof Boolean bool
            && bool;
    }

    public static java.util.List<String> questBindings(
        MinecraftServer server,
        String npcId
    ) {
        Object value = invoke(
            questBindings(),
            server,
            npcId
        );

        if (value instanceof String[] array) {
            return java.util.List.of(array);
        }

        return java.util.List.of();
    }

    public static double rewardMultiplier(
        MinecraftServer server
    ) {
        Object value = invoke(
            rewardMultiplier(),
            server
        );

        if (value instanceof Number number) {
            return Math.max(
                0.0D,
                number.doubleValue()
            );
        }

        return 1.0D;
    }

    private static Method storyAct() {
        resolve();
        return storyActMethod;
    }

    private static Method flag() {
        resolve();
        return flagMethod;
    }

    private static Method region() {
        resolve();
        return regionMethod;
    }

    private static Method rewardMultiplier() {
        resolve();
        return rewardMultiplierMethod;
    }

    private static Method questBindings() {
        resolve();
        return questBindingsMethod;
    }

    private static Object invoke(
        Method method,
        Object... args
    ) {
        if (method == null) {
            return null;
        }

        try {
            return method.invoke(null, args);
        } catch (ReflectiveOperationException
                | RuntimeException ignored) {
            return null;
        }
    }

    private static void resolve() {
        if (resolved) {
            return;
        }

        resolved = true;

        try {
            Class<?> api = Class.forName(API);

            storyActMethod = api.getMethod(
                "getStoryAct",
                MinecraftServer.class
            );
            flagMethod = api.getMethod(
                "isFlagSet",
                MinecraftServer.class,
                String.class
            );
            regionMethod = api.getMethod(
                "isRegionUnlocked",
                MinecraftServer.class,
                String.class
            );
            rewardMultiplierMethod = api.getMethod(
                "getQuestRewardMultiplier",
                MinecraftServer.class
            );
            questBindingsMethod = api.getMethod(
                "getNpcQuestBindings",
                MinecraftServer.class,
                String.class
            );
        } catch (ReflectiveOperationException
                | LinkageError ignored) {
            storyActMethod = null;
            flagMethod = null;
            regionMethod = null;
            rewardMultiplierMethod = null;
            questBindingsMethod = null;
        }
    }
}
