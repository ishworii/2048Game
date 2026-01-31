package ui;
import core.GameLogic;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;

public class GameWindow extends JFrame {
    public GameWindow(GameLogic game){
        //basic window properties
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //game panel
        GamePanel panel = new GamePanel(game);
        add(panel);

        //Auto-play
        JButton autoPlayBtn = new JButton("Start Auto-Play");
        autoPlayBtn.setFocusable(false);
        autoPlayBtn.addActionListener(e -> {
            panel.toggleAutoPlay();
            autoPlayBtn.setText(autoPlayBtn.getText().equals("Start Auto-Play") ? "Stop" : "Start Auto-Play");
        });
        add(autoPlayBtn, BorderLayout.SOUTH);

        //finalize the window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
