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
}
