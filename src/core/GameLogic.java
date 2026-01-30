package core;

import java.util.ArrayList;
import java.util.Arrays;

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

    public void printBoard(){
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0; j < 4 ; j++){
                System.out.print(this.board[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public void leftShift(){
        for (int row = 0 ; row < 4 ; row++) {
            int write = 0;
            for (int i = 0; i < this.board[row].length; i++) {
                if (!this.board[row][i].isEmpty()) {
                    this.board[row][write].setValue(this.board[row][i].getValue());
                    if (write != i) {
                        this.board[row][i].setValue(0);
                    }
                    write++;
                }
            }
        }
    }

    public void merge(){
        for (int row = 0 ; row < 4 ; row++) {
            for (int i = 0; i < this.board[row].length - 1; i++) {
                var current = this.board[row][i];
                var next = this.board[row][i+1];
                if (current.getValue() == next.getValue() && !current.isMerged() && !next.isMerged()){
                    current.setValue(current.getValue() + next.getValue());
                    next.setValue(0);
                }
            }
        }
        this.leftShift();
    }
}
