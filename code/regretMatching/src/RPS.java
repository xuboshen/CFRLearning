import java.util.Random;
import java.util.Arrays;
public class RPS {
    public static void main(String[] args){
        RPSTrainer trainer = new RPSTrainer();
        trainer.train(1_000_000);
    }
}

class RPSTrainer{
    public static final int NUM_ACTIONS = 3;
    public static final int ROCK = 0, PAPER = 1, SCISSORS = 2;
    public static final Random random = new Random();
    public static final double[] oppStrategy = {0.4, 0.3, 0.3};
    public static final String[] name = {"ROCK", "PAPER", "SCISSORS"};

    private int[] regretSum = new int[NUM_ACTIONS];
    private double[] strategy = new double[NUM_ACTIONS];
    private double[] strategySum = new double[NUM_ACTIONS];
    // get other players' actions
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
    // get my strategy according to regretSum
    public double[] getStrategy(){
        double normalizingSum = 0;
        
        for (int i = 0; i < NUM_ACTIONS; ++i){
            this.strategy[i] = (regretSum[i] > 0) ? regretSum[i] : 0;
            normalizingSum += this.strategy[i];
        }        

        for (int i = 0; i < NUM_ACTIONS; ++i){
            if (normalizingSum > 0)
                this.strategy[i] /= normalizingSum;
            else
                this.strategy[i] = 1.0 / NUM_ACTIONS;
            this.strategySum[i] += this.strategy[i];
        }

        return this.strategy;
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
    // training step
    public void train(int iterations){
        double[] actionUtilities = new double[NUM_ACTIONS];

        for (int i = 0; i < iterations; ++i){
            // get regret-matched mixed-strategy actions
            double[] myStrategy = getStrategy();
            int myAction = getAction(myStrategy);
            int oppAction = getAction(oppStrategy);
            // calculate utilities of my varying actions regarding to fixed oppActions.
            actionUtilities[oppAction] = 0;
            actionUtilities[(oppAction == NUM_ACTIONS - 1) ? 0 : oppAction + 1] = 1; // if oppAction is scissors, then rock can get positive regrets
            actionUtilities[(oppAction == 0) ? NUM_ACTIONS - 1 : oppAction - 1] = -1;
            //accumulate regrets, u(s') - u(s)
            for (int j = 0; j < NUM_ACTIONS; ++j){
                regretSum[j] += actionUtilities[j] - actionUtilities[myAction];
            }
            if (i == iterations - 1){
                System.out.println("Last step:");
                System.out.printf("      oppAction:%s", name[oppAction]);
                System.out.println();
                System.out.printf("      myAction:%s", name[myAction]);
                System.out.println();
                displayResults();
            }
            if (i == 0){
                System.out.println("first step");
                System.out.printf("      oppAction:%s", name[oppAction]);
                System.out.println();
                System.out.printf("      myAction:%s", name[myAction]);
                System.out.println();
                displayResults();
            }
        }

        return;
    }
    // display results
    public void displayResults(){
        System.out.print("      myStrategy:");
        System.out.println(Arrays.toString(getStrategy()));
        System.out.print("      regretSum:");
        System.out.println(Arrays.toString(regretSum));
        System.out.print("      strategySum:");
        System.out.println(Arrays.toString(strategySum));
        System.out.print("      averageStrategy:");
        System.out.println(Arrays.toString(getAverageStrategy()));
    }
}
