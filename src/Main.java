import core.GameLogic;

public class Main {
    public static void main(String[] args) {
       var game = new GameLogic() ;
//       game.spawn();
//       game.spawn();
        game.setBoard(new int[][] {
                {2, 0, 2, 2},
                {0, 0, 0, 2},
                {0, 0, 0, 0},
                {2, 2, 2, 2}
        });
       game.printBoard();
//       System.out.println();
//       game.moveLeft();
//       game.printBoard();
//        System.out.println();
//       game.moveLeft();
//       game.printBoard();
        System.out.println();
//        game.moveRight();
//        game.printBoard();
        game.moveDown();
        game.printBoard();
    }
}