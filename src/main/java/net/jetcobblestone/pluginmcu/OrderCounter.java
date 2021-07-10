package net.jetcobblestone.pluginmcu;

public class OrderCounter {

    private int count = 0;
    private final int size;

    public OrderCounter(int size) {
        this.size = size;
    }

    public String nextInt() {
        final StringBuilder ret = new StringBuilder(Integer.toString(count));
        final int dif = size - ret.length();
        for (int i = 0; i < dif; i++) {
            ret.insert(0, "0");
        }
        count++;
        return ret.toString();
    }
}
