package net.jetcobblestone.pluginmcu.util.annotationscheduling;

public interface ITickable {
    void tick();

    boolean isDone();

    void finish();

}
