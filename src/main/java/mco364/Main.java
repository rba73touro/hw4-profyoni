package mco364;

import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mco364.GameOfLife.XX;
import static mco364.GameOfLife.__;

public class Main {



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        clearConsole();

        GameOfLife life = new GameOfLife(9);
        life.seed(GameOfLife.beaconPattern);

        for (int i = 0; i < 10; i++) {
            System.out.println(life);
            life.updateToNextGenerationMT(7);
            sleep(1000);
            clearConsole();
        }
    }

    public final static void clearConsole() {
        for (int i = 0; i < 100; i++) { // safety net since next code only works on console not Netbeans output
            System.out.println();
        }
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
// ignore
        }
    }
}
