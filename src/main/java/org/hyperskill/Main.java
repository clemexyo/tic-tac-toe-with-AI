package org.hyperskill;

import java.util.*;

class Main {

    private static final String INPUT_COMMAND_REGEX = "^start (easy|medium|hard|user) (easy|medium|hard|user)$";


    public static void main(String[] args) {
        // user input for game settings
        String inputCommand;
        do{
            Scanner scanner = new Scanner(System.in);
            System.out.print("Input command: ");
            inputCommand = scanner.nextLine();
            if("exit".equals(inputCommand)){
                continue;
            }
            if(!inputCommand.matches(INPUT_COMMAND_REGEX)){
                System.out.println("Bad parameters!");
                continue;
            }
            // generate players
            Switcher<Player> playerSwitcher = Switcher.generatePlayerSwitcher(inputCommand);

            // generate board
            List<List<Character>> board = Board.constructEmptyBoard();
            Board.printBoard(board);

            // start the current game loop
            IndexPoint generatedMove;
            do{
                Player currentPlayer = playerSwitcher.getValue();
                generatedMove = currentPlayer.generateMove(board);
                if(generatedMove != null) {
                    // correct move has been generated, continue to play it
                    int row = generatedMove.row();
                    int col = generatedMove.col();
                    board.get(row).set(col, currentPlayer.getMySymbol());
                    Board.printBoard(board);
                    String outcome = Board.determineOutcome(board);
                    if (outcome.contains("wins") || outcome.contains("Draw")) {
                        System.out.println(outcome);
                        break;
                    }
                    playerSwitcher.goNext();
                }
            }while (generatedMove != null); // current game loop - iterates for each round
        }while (!inputCommand.equals("exit")); // all games loop

    }

}