package ui;

import core.AIPlayer;
import core.GameLogic;
import core.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameLogic game;
    private final AIPlayer ai;
    private static final int TILE_SIZE  = 120;
    private static final int MARGIN = 16;
    private static final int HEADER_HEIGHT = 80;
    private static final int ANIMATION_DURATION = 200; // milliseconds

    private boolean isAnimating = false;
    private boolean gameOver = false;
    private final List<AnimatedTile> animatedTiles = new ArrayList<>();
    private long animationStartTime;
    private int[][] boardBeforeMove;
    private int[][] boardAfterMove;
    private Timer autoPlayTimer;

    private static class AnimatedTile {
        int value;
        double startRow, startCol;
        int endRow, endCol;
        boolean isMerging;
        boolean isNew;

        AnimatedTile(int value, double startRow, double startCol, int endRow, int endCol, boolean isMerging) {
            this.value = value;
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            this.isMerging = isMerging;
            this.isNew = false;
        }
    }

    public GamePanel(GameLogic game){
        this.game = game;
        this.ai = new AIPlayer(game);

        int boardSize = (4 * TILE_SIZE) + (5 * MARGIN);
        int width = boardSize + (2 * MARGIN);
        int height = boardSize + HEADER_HEIGHT + (2 * MARGIN);
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(250, 248, 239));
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (isAnimating) return; // Ignore input during animation

                int[][] oldBoard = captureBoard();
                boolean moved = false;

                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP -> moved = game.moveUp();
                    case java.awt.event.KeyEvent.VK_DOWN -> moved = game.moveDown();
                    case java.awt.event.KeyEvent.VK_LEFT -> moved = game.moveLeft();
                    case java.awt.event.KeyEvent.VK_RIGHT -> moved = game.moveRight();
                }

                if (moved) {
                    int[][] newBoard = captureBoard();
                    setupAnimation(oldBoard, newBoard);
                    startAnimation();
                }
            }
        });
        setupAutoPlay();
    }

    private int[][] captureBoard() {
        int[][] board = new int[4][4];
        Tile[][] gameBoard = game.getBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = gameBoard[i][j].getValue();
            }
        }
        return board;
    }

    private void setupAnimation(int[][] oldBoard, int[][] newBoard) {
        animatedTiles.clear();
        boardBeforeMove = oldBoard;
        boardAfterMove = newBoard;

        // Create animations for all non-empty tiles in the old board
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int value = oldBoard[r][c];
                if (value != 0) {
                    // Find where this tile ended up
                    Point newPos = findTileDestination(oldBoard, newBoard, r, c);
                    if (newPos != null) {
                        boolean isMerging = (newBoard[newPos.y][newPos.x] == value * 2);
                        animatedTiles.add(new AnimatedTile(value, r, c, newPos.y, newPos.x, isMerging));
                    }
                }
            }
        }
    }

    private Point findTileDestination(int[][] oldBoard, int[][] newBoard, int oldRow, int oldCol) {
        int value = oldBoard[oldRow][oldCol];

        // First, check if the tile stayed in place
        if (newBoard[oldRow][oldCol] == value) {
            return new Point(oldCol, oldRow);
        }

        // Look in the direction of movement for the tile or a merge
        // Check same row (left/right movement)
        for (int c = 0; c < 4; c++) {
            if (newBoard[oldRow][c] == value || newBoard[oldRow][c] == value * 2) {
                return new Point(c, oldRow);
            }
        }

        // Check same column (up/down movement)
        for (int r = 0; r < 4; r++) {
            if (newBoard[r][oldCol] == value || newBoard[r][oldCol] == value * 2) {
                return new Point(oldCol, r);
            }
        }

        return null;
    }

    private void startAnimation() {
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();

        Timer timer = new Timer(16, null); // ~60 FPS
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - animationStartTime;

            if (elapsed >= ANIMATION_DURATION) {
                timer.stop();
                isAnimating = false;
                game.spawn();

                if (game.isGameOver()) {
                    gameOver = true;
                }

                repaint();
            } else {
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        drawHeader(g2);
        drawBoard(g2);

        if (gameOver) {
            drawGameOver(g2);
        }
    }

    private void drawHeader(Graphics2D g2) {
        // Draw "2048" title
        g2.setFont(new Font("SansSerif", Font.BOLD, 48));
        g2.setColor(new Color(119, 110, 101));
        g2.drawString("2048", MARGIN, 55);

        // Draw score box
        int scoreBoxWidth = 120;
        int scoreBoxHeight = 50;
        int scoreBoxX = getWidth() - scoreBoxWidth - MARGIN;
        int scoreBoxY = 15;

        g2.setColor(new Color(187, 173, 160));
        g2.fillRoundRect(scoreBoxX, scoreBoxY, scoreBoxWidth, scoreBoxHeight, 8, 8);

        // Draw "SCORE" label
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(238, 228, 218));
        String scoreLabel = "SCORE";
        FontMetrics fm1 = g2.getFontMetrics();
        int labelWidth = fm1.stringWidth(scoreLabel);
        g2.drawString(scoreLabel, scoreBoxX + (scoreBoxWidth - labelWidth) / 2, scoreBoxY + 18);

        // Draw score value
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String scoreText = String.valueOf(game.getScore());
        FontMetrics fm2 = g2.getFontMetrics();
        int scoreWidth = fm2.stringWidth(scoreText);
        g2.drawString(scoreText, scoreBoxX + (scoreBoxWidth - scoreWidth) / 2, scoreBoxY + 42);
    }

    private void drawGameOver(Graphics2D g2) {
        // Semi-transparent overlay
        int boardSize = (4 * TILE_SIZE) + (5 * MARGIN);
        g2.setColor(new Color(238, 228, 218, 200));
        g2.fillRoundRect(MARGIN, HEADER_HEIGHT + MARGIN, boardSize, boardSize, 8, 8);

        // Game Over text
        g2.setFont(new Font("SansSerif", Font.BOLD, 60));
        g2.setColor(new Color(119, 110, 101));
        String gameOverText = "Game Over!";
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        int centerX = (getWidth() - textWidth) / 2;
        int centerY = HEADER_HEIGHT + MARGIN + (boardSize / 2);
        g2.drawString(gameOverText, centerX, centerY);
    }

    private void drawBoard(Graphics2D g2){
        // Draw board background
        int boardSize = (4 * TILE_SIZE) + (5 * MARGIN);
        g2.setColor(new Color(187, 173, 160));
        g2.fillRoundRect(MARGIN, HEADER_HEIGHT + MARGIN, boardSize, boardSize, 8, 8);

        // Draw empty tile backgrounds
        for(int r = 0 ; r < 4 ; r++){
            for(int c = 0; c < 4 ; c++){
                int x = MARGIN + MARGIN + c * (TILE_SIZE + MARGIN);
                int y = HEADER_HEIGHT + MARGIN + MARGIN + r * (TILE_SIZE + MARGIN);
                g2.setColor(getBackgroundForValue(0));
                g2.fillRoundRect(x,y,TILE_SIZE,TILE_SIZE,15,15);
            }
        }

        if (isAnimating) {
            // Draw animated tiles at interpolated positions
            long elapsed = System.currentTimeMillis() - animationStartTime;
            double progress = Math.min(1.0, elapsed / (double) ANIMATION_DURATION);

            // Ease out quad for smoother, more natural animation
            progress = 1 - Math.pow(1 - progress, 2);

            for (AnimatedTile tile : animatedTiles) {
                double currentRow = tile.startRow + (tile.endRow - tile.startRow) * progress;
                double currentCol = tile.startCol + (tile.endCol - tile.startCol) * progress;

                int x = MARGIN + MARGIN + (int)Math.round(currentCol * (TILE_SIZE + MARGIN));
                int y = HEADER_HEIGHT + MARGIN + MARGIN + (int)Math.round(currentRow * (TILE_SIZE + MARGIN));

                // Show merged value at the end of animation
                int displayValue = tile.value;
                if (tile.isMerging && progress > 0.5) {
                    displayValue = tile.value * 2;
                }

                // Draw the tile with slight scale effect if merging
                if (tile.isMerging && progress > 0.5) {
                    double scaleProgress = (progress - 0.5) * 2; // 0 to 1 in second half
                    double scale = 1.0 + 0.1 * Math.sin(scaleProgress * Math.PI); // Pop effect

                    int scaledSize = (int)(TILE_SIZE * scale);
                    int offset = (TILE_SIZE - scaledSize) / 2;

                    g2.setColor(getBackgroundForValue(displayValue));
                    g2.fillRoundRect(x + offset, y + offset, scaledSize, scaledSize, 15, 15);
                    drawTileValue(g2, displayValue, x + offset, y + offset, scaledSize);
                } else {
                    g2.setColor(getBackgroundForValue(displayValue));
                    g2.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 15, 15);
                    drawTileValue(g2, displayValue, x, y);
                }
            }
        } else {
            // Draw static tiles
            Tile[][] board = game.getBoard();
            for(int r = 0 ; r < 4 ; r++){
                for(int c = 0; c < 4 ; c++){
                    Tile tile = board[r][c];
                    if(!tile.isEmpty()){
                        int x = MARGIN + MARGIN + c * (TILE_SIZE + MARGIN);
                        int y = HEADER_HEIGHT + MARGIN + MARGIN + r * (TILE_SIZE + MARGIN);

                        g2.setColor(getBackgroundForValue(tile.getValue()));
                        g2.fillRoundRect(x,y,TILE_SIZE,TILE_SIZE,15,15);
                        drawTileValue(g2,tile.getValue(),x,y);
                    }
                }
            }
        }
    }

    private void drawTileValue(Graphics2D g2, int value, int x, int y){
        drawTileValue(g2, value, x, y, TILE_SIZE);
    }

    private void drawTileValue(Graphics2D g2, int value, int x, int y, int tileSize){
        String s = String.valueOf(value);

        // Dynamic font size based on number of digits
        int baseFontSize;
        if (value < 100) {
            baseFontSize = 48;
        } else if (value < 1000) {
            baseFontSize = 42;
        } else if (value < 10000) {
            baseFontSize = 36;
        } else {
            baseFontSize = 30;
        }

        int fontSize = (int)(baseFontSize * (tileSize / (double)TILE_SIZE));
        g2.setFont(new Font("SansSerif",Font.BOLD, fontSize));
        g2.setColor(getForegroundForValue(value));

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(s);
        int textHeight = fm.getAscent();

        int textX = x + (tileSize - textWidth) / 2;
        int textY = y + (tileSize + textHeight) / 2 - 5;
        g2.drawString(s,textX,textY);
    }

    private Color getBackgroundForValue(int value) {
        return switch (value) {
            case 0    -> new Color(205, 193, 180);
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

    public void setupAutoPlay() {
        // 100 milliseconds
        autoPlayTimer = new Timer(100, e -> {
            if (game.isGameOver()) {
                autoPlayTimer.stop();
                gameOver = true;
                repaint();
                System.out.println("AI reached Game Over. Final Score: " + game.getScore());
                return;
            }

            String bestMove = ai.getBestMove();

            if (bestMove != null) {
                boolean moved = switch (bestMove) {
                    case "W" -> game.moveUp();
                    case "A" -> game.moveLeft();
                    case "S" -> game.moveDown();
                    case "D" -> game.moveRight();
                    default  -> false;
                };

                if (moved) {
                    game.spawn();
                    repaint();

                    if (game.isGameOver()) {
                        autoPlayTimer.stop();
                        gameOver = true;
                        repaint();
                        System.out.println("AI reached Game Over. Final Score: " + game.getScore());
                    }
                } else {
                    // No move was possible, check game over
                    if (game.isGameOver()) {
                        autoPlayTimer.stop();
                        gameOver = true;
                        repaint();
                        System.out.println("AI reached Game Over. Final Score: " + game.getScore());
                    }
                }
            }
        });
    }

    public void toggleAutoPlay() {
        if (autoPlayTimer.isRunning()) {
            autoPlayTimer.stop();
        } else {
            autoPlayTimer.start();
        }
    }

}
