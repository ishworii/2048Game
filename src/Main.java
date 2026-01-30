import core.GameLogic;
import ui.GameWindow;

public class Main {
    public static void main(String[] args) {
        var game = new GameLogic();
        game.spawn();
        game.spawn();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameWindow(game);
        });
    }
}