package net.jetcobblestone.pluginmcu.util.annotationscheduling;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TickTask {

    private static TickTask instance;

    public static TickTask getInstance(ScheduleManager scheduleManager) {
        if (instance == null) {
            instance = new TickTask();
            scheduleManager.register(instance);
        }
        return instance;
    }

    private final List<ITickable> tickables = new LinkedList<>();
    private final List<ITickable> toAdd = new ArrayList<>();

    public void add(ITickable tickable) {
        this.toAdd.add(tickable);
    }

    @TickableMethod
    public void tickAll() {
        tickables.addAll(toAdd);
        toAdd.clear();

        this.tickables.removeIf(iTickable -> {
            if (iTickable.isDone()) {
                iTickable.finish();
                return true;
            }
            iTickable.tick();
            return false;
        });
    }
}
