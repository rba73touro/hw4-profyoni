package mco364;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yrobi
 */
public class GameOfLife {

    public static final boolean XX = true, __ = false;

    private final int size;
    boolean board[][];

    boolean[][] boardCopy;

    GameOfLife() {
        this(20);
    }

    GameOfLife(int size) {
        this.size = size;
        board = new boolean[size][size];
        boardCopy = new boolean[size][size];
    }

    public void seed(boolean seed[][]) {
        for (int i = 0; i < seed.length; i++) {
            for (int j = 0; j < seed[i].length; j++) {
                board[i][j] = seed[i][j];
            }
        }
    }

    public int neighborCount(int row, int col) {
        int count = 0;
        int cMin = Math.max(0, col - 1), cMax = Math.min(size - 1, col + 1);
        int rMin = Math.max(0, row - 1), rMax = Math.min(size - 1, row + 1);
        for (int i = rMin; i <= rMax; i++) {
            for (int j = cMin; j <= cMax; j++) {
                if (board[i][j]
                        && !(i == row && j == col)) // ignore "center" cell
                {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isAliveNextGeneration(int row, int col) {
        int neighborCount = neighborCount(row, col);
        if (!board[row][col]) {
            return neighborCount == 3;
        }

        return (neighborCount == 2 || neighborCount == 3);
    }

    public void updateToNextGeneration() {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                setCellBoardCopy(i, j, isAliveNextGeneration(i, j));
            }
        }
        board = boardCopy;
    }

    class LifeThread implements Runnable {
        private int min;
        private int max;

        LifeThread(int min, int max) {
            this.min = min;
            this.max = max;

//        if (AppSettings.DEBUG) 
//        {
//            System.out.println(min + "," + max);
//        }
        }
        @Override
        public void run() {
            for (int i = min; i < max; i++) {
                int row = i / size;
                int col = i % size;

                boolean isAlive = isAliveNextGeneration(row, col);
                setCellBoardCopy(row, col, isAlive);
                //System.out.printf("[%d,%d] = [%s]%n", row, col, isAlive);
            }
        }
    }

    public void updateToNextGenerationMT(int threadCount) {
        ExecutorService ex = Executors.newFixedThreadPool(threadCount);
        double averageSizePerThread = (double) size * size / threadCount;
        ArrayList<LifeThread> threadList = new ArrayList<>();
        for (int threadNumber = 0; threadNumber < threadCount; threadNumber++) {
            int min = (int) (threadNumber * averageSizePerThread);
            int max = (int) ((threadNumber + 1) * averageSizePerThread);
            LifeThread t = new LifeThread(min, max);
            ex.execute(t);
        }
        ex.shutdown();
        try {
            ex.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ex1) {}
        
        boolean[][] temp = board;
        board = boardCopy;
        boardCopy = temp;        
    }

    private synchronized void setCellBoardCopy(int i, int j, boolean value) {
        boardCopy[i][j] = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(board[i][j] ? "X" : " ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    static final boolean[][] blinkerPattern = new boolean[][]{
        {__, __, __, __, __},
        {__, __, XX, __, __},
        {__, __, XX, __, __},
        {__, __, XX, __, __},
        {__, __, __, __, __},};

    static final boolean[][] toadPattern = new boolean[][]{
        {__, __, __, __, __, __},
        {__, __, __, __, __, __},
        {__, __, XX, XX, XX, __},
        {__, XX, XX, XX, __, __},
        {__, __, __, __, __, __},
        {__, __, __, __, __, __},};

    static final boolean[][] beaconPattern = new boolean[][]{
        {__, __, __, __, __, __},
        {__, XX, XX, __, __, __},
        {__, XX, XX, __, __, __},
        {__, __, __, XX, XX, __},
        {__, __, __, XX, XX, __},
        {__, __, __, __, __, __},};
}
