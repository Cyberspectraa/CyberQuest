package com.cyberspectraa.cyberquest.compat;

import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;

public final class CyberProgressionCompat {
    private static final String MANAGER =
        "com.cyberspectraa.cyberraces.progression.ProgressionManager";

    private static boolean resolved;
    private static Method getLevelMethod;
    private static Method addExperienceMethod;

    private CyberProgressionCompat() {
    }

    public static int getLevel(LivingEntity entity) {
        resolve();

        if (entity == null || getLevelMethod == null) {
            return 1;
        }

        try {
            Object value = getLevelMethod.invoke(null, entity);
            return value instanceof Number number
                ? Math.max(1, number.intValue())
                : 1;
        } catch (ReflectiveOperationException
                | RuntimeException ignored) {
            return 1;
        }
    }

    public static long addExperience(
        LivingEntity entity,
        long amount
    ) {
        resolve();

        if (entity == null
                || addExperienceMethod == null
                || amount <= 0L) {
            return 0L;
        }

        try {
            Object change = addExperienceMethod.invoke(
                null,
                entity,
                amount,
                "quest"
            );

            if (change != null) {
                try {
                    Method added = change.getClass()
                        .getMethod("experienceAdded");
                    Object value = added.invoke(change);

                    if (value instanceof Number number) {
                        return Math.max(
                            0L,
                            number.longValue()
                        );
                    }
                } catch (ReflectiveOperationException ignored) {
                }
            }

            return amount;
        } catch (ReflectiveOperationException
                | RuntimeException ignored) {
            return 0L;
        }
    }

    private static void resolve() {
        if (resolved) {
            return;
        }

        resolved = true;

        try {
            Class<?> manager = Class.forName(MANAGER);

            getLevelMethod = manager.getMethod(
                "getLevel",
                LivingEntity.class
            );

            addExperienceMethod = manager.getMethod(
                "addExperience",
                LivingEntity.class,
                long.class,
                String.class
            );
        } catch (ReflectiveOperationException
                | LinkageError ignored) {
            getLevelMethod = null;
            addExperienceMethod = null;
        }
    }
}
