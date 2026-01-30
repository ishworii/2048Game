package core;

import java.util.ArrayList;

public class GameLogic {
    private Tile[][] board =  new Tile[4][4];

    public GameLogic(){
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0 ; j < 4 ; j++){
                this.board[i][j] = new Tile();
            }
        }
    }

    public void setBoard(int[][] boardArray) {
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0; j <  4; j++){
                this.board[i][j].setValue(boardArray[i][j]);
            }
        }
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

    public boolean leftShift(){
        boolean moved = false;
        for (int row = 0 ; row < 4 ; row++) {
            int write = 0;
            for (int i = 0; i < this.board[row].length; i++) {
                if (!this.board[row][i].isEmpty()) {
                    this.board[row][write].setValue(this.board[row][i].getValue());
                    if (write != i) {
                        this.board[row][i].setValue(0);
                    }
                    moved = true;
                    write++;
                }
            }
        }
        return moved;
    }

    public boolean merge(){
        boolean mergedAny = false;
        for (int row = 0 ; row < 4 ; row++) {
            for (int i = 0; i < this.board[row].length - 1; i++) {
                var current = this.board[row][i];
                var next = this.board[row][i+1];
                if (current.getValue() == next.getValue() && !current.isMerged() && !next.isMerged()){
                    current.setValue(current.getValue() + next.getValue());
                    current.setMerged(true);
                    next.setValue(0);
                    mergedAny  = true;
                }
            }
        }
        return mergedAny;
    }

    private void resetMergeFlags(){
        for(int i = 0; i < 4; i++){
            for(int j = 0 ; j < 4 ; j++){
                this.board[i][j].setMerged(false);
            }
        }
    }

    private void reverseRows() {
        for (int i = 0; i < 4; i++) {
            int left = 0;
            int right = 3;
            while (left < right) {
                Tile temp = board[i][left];
                board[i][left] = board[i][right];
                board[i][right] = temp;
                left++;
                right--;
            }
        }
    }

    public void transpose() {
        int n = this.board.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                var temp = this.board[i][j];
                this.board[i][j] = this.board[j][i];
                this.board[j][i] = temp;
            }
        }
    }

    public boolean moveLeft(){
        this.resetMergeFlags();
        boolean s1 = this.leftShift();
        boolean m = this.merge();
        boolean s2 = this.leftShift();
        return s1 || m || s2;
    }


    public  boolean moveRight(){
        //rotate
        this.reverseRows();
        //move left
        boolean moved = this.moveLeft();
        //rotate again
        this.reverseRows();
        return moved;

    }

    public boolean moveUp(){
        //transpose
        this.transpose();
        boolean moved = this.moveLeft();
        this.transpose();
        return moved;

    }

    public boolean moveDown(){
        //transpose
        this.transpose();
        //rotate
        this.reverseRows();
        boolean moved = this.moveLeft();
        //rotate
        this.reverseRows();
        //transpose
        this.transpose();
        return moved;
    }
}
