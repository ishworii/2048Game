import core.GameLogic;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameLogic game = new GameLogic();
        Scanner scanner = new Scanner(System.in);

        // Initial setup
        game.spawn();
        game.spawn();

        while (true) {
            game.printBoard();
            System.out.print("Move (W/A/S/D) or Q to quit: ");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("Q")) break;

            boolean moved = false;
            switch (input) {
                case "W" -> moved = game.moveUp();
                case "A" -> moved = game.moveLeft();
                case "S" -> moved = game.moveDown();
                case "D" -> moved = game.moveRight();
                default  -> System.out.println("Use W, A, S, or D!");
            }

            if (moved) {
                game.spawn();
            } else {
                System.out.println("--- Move blocked! ---");
            }
        }
        System.out.println("Thanks for playing!");
    }
}