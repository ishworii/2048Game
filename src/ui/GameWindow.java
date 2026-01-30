package ui;
import core.GameLogic;

import javax.swing.JFrame;

public class GameWindow extends JFrame {
    public GameWindow(GameLogic game){
        //basic window properties
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //game panel
        GamePanel panel = new GamePanel(game);
        add(panel);

        //finalize the window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
