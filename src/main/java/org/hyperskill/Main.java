package org.hyperskill;

import java.util.*;

class Player{
    private final Character moveSymbol;
    private final boolean isAI;
    private final String level;

    public Player(Character moveSymbol, String level) {
        this.moveSymbol = moveSymbol;
        this.level = level;
        if("easy".equals(level)){
            this.isAI = true;
        }
        else{
            this.isAI = false;
        }
    }

    public Character getMoveSymbol() {
        return moveSymbol;
    }

    public boolean isAI() {
        return this.isAI;
    }

    public String getLevel(){
        return this.level;
    }
}

class Switcher<T>{
    private final List<T> items;
    private int index = 0;

    public Switcher(List<T> items){
        this.items = items;
    }

    public T getValue() {
        return items.get(index);
    }

    public void goNext(){
        index = (index + 1) % items.size(); // Move to the next element cyclically
    }
}

class Main {
    private static final String correctFormat = "^[1-3] [1-3]$";
    private static final String outOfBoundsRegex = "[1-3 ]+";
    private static final String numbersRegex = "[0-9 ]+";
    private static final String inputCommandRegex = "^start [a-zA-Z]+ [a-zA-Z]+$";

    public static void main(String[] args) {
        // user input for game settings
        boolean correctCommand = false;
        String inputCommand;
        do{
            Scanner scanner = new Scanner(System.in);
            System.out.print("Input command: ");
            inputCommand = scanner.nextLine();
            if("exit".equals(inputCommand)){
                break;
            }
            if(inputCommand.matches(inputCommandRegex)){
                correctCommand = true;
            }
            else{
                System.out.println("Bad parameters!");
            }
        }while(!correctCommand);

        // generated players
        String[] parameters = inputCommand.split(" ");
        Player player1 = new Player('X', parameters[1]);
        Player player2 = new Player('O', parameters[2]);
        Switcher<Player> playerSwitcher = new Switcher<>(List.of(player1, player2));

        // generate board and start the game
        List<List<Character>> board = constructBoard("_________");
        printBoard(board);
        boolean gameContinue = true;
        Scanner scanner = new Scanner(System.in);
        do{
            String coordinates;
            Player currentPlayer = playerSwitcher.getValue();
            if(currentPlayer.isAI()){ // AI move
                System.out.printf("Making move level \"%s\"\n", currentPlayer.getLevel());
                coordinates = generateAIMove(board);
            }
            else{ //player move
                System.out.print("Enter the coordinates: ");
                coordinates = scanner.nextLine();
                if(!validateUserMove(coordinates, board)){
                    continue;
                }
            }
            // correct move has been generated, continue to play it
            int x = coordinates.charAt(0) - 48 - 1;
            int y = coordinates.charAt(2) - 48 - 1;
            board.get(x).set(y, currentPlayer.getMoveSymbol());
            printBoard(board);
            String outcome = determineOutcome(board);
            if(outcome.contains("wins") || outcome.contains("Draw")){
                System.out.println(outcome);
                gameContinue = false;
            }
            playerSwitcher.goNext();
        }while(gameContinue);

    } // main end

    private static boolean validateUserMove(String coordinates, List<List<Character>> board){
        if(!coordinates.matches(numbersRegex)){
            System.out.println("You should enter numbers!");
            return false;
        }
        if(!coordinates.matches(outOfBoundsRegex)){
            System.out.println("Coordinates should be from 1 to 3!");
            return false;
        }
        int x = coordinates.charAt(0) - 48 - 1;
        int y = coordinates.charAt(2) - 48 - 1;
        if(board.get(x).get(y) != '_'){
            System.out.println("This cell is occupied! Choose another one!");
            return false;
        }
        if(coordinates.matches(correctFormat)){
            return true;
        }
        return false;
    }

    private static String generateAIMove(List<List<Character>> board) {
        List<List<Integer>> emptyCells = new ArrayList<>();
        for(int r = 0; r < board.size(); r++){
            List<Character> row = board.get(r);
            for(int c = 0; c < row.size(); c++){
                if(board.get(r).get(c) == '_'){
                    emptyCells.add(List.of(r + 1, c + 1));
                }
            }
        }
        if(emptyCells.isEmpty()){
            return "";
        }
        Random rnd = new Random();
        List<Integer> move = emptyCells.get(rnd.nextInt(emptyCells.size()));
        return String.format("%d %d", move.getFirst(), move.getLast());
    }

    private static String determineOutcome(List<List<Character>> board) {
        String outcome = "Game not finished";
        long empties = 0;

        // horizontal check
        for(List<Character> row : board){
            Character firstCharacter = row.getFirst();
            long count = row.stream().filter(c -> c != '_' && c == firstCharacter).count();
            empties += row.stream().filter(c -> c == '_').count();
            if(count == row.size()){
                return String.format("%c wins", firstCharacter);
            }
        }

        // vertical check
        for(int c = 0; c < board.size(); c++){
            int counter = 1;
            for(int r = 0; r < board.getFirst().size() - 1; r++){
                if(board.get(r).get(c) != '_' && board.get(r).get(c) == board.get(r + 1).get(c)){
                    counter++;
                }
            }
            if(counter == board.size()){
                return String.format("%c wins", board.getFirst().get(c));
            }
        }

        // main diagonal check
        int counter = 1;
        for(int i = 0; i < board.size() - 1; i++){
            if(board.get(i).get(i) == '_'){
                break;
            }
            if(board.get(i).get(i) != '_' && board.get(i).get(i) == board.get(i + 1).get(i + 1)){
                counter++;
            }
        }
        if(counter == board.size()){
            return String.format("%c wins", board.getFirst().getFirst());
        }

        // anti diagonal check
        counter = 1;
        for(int i = 0; i < board.size() - 1; i++){
            if(board.get(i).get(board.size() - 1 - i) != '_' && board.get(i).get(board.size() - 1 - i) == board.get(i + 1).get(board.size() - 1 -1 -i)){
                counter++;
            }
        }
        if(counter == board.size()){
            return String.format("%c wins", board.getLast().getFirst());
        }

        // draw check
        if(empties == 0){
            outcome = "Draw";
        }
        return outcome;
    }

    private static Character determineFirstSecondMove(Character move) {
        return 'X' == move ? 'O' : 'X';
    }

    private static Character determineFirstMove(String boardString) {
        long xCount = boardString.chars().filter(ch -> ch == 'X').count();
        long oCount = boardString.chars().filter(ch -> ch == 'O').count();

        return xCount > oCount ? 'O' : 'X';

    }

    private static List<List<Character>> constructBoard(String boardString){
        List<Character> row1 = new ArrayList<>(List.of(boardString.charAt(0), boardString.charAt(1), boardString.charAt(2)));
        List<Character> row2 = new ArrayList<>(List.of(boardString.charAt(3), boardString.charAt(4), boardString.charAt(5)));
        List<Character> row3 = new ArrayList<>(List.of(boardString.charAt(6), boardString.charAt(7), boardString.charAt(8)));
        return List.of(row1, row2, row3);
    }

    private static void printBoard(List<List<Character>> board){
        System.out.println("---------");
        for(List<Character> row : board){
            System.out.print("| ");
            for(Character c : row){
                System.out.print(c != '_' ? c + " " : "  ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

}