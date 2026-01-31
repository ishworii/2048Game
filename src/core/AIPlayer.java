package core;


import java.util.List;

public class AIPlayer {
    private final GameLogic game;
    private final int MAX_DEPTH = 3;

    public AIPlayer(GameLogic game){
        this.game = game;
    }

    private double evaluate(Tile[][] board){
        // Snake pattern weights - encourages keeping high tiles in top-left corner
        double[][] weights = {
                {65536, 32768, 16384, 8192},
                {512, 1024, 2048, 4096},
                {256, 128, 64, 32},
                {2, 4, 8, 16}
        };

        double score = 0;
        int emptyCount = 0;
        int maxTile = 0;

        // Weighted position score
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                int val = board[i][j].getValue();
                if(val > 0) {
                    score += val * weights[i][j];
                    maxTile = Math.max(maxTile, val);
                } else {
                    emptyCount++;
                }
            }
        }

        // Empty cells bonus (more empty = better)
        double emptyBonus = emptyCount * emptyCount * 1000;

        // Monotonicity bonus
        double monotonicity = calculateMonotonicity(board);

        // Smoothness bonus (similar adjacent tiles)
        double smoothness = calculateSmoothness(board);

        // Keep max tile in corner
        double cornerBonus = 0;
        if (board[0][0].getValue() == maxTile) cornerBonus = maxTile * 10;

        return score + emptyBonus + monotonicity * 100 + smoothness * 10 + cornerBonus;
    }

    private double calculateMonotonicity(Tile[][] board) {
        double monotonicity = 0;

        // Check rows
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int curr = board[i][j].getValue();
                int next = board[i][j + 1].getValue();
                if (curr > 0 && next > 0) {
                    if (curr >= next) monotonicity += 1;
                    else monotonicity -= 1;
                }
            }
        }

        // Check columns
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                int curr = board[i][j].getValue();
                int next = board[i + 1][j].getValue();
                if (curr > 0 && next > 0) {
                    if (curr >= next) monotonicity += 1;
                    else monotonicity -= 1;
                }
            }
        }

        return monotonicity;
    }

    private double calculateSmoothness(Tile[][] board) {
        double smoothness = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int val = board[i][j].getValue();
                if (val > 0) {
                    // Check right neighbor
                    if (j < 3 && board[i][j + 1].getValue() > 0) {
                        smoothness -= Math.abs(val - board[i][j + 1].getValue());
                    }
                    // Check down neighbor
                    if (i < 3 && board[i + 1][j].getValue() > 0) {
                        smoothness -= Math.abs(val - board[i + 1][j].getValue());
                    }
                }
            }
        }

        return smoothness;
    }


    public String getBestMove() {
        double bestScore = -1;
        String bestMove = "W";

        for (String move : new String[]{"W", "A", "S", "D"}) {
            // Use the deep copy method directly from your engine
            Tile[][] sandbox = game.getBoardCopy();

            if (game.simulateMove(sandbox, move)) {
                // Start the recursion
                double score = expectimax(sandbox, MAX_DEPTH - 1, false);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    private Tile[][] clone(Tile[][] board){
        Tile[][] copy = new Tile[4][4];
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0; j < 4; j++){
                copy[i][j] = new Tile();
                copy[i][j].setValue(board[i][j].getValue());
            }
        }
        return copy;
    }

    public double expectimax(Tile[][] board, int depth,boolean isPlayer){
        if(depth == 0 || game.isGameOver()){
            return evaluate(board);
        }
        if(isPlayer){
            double maxScore = -Double.MAX_VALUE;
            for(String move : new String[]{"W","S","A","D"}){
                Tile[][] copy = clone(board);
                if(game.simulateMove(copy,move)){
                    maxScore = Math.max(maxScore,expectimax(copy,depth- 1,false));
                }
            }
            return maxScore == -Double.MAX_VALUE ? evaluate(board) : maxScore;
        }else{

            List<int[]> emptyCells = game.getEmptyCells(board);
            if(emptyCells.isEmpty()) return evaluate(board);

            double totalScore = 0;
            for(int[] cell : emptyCells){
                int r = cell[0];
                int c = cell[1];

                // simulate 90 % probability of getting 2
                board[r][c].setValue(2);
                totalScore += 0.9 * expectimax(board,depth - 1,true);

                // simulate 10 % probability of getting 4
                board[r][c].setValue(4);
                totalScore += 0.1 * expectimax(board,depth-1,true);

                board[r][c].setValue(0);
            }
            return totalScore / emptyCells.size();
        }
    }


}
