package net.jetcobblestone.pluginmcu.util.annotationscheduling;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScheduledMethod {
    private final Object object;
    @Getter private final Method method;
    private final int ticks;
    private final int initialDelay;
    private boolean ranOnce = false;
    private int counter = 0;

    public ScheduledMethod(Object object, Method method, int ticks, int initialDelay) {
        this.object = object;
        this.method = method;
        this.ticks = Math.max(ticks, 1); //ticks is always at least 1
        this.initialDelay = Math.max(initialDelay, 0); //initial delay is always at least 0
    }

    public void tick() {
        if (!ranOnce) {
            if (counter == initialDelay) {
                invoke();
                ranOnce = true;
                counter = 1;
            }
        }
        else if (counter % ticks == 0) {
            invoke();
        }
        counter++;
    }

    private void invoke() {
        try {
            method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
