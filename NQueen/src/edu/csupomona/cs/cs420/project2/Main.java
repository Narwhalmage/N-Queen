package edu.csupomona.cs.cs420.project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 18-Queen Problem solved using (SAHCA)Steepest Ascent Hill Climbing Algorithm 
 * or Genetic Algorithm. Also does analysis for SAHCA for more than 100 
 * instances.
 * @author Bevan Choy
 */
public class Main {
    
    //initialize random variable
    private static final Random rnd = new Random();
    
    private static long totalRunTime;
    private static long averageRunTime;
    private static int totalSearchCost;
    
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------
    /**
     * Genetic Class implements methods that will emulate the Genetic Algorithm.
     * Used to solve 18-Queen problem
     */
    public static class Genetic {
        
        //does the crossover between two random nodes by using a random point
        static Node[] crossOver(Node n1, Node n2) {
            final int BOARD_SIZE = n1.BOARD.length;
            final int crossOver = rnd.nextInt(BOARD_SIZE - 1);
            
            
            //crossover with 1 - 2
            int[] crossBoard1 = new int[BOARD_SIZE];
            System.arraycopy(n1.BOARD, 0, crossBoard1, 0, crossOver);
            System.arraycopy(n2.BOARD, crossOver, crossBoard1, crossOver, 
                    BOARD_SIZE-crossOver);
            Node n12 = new Node(crossBoard1);
            
            //crossover with 2 - 1
            int[] crossBoard2 = new int[BOARD_SIZE];
            System.arraycopy(n2.BOARD, 0, crossBoard2, 0, crossOver);
            System.arraycopy(n1.BOARD, crossOver, crossBoard2, crossOver, 
                    BOARD_SIZE-crossOver);
            Node n21 = new Node(crossBoard2);
            
            return new Node[] { n12, n21};
        }
        
        //will select the best 
        static List<Node> select(List<Node> nodes) {
            //List contains best elements
            Collections.sort(nodes, (Node n1, Node n2) -> {
                return n1.COST - n2.COST;
            });
            
            List<Node> select = new ArrayList<>();
            for (int j = 0; j <= nodes.size() / 2; j++) {
                select.add(nodes.get(j));
            }
            return select;
        }
        
        //does mutation on the node provided with chance to mutate also provided
        static Node mutation(Node n, double mutateChance) {
            if (mutateChance < rnd.nextDouble()) {
                return n;
            }
            int[] mutatedNodes = Arrays.copyOf(n.BOARD, n.BOARD.length);
            final int MUTATE = rnd.nextInt(mutatedNodes.length);
            mutatedNodes[MUTATE] = rnd.nextInt(mutatedNodes.length);
            
            return new Node(mutatedNodes);
        }
            
    }

//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------    
    /**
     * Solution class that holds the solution to a queen problem, with 
     * additional data measurements 
     */
    private static class Solution {
        final int TOTAL_MOVES;
        final int SEARCH_COST;
        final long START_TIME;
        final long END_TIME;
        final Node FINAL_STATE;
        
