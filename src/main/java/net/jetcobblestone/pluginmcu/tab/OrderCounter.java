package net.jetcobblestone.pluginmcu.tab;

public class OrderCounter {

    private int count = 0;
    private final int size;

    public OrderCounter(int size) {
        this.size = size;
    }

    public String getAndInc() {
        final String ret = get();
        count++;
        return ret;
    }

    public String get() {
        final StringBuilder ret = new StringBuilder(Integer.toString(count));
        final int dif = size - ret.length();
        for (int i = 0; i < dif; i++) {
            ret.insert(0, "0");
        }
        return ret.toString();
    }

    public void inc(int i) {
        count += i;
    }
}
