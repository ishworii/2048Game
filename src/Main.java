import core.GameLogic;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameLogic game = new GameLogic();
        Scanner scanner = new Scanner(System.in);

        // Initial setup: 2048 always starts with two tiles
        game.spawn();
        game.spawn();

        System.out.println("--- WELCOME TO 2048 (CONSOLE EDITION) ---");

        while (true) {
            game.printBoard();

            // CRITICAL: Check for Game Over at the start of every turn.
            // If the board is full and no merges are possible, the loop breaks.
            if (game.isGameOver()) {
                System.out.println("\n*******************************");
                System.out.println("   GAME OVER: NO MOVES LEFT!   ");
                System.out.println("*******************************");
                break;
            }

            System.out.print("Move (W=Up, A=Left, S=Down, D=Right) or Q to Quit: ");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("Q")) {
                System.out.println("Exiting game...");
                break;
            }

            boolean moved = false;
            switch (input) {
                case "W" -> moved = game.moveUp();
                case "A" -> moved = game.moveLeft();
                case "S" -> moved = game.moveDown();
                case "D" -> moved = game.moveRight();
                default  -> {
                    System.out.println("Invalid input! Use W, A, S, or D.");
                    continue; // Skip the rest of the loop and ask for input again
                }
            }

            // A new tile ONLY spawns if the board actually changed
            if (moved) {
                game.spawn();
            } else {
                System.out.println("!! Move blocked: Try a different direction !!");
            }
        }

        scanner.close();
        System.out.println("Final Board state shown above. Thanks for playing!");
    }
}