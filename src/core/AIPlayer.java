package core;


import java.util.List;

public class AIPlayer {
    private final GameLogic game;
    private final int MAX_DEPTH = 3;

    public AIPlayer(GameLogic game){
        this.game = game;
    }

    private double evaluate(Tile[][] board){
        int[][] weights = {
                {100,80,70,60},
                {20,30,40,50},
                {15,12,10,8},
                {2,3,4,5}
        };
        double score = 0;
        int emptyCount = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                int val = board[i][j].getValue();
                score += val * weights[i][j];
                if(val == 0) emptyCount++;
            }
        }
        //high reward for keeping board open
        return score + (emptyCount * 100);
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
            double maxScore = 0;
            for(String move : new String[]{"W","S","A","D"}){
                Tile[][] copy = clone(board);
                if(game.simulateMove(copy,move)){
                    maxScore = Math.max(maxScore,expectimax(copy,depth- 1,false));
                }
            }
            return maxScore;
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

                return totalScore / emptyCells.size();
            }
        }
        //UNREACHABLE
        return 0.0;
    }


}
