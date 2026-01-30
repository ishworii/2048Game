package ui;

import core.GameLogic;
import core.Tile;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameLogic game;
    private static final int TILE_SIZE  = 100;
    private static final int MARGIN = 15;

    public GamePanel(GameLogic game){
        this.game = game;

        int size = (4 * TILE_SIZE) + (5 * MARGIN);
        setPreferredSize(new Dimension(size,size));
        setBackground(new Color(187,173,160));
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        drawBoard(g2);
    }

    private void drawBoard(Graphics2D g2){
        Tile[][] board = game.getBoard();
        for(int r = 0 ; r < 4 ; r++){
            for(int c = 0; c < 4 ; c++){
                Tile tile = board[r][c];

                int x = MARGIN + c * (TILE_SIZE + MARGIN);
                int y = MARGIN + r * (TILE_SIZE + MARGIN);

                g2.setBackground(getBackgroundForValue(tile.getValue()));
                g2.fillRoundRect(x,y,TILE_SIZE,TILE_SIZE,15,15);

                if(!tile.isEmpty()){
                    drawTileValue(g2,tile.getValue(),x,y);
                }
            }
        }
    }

    private void drawTileValue(Graphics2D g2, int value, int x, int y){
        String s = String.valueOf(value);
        g2.setFont(new Font("SansSerif",Font.BOLD,36));
        g2.setColor(getForegroundForValue(value));

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(s);
        int textHeight = fm.getAscent();

        int textX = x + (TILE_SIZE - textWidth) / 2;
        int textY = y + (TILE_SIZE + textHeight) / 2 - 5;
        g2.drawString(s,textX,textY);
    }

    private Color getBackgroundForValue(int value) {
        return switch (value) {
            case 0    -> new Color(0, 193, 180);
            case 2    -> new Color(0xeee4da);
            case 4    -> new Color(0xede0c8);
            case 8    -> new Color(0xf2b179);
            case 16   -> new Color(0xf59563);
            case 32   -> new Color(0xf67c5f);
            case 64   -> new Color(0xf65e3b);
            case 128  -> new Color(0xedcf72);
            case 256  -> new Color(0xedcc61);
            case 512  -> new Color(0xedc850);
            case 1024 -> new Color(0xedc53f);
            case 2048 -> new Color(0xedc22e);
            default   -> new Color(0xcdc1b4); // Empty tile color
        };
    }

    private Color getForegroundForValue(int value) {
        // For 2 and 4, use the classic dark grey
        if (value <= 4) {
            return new Color(0x776e65);
        }
        // For 8 and up, use the light off-white
        return new Color(0xf9f6f2);
    }

}
