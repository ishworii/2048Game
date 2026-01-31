package core;

import java.util.ArrayList;

public class GameLogic {
    private final Tile[][] board =  new Tile[4][4];
    private int score = 0;

    public GameLogic(){
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0 ; j < 4 ; j++){
                this.board[i][j] = new Tile();
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void setBoard(int[][] boardArray) {
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0; j <  4; j++){
                this.board[i][j].setValue(boardArray[i][j]);
            }
        }
    }
    public Tile[][] getBoardCopy(){
        Tile[][] copy = new Tile[4][4];
        for(int i = 0; i < 4 ; i++){
            for(int j = 0 ; j < 4 ; j++){
                copy[i][j] = new Tile();
                copy[i][j].setValue(board[i][j].getValue());
            }
        }
        return copy;
    }

    public Tile[][] getBoard(){
        return board;
    }

    public void spawn(){
      ArrayList<Integer> emptyIndices = new ArrayList<Integer>();
      for (int i = 0 ; i < 16 ; i ++){
          if(this.board[i / 4][i % 4].isEmpty()){
              emptyIndices.add(i);
          }
      }
      if(!emptyIndices.isEmpty()){
          int randomIndex = (int) (Math.random() * emptyIndices.size());
          int boardIndex = emptyIndices.get(randomIndex);
          int value = Math.random() < 0.9 ? 2 : 4;
          board[boardIndex / 4][boardIndex % 4].setValue(value);
      }
    }

    public void printBoard() {
        System.out.println("-------------------------");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String val = board[i][j].isEmpty() ? "." : String.valueOf(board[i][j].getValue());
                System.out.printf("| %4s ", val);
            }
            System.out.println("|");
        }
        System.out.println("-------------------------");
    }

    public boolean leftShift(Tile[][] targetBoard){
        boolean moved = false;
        for (int row = 0 ; row < 4 ; row++) {
            int write = 0;
            for (int i = 0; i < targetBoard[row].length; i++) {
                if (!targetBoard[row][i].isEmpty()) {
                    if (write != i) {
                        targetBoard[row][write].setValue(targetBoard[row][i].getValue());
                        targetBoard[row][i].setValue(0);
                        moved = true;
                    }
                    write++;
                }
            }
        }
        return moved;
    }

    public boolean merge(Tile[][] targetBoard){
        boolean mergedAny = false;
        for (int row = 0 ; row < 4 ; row++) {
            for (int i = 0; i < targetBoard[row].length - 1; i++) {
                var current = targetBoard[row][i];
                var next = targetBoard[row][i+1];
                if (!current.isEmpty() && current.getValue() == next.getValue() && !current.isMerged() && !next.isMerged()){
                    int newValue = current.getValue() + next.getValue();
                    current.setValue(newValue);
                    current.setMerged(true);
                    next.setValue(0);
                    score += newValue;
                    mergedAny  = true;
                }
            }
        }
        return mergedAny;
    }

    private void resetMergeFlags(Tile[][] targetBoard){
        for(int i = 0; i < 4; i++){
            for(int j = 0 ; j < 4 ; j++){
                targetBoard[i][j].setMerged(false);
            }
        }
    }

    private void reverseRows(Tile[][] targetBoard) {
        for (int i = 0; i < 4; i++) {
            int left = 0;
            int right = 3;
            while (left < right) {
                Tile temp = targetBoard[i][left];
                targetBoard[i][left] = targetBoard[i][right];
                targetBoard[i][right] = temp;
                left++;
                right--;
            }
        }
    }

    public void transpose(Tile[][] targetBoard) {
        int n = targetBoard.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                var temp = targetBoard[i][j];
                targetBoard[i][j] = targetBoard[j][i];
                targetBoard[j][i] = temp;
            }
        }
    }

    public boolean moveLeft(Tile[][] targetBoard){
        this.resetMergeFlags(targetBoard);
        boolean s1 = this.leftShift(targetBoard);
        boolean m = this.merge(targetBoard);
        boolean s2 = this.leftShift(targetBoard);
        return s1 || m || s2;
    }

    public boolean moveLeft(){
        return moveLeft(this.board);
    }


    public  boolean moveRight(Tile[][] targetBoard){
        //rotate
        this.reverseRows(targetBoard);
        //move left
        boolean moved = this.moveLeft(targetBoard);
        //rotate again
        this.reverseRows(targetBoard);
        return moved;

    }

    public boolean moveRight(){
        return moveRight(this.board);
    }

    public boolean moveUp(Tile[][] targetBoard){
        //transpose
        this.transpose(targetBoard);
        boolean moved = this.moveLeft(targetBoard);
        this.transpose(targetBoard);
        return moved;

    }

    public boolean moveUp(){
        return moveUp(this.board);
    }

    public boolean moveDown(Tile[][] targetBoard){
        //transpose
        this.transpose(targetBoard);
        //rotate
        this.reverseRows(targetBoard);
        boolean moved = this.moveLeft(targetBoard);
        //rotate
        this.reverseRows(targetBoard);
        //transpose
        this.transpose(targetBoard);
        return moved;
    }

    public boolean moveDown(){
        return moveDown(this.board);
    }

    public boolean isGameOver() {
        // If there's an empty space, the game continues
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j].isEmpty()) return false;
            }
        }

        //  Check for horizontal merges
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getValue() == board[i][j + 1].getValue()) return false;
            }
        }

        // Check for vertical merges
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j].getValue() == board[i + 1][j].getValue()) return false;
            }
        }

        // Game Over
        return true;
    }
}
