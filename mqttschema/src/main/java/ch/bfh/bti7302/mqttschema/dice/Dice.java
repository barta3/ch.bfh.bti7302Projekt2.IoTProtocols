package ch.bfh.bti7302.mqttschema.dice;

import java.util.Random;

public class Dice {
    
    private Random rand = new Random();
    
    private DiceConfig config = new DiceConfig();
    private static Dice instance = new Dice();
    
    private Dice() {
        
    }
    
    public static Dice getInstance() {
        if (instance == null) {
            instance = new Dice();
        }
        return instance;
    }
    
    public int generateNumber() {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((config.getMax() - config.getMin()) + 1) + config.getMin();
        return randomNum;
    }

    public DiceConfig getConfig() {
        return config;
    }
    
    

}
