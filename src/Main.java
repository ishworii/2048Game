import core.GameLogic;

public class Main {
    public static void main(String[] args) {
       var game = new GameLogic() ;
       game.spawn();
       game.spawn();
       game.printBoard();
    }
}