        //contructor with measurement variables
        Solution(int tm, int sc, long st, Node fs) {
            this.TOTAL_MOVES = tm;
            this.SEARCH_COST = sc;
            this.FINAL_STATE = fs;
            this.START_TIME = st;
            this.END_TIME = System.nanoTime();
        }
        
    }
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------    
    /**
     * Node class implements methods that will emulate a node used in a queen 
     * problem
     */
    public static class Node {
        //board simulating the chess board
        final int BOARD[];
        //cost of the board
        final int COST;

//------------------------------------------------------------------------------     
        //contructors for the Node class
        Node (int[] board, int cost) {
            this.BOARD = board;
            this.COST = cost;
        }        
        Node (int[] board) {
            this(board, numAttQueens(board));
        }
        
        

//------------------------------------------------------------------------------
        //returns # of attacking queens
        static int numAttQueens(final int [] BOARD) {
            int count = 0;
            for (int j = 0; j < BOARD.length; j++) {
                count += numAttQueensMaxIndex(BOARD, j);
            }
            return count;
        }
        int numAttQueens() {
            return numAttQueens(BOARD);
        }
        
//------------------------------------------------------------------------------     
        //returns # of attacking queens on a board with index indicating max
        //to check
        static int numAttQueensMaxIndex(final int[] BOARD, int i) {
            int count = 0;
            final int MAX = BOARD[i];
            for (int j = 0; j < i; j++) {
                if (BOARD[j] == MAX) {
                    count++;
                }
                if (Math.abs(BOARD[j] - MAX) == Math.abs(i - j)) {
                    count++;
                }
            }
            return count;
        }
        int numAttQueensMaxIndex(int i) {
            return numAttQueensMaxIndex(BOARD, i);
        }        
        
//------------------------------------------------------------------------------     
        //will generate successors for the steepest algorithm 
        List<Node> successorGenerator() {
            List<Node> successors = new ArrayList<>((BOARD.length * BOARD.length)
                    - BOARD.length);
            int [] copy;
            int [] board = Arrays.copyOf(BOARD, BOARD.length);
            int value;
            int cost;
            
            for (int j = 0; j < board.length; j++) {
                value = board[j];
                for (int k = 0; k < board.length; k++) {
                    if (k == value) {
                        continue;
                    }                    
                    cost = numAttQueens(board);
                    copy = Arrays.copyOf(board, board.length);
                    board[j] = k;
                    successors.add(new Node(copy, cost));
                }
                board[j] = value;
            }
            if (successors.size() != (BOARD.length * BOARD.length) - 
                    BOARD.length) {
                System.out.println("This is not right.");
            }
            
            Collections.sort(successors, (Node n1, Node n2) ->  {
                return n1.COST - n2.COST;
            });
            return successors;
        }
        
//------------------------------------------------------------------------------     
        //returns a string to represent the node
        //board path
        @Override
        public String toString() {
            //used for easier input
            StringBuilder strB = new StringBuilder();
            //layout of the board
            for (int j = 0; j < BOARD.length; j++) {
                for (int k = 0; k < BOARD.length; k++) {
                    if (BOARD[j] == k) {
                        strB.append('Q');
                    } else {
                        strB.append(',');
                    }
                    strB.append(' ');
                }
                //make room for the next line
                strB.append('\n');
            }
            strB.deleteCharAt(strB.length() - 1);
            return strB.toString();
        }
        
        
    }
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------    
    //Method that starts it all
    public static void main(String[] args) {
        //board set to 18; Queens set to 18
        final int BOARD_SIZE = 18;
        final int ITERATIONS;
        
        Scanner kb = new Scanner(System.in);
        int choice;
        boolean ans = false;
        
        //menu
        System.out.println("I am glad to see you. Shall we get started?");
        System.out.println("we will now solve the 18-Queen problem.");
        System.out.println("We'll begin the Straight-forward steepest-ascent" +
                "hill climbing algorithm");
        
        System.out.format("How many 18-Queen problems will be generated? (" +
                "n > 100 is ideal) \n" + ">>> ");

        ITERATIONS = kb.nextInt();
        
        System.out.format("Which program do you want to run first? \n" + 
                "\t 1) steepest-ascent hill-climbing algorithm \n" +
                "\t 2) genetic algorithm \n" + ">>> ");
        choice = kb.nextInt();
        do {
                   
        if (choice == 1) {
            ans = true;
            System.out.println("STEEEEEEEEP!!! \n");
            //initialize board
            int[] board;
            Solution s; 
            
            ArrayList<Integer> solutions = new ArrayList();
            double n = 0; 
            //# of moves used to solve solution
            double m1 = 0;
            //# of moves used to solve solution, but failed
            double m2 = 0;
            System.out.format("%-16s %-16s %-16s %-16s %-16s%n", "Test", 
                    "Attacking Queens", "moves", "search cost", "time");
//            writer.write(String.format("%s\t%s\t%s\t%s\t%s%n", "Test", 
//                    "attacking_Queens", "moves", "search_cost", "time"); 
            
            for (int j = 0; j < ITERATIONS; j++) {
                board = randomBoardGenerator(BOARD_SIZE);
                s = startSteepestAlg(board);
                long elapsedTime = 
                        TimeUnit.NANOSECONDS.toNanos(s.END_TIME-s.START_TIME);
                totalRunTime += elapsedTime;
                System.out.format("%-16s %-16s %-16s %-16s %-16s%n", +  
                        j + 1, s.FINAL_STATE.COST, s.TOTAL_MOVES, s.SEARCH_COST,
                        TimeUnit.NANOSECONDS.toNanos(s.END_TIME-s.START_TIME));
                totalSearchCost += s.SEARCH_COST;
                
                //solved problems
                if (s.FINAL_STATE.COST == 0) {
                    n++;
                    m1 += s.TOTAL_MOVES;
                    solutions.add(j + 1);
                //unsolved problems (i.e. reached local max)    
                } else {
                    m2 += s.TOTAL_MOVES;
                }
            }
            averageRunTime = totalRunTime / ITERATIONS;
            System.out.println("Problems solved: " + (n / ITERATIONS) * 100 +
                     "%");
            System.out.println("Average search cost: " + 
                    (totalSearchCost / ITERATIONS));
            System.out.format("Average # of moves: %.0f%n",   
                    (m1 / n));
            System.out.format("Average # of moves (no solution): %.0f%n", 
                    (m2 /(ITERATIONS - n)));
            System.out.println("Average Runtime (nanoseconds): " + 
                    averageRunTime);
            System.out.println("All solutions by Test #: " + 
                    Arrays.toString(solutions.toArray(new Integer[0])));
            
            
        } else if (choice == 2) {
            ans = true;
            System.out.println("GENERATION!!! \n");
            System.out.format("How many elements do you want with each " +
                    "generation? \n" + ">>> ");
            final int ELEMENTS_IN_GENERATION = kb.nextInt();
            System.out.format("What will be the mutation modifier/chance" +
                    " (0.0 or 1.0)? \n" + ">>> ");
            final double MUTATION_MOD = kb.nextDouble();
            
            Solution s;
            for (int j = 0; j < ITERATIONS; j++) {
                List<Node> initStates = new ArrayList(ELEMENTS_IN_GENERATION);
                
                for (int k = 0; k < ELEMENTS_IN_GENERATION; k++) {
                    initStates.add(new Node(randomBoardGenerator(BOARD_SIZE)));
                }
                
                s = startGeneticAlg(initStates, MUTATION_MOD);
                long elapsedTime = 
                        TimeUnit.NANOSECONDS.toNanos(s.END_TIME-s.START_TIME);
                totalRunTime += elapsedTime;
                        TimeUnit.NANOSECONDS.toNanos(s.END_TIME-s.START_TIME); 
                System.out.println("\nIterate #" + (j+1));
                System.out.println(s.FINAL_STATE);
                System.out.println("Time Passed: " +
                        elapsedTime);                
                System.out.format("Generated nodes: %d %n", s.TOTAL_MOVES);
            }
            averageRunTime = totalRunTime / ITERATIONS;
            System.out.println("Average Runtime (nanoseconds)= " + 
                    averageRunTime);
            
        }
        
        } while (ans != true);
        kb.close();
        
        
    }
    
//------------------------------------------------------------------------------
    //method returns a board of size 18 generating queens in a random position 
    //(initially assigned diagonally, then swapped at each position randomly
    public static int[] randomBoardGenerator(final int SIZE) {
        int[] board = new int[SIZE];
        for (int j = 0; j < board.length; j++) {
            board[j] = rnd.nextInt(board.length);
        }
        return board;
    }
    
//------------------------------------------------------------------------------
    //method that runs the steepest algorithm with values from the parameter.
    //progress with best closest node
    public static Solution startSteepestAlg(int[] initS) {
        Node neighbor;
        List<Node> neighborNodes;
        Node currentNode = new Node(initS);
        if (currentNode.COST == 0) {
            System.out.println("????? Initial state is the goal state. \n" +
                    currentNode + "\n");
            return new Solution(0, 0, System.nanoTime(), currentNode);
        }
        // Search cost = # of nodes generated
        int searchCost = 0;        
        int currentMoves = 0;
        final long START_TIME = System.nanoTime();
        while (true) {
            //show board TEST
//            if (showBoardPath) {
//                System.out.println("Move " + currentMoves);
//                System.out.println(currentNode + "\n");
//            }
            neighborNodes = currentNode.successorGenerator();
            searchCost += neighborNodes.size();
            neighbor = neighborNodes.get(0);
            
            if (currentNode.COST <= neighbor.COST) {
                return new Solution(currentMoves, searchCost, 
                        START_TIME, currentNode);
            }
            currentNode = neighbor;
            currentMoves++;
        }
    }
    
//------------------------------------------------------------------------------
    //method that runs the genetic algorithm with values from the parameter.
    //continues to run until a solution is found
    public static Solution startGeneticAlg(List<Node> iStates, double mut) {
        //number of generations done
        int numGenerations = 0;
        final long START_TIME = System.nanoTime();
        
        Node highestFitnessFunction; 
        Node[] crossOver;
        List<Node> crossedOver = new ArrayList(iStates.size());
        List<Node> bestCurrentGeneration;
        List<Node> nextGeneration = iStates;
        
        while (true) {
            bestCurrentGeneration = Genetic.select(nextGeneration);
            //Go through best list from the current generation to get solution
            for (Node n : bestCurrentGeneration) {
                if (n.COST == 0) {
                    return new Solution(numGenerations, 0, START_TIME, n);                    
                }                
            }
            //clear the crossed over list of nodes
            crossedOver.clear();
            highestFitnessFunction = bestCurrentGeneration.get(0);
            //loop through best list 
            for (int j = 1; j < bestCurrentGeneration.size(); j++) {
                crossOver = Genetic.crossOver(highestFitnessFunction, 
                        bestCurrentGeneration.get(j));
                crossedOver.add(crossOver[0]);
                crossedOver.add(crossOver[1]);
            }
            //clean up list 
            nextGeneration.clear();
            for (Node n : crossedOver) {
                nextGeneration.add(Genetic.mutation(n, mut));
            }
            numGenerations++;
        }
         
    }
//------------------------------------------------------------------------------
    
    // SEARCH COST = # of nodes generated
    
    
}
