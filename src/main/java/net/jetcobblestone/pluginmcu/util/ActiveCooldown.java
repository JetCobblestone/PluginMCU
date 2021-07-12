package net.jetcobblestone.pluginmcu.util;


import net.jetcobblestone.pluginmcu.util.annotationscheduling.ITickable;
import net.jetcobblestone.pluginmcu.util.annotationscheduling.ScheduleManager;
import net.jetcobblestone.pluginmcu.util.annotationscheduling.TickTask;

import java.util.function.Consumer;

public class ActiveCooldown implements ITickable {

    private int count = 0;
    private final int ticks;
    private final int runnableFrequency;
    private final Consumer<Integer> onTick;
    private final Runnable onFinish;
    private final ScheduleManager scheduleManager;

    public ActiveCooldown(int ticks, int runnableFrequency, Consumer<Integer> onTick, Runnable onFinish, ScheduleManager scheduleManager) {
        this.ticks = ticks;
        this.runnableFrequency = runnableFrequency;
        this.onTick = onTick;
        this.onFinish = onFinish;
        this.scheduleManager = scheduleManager;
        TickTask.getInstance(scheduleManager).add(this);
    }

    public void tick() {
        if (onTick != null && count % runnableFrequency == 0) {
            onTick.accept(count/runnableFrequency);
        }
        count++;
    }

    @Override
    public boolean isDone() {
        return count == ticks;
    }

    @Override
    public void finish() {
        if (onFinish != null) onFinish.run();
    }

    public int getCount() {
        return count;
    }

    public boolean isFinished() {
        return count >= ticks;
    }

    public int getRemainingTicks() {
        return ticks - count;
    }

    public int getTicks() {
        return ticks;
    }

    public double getRemainingTime() {
        return ((double) (ticks - count))/20;
    }

    public double getTotalTime() {
        return ((double) ticks/20);
    }
}
