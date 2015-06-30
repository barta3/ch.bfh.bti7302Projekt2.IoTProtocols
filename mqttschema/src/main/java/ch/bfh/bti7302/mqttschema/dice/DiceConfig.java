package ch.bfh.bti7302.mqttschema.dice;

public class DiceConfig {
    private int interval = 10000;
    private int min = 1;
    private int max = 6;
    
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
