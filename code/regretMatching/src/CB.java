import java.util.Random;
import java.util.Arrays;

public class CB{
    public static void main(String[] args){
        Commander Colonel = new Commander("Colonel");
        Commander Boba = new Commander("Boba");
        Colonel.displayResults();
        Boba.displayResults();
        // Trainer trainer = new Trainer();
        train(100_000_000, Colonel, Boba);
        Colonel.displayResults();
        Boba.displayResults();
    }
    public static void train(int iteration, Commander C, Commander B){
        final int NUM_ACTIONS = 21;
        // int[] actionUtilitiesC = new int[NUM_ACTIONS];
        // int[] actionUtilitiesB = new int[NUM_ACTIONS];
        for (int i = 0; i < iteration; ++i){
            double[] strategyC = C.getStrategy();
            double[] strategyB = B.getStrategy();
            int actionC = C.getAction(strategyC);
            int actionB = B.getAction(strategyB);
            // renew utilities
            C.renewUtilities(actionB);
            B.renewUtilities(actionC);
            // renew regrets
            C.renewRegretSum(actionC);
            B.renewRegretSum(actionB);
        }
    }
}

class Commander{
    public final int NUM_ACTIONS = 21, N = 3;
    public final String[] action = {
        "005", "014", "023", "032", "041", 
        "050", "104", "113", "122", "131", 
        "140", "203", "212", "221", "230", 
        "302", "311", "320", "401", "410", 
        "500", 
    };
    private double[] actionUtilities = new double[NUM_ACTIONS];
    private double[] strategy = new double[NUM_ACTIONS];
    private double[] strategySum = new double[NUM_ACTIONS];
    private int[] regretSum = new int[NUM_ACTIONS];
    private Random random = new Random();
    private String name = new String();

    Commander(String name){
        this.name = name;
    }

    public int getAction(double[] strategy){
        int action = -1;
        double accumulation = 0;
        double r = random.nextDouble();

        while (action < NUM_ACTIONS && r >= accumulation){
            action++;
            accumulation += strategy[action];
        }

        return action;
    }
    
    public double[] getStrategy(){
        double normalizingSum = 0;

        for (int i = 0; i < NUM_ACTIONS; ++i){
            strategy[i] = (regretSum[i] > 0) ? regretSum[i] : 0;
            normalizingSum += strategy[i];
        }
        for (int i = 0; i < NUM_ACTIONS; ++i){
            if (normalizingSum > 0)
                strategy[i] /= normalizingSum;
            else
                strategy[i] = 1.0 / NUM_ACTIONS;
            strategySum[i] += strategy[i];   
        }

        return strategy;
    }

    public double[] getAverageStrategy(){
        double[] averageStrategy = new double[NUM_ACTIONS];
        double normalizingSum = 0;

        for (int i = 0; i < NUM_ACTIONS; ++i){
            normalizingSum += strategySum[i];
        }
        for (int i = 0; i < NUM_ACTIONS; ++i){
            if (normalizingSum > 0)
                averageStrategy[i] = strategySum[i] / normalizingSum;
            else
                averageStrategy[i] = 1.0 / NUM_ACTIONS;
        }

        return averageStrategy;
    }
    public void renewRegretSum(int myAction){
        for (int i = 0; i < NUM_ACTIONS; ++i){
            regretSum[i] += actionUtilities[i] - actionUtilities[myAction];
        }
    }
    public void renewUtilities(int oppAction){
        for (int i = 0; i < NUM_ACTIONS; ++i){
            actionUtilities[i] = calculateUtility(i, oppAction);
        }
    }
    private int calculateUtility(int myAction, int oppAction){
        int utility = 0;
        int cnt = 0;
        
        for (int i = 0; i < N; ++i){
            int t = action[myAction].charAt(i) - action[oppAction].charAt(i);
            if (t > 0)
                cnt++;
            else if (t < 0)
                cnt--;
        }
        if (cnt > 0)
            utility = 1;
        else if (cnt < 0)
            utility = -1;
        else
            utility = 0;

        return utility;
    }
    public void displayResults(){
        System.out.println(name);
        // System.out.print("      myStrategy:");
        // System.out.println(Arrays.toString(getStrategy()));
        // System.out.print("      regretSum:");
        // System.out.println(Arrays.toString(regretSum));
        // System.out.print("      strategySum:");
        // System.out.println(Arrays.toString(strategySum));
        System.out.println("      averageStrategy:");
        // System.out.println(Arrays.toString(getAverageStrategy()));
        double[] avgstrategy = getAverageStrategy();
        for (int i = 0; i < NUM_ACTIONS; ++i){
            if (Math.abs(avgstrategy[i]) < 1e-5)
                continue;
            System.out.print("                     "+action[i] + ":");
            System.out.println(avgstrategy[i]);
        }
        System.out.println();
    }
}