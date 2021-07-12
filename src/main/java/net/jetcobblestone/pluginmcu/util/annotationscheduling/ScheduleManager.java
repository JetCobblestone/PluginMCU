package net.jetcobblestone.pluginmcu.util.annotationscheduling;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScheduleManager {
    public Map<Object, Set<ScheduledMethod>> map = new HashMap<>();

    public ScheduleManager(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void tick() {
        for (Map.Entry<Object, Set<ScheduledMethod>> entry : map.entrySet()) {
            for (ScheduledMethod scheduledMethod : entry.getValue()) {
                scheduledMethod.tick();
            }
        }
    }

    public void register (Object object){
        final Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(TickableMethod.class)) {
                method.setAccessible(true);
                final Set<ScheduledMethod> set = map.computeIfAbsent(object, k -> new HashSet<>());
                final TickableMethod annotation = method.getAnnotation(TickableMethod.class);
                set.add(new ScheduledMethod(object, method, annotation.ticks(), annotation.initialDelay()));
            }
        }
    }
}