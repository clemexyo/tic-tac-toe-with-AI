package org.hyperskill;

import java.util.*;

class Player{
    private final Character moveSymbol;
    private boolean isAI;
    private final String level;
    private static final List<String> levels = List.of("easy", "medium", "hard");

    public Player(Character moveSymbol, String level) {
        this.moveSymbol = moveSymbol;
        this.level = level;
        levels.stream()
                .filter(l -> l.equals(level))
                .findFirst()
                .ifPresentOrElse(l -> this.isAI = true,
                                () -> this.isAI = false);
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

    public List<T> getValues() {
        return this.items;
    }

    public void goPrevious(){
        index = (index - 1 + items.size()) % items.size();
    }
}

class Main {
    private static final String correctFormat = "^[1-3] [1-3]$";
    private static final String outOfBoundsRegex = "[1-3 ]+";
    private static final String numbersRegex = "[0-9 ]+";
    private static final String inputCommandRegex = "^start [a-zA-Z]+ [a-zA-Z]+$";
    private static final Map<Character, Integer> scores = Map.of('X', 10, 'O', -10, 'D', 0);

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
            if(!inputCommand.matches(inputCommandRegex)){
                System.out.println("Bad parameters!");
                continue;
            }
            // generated players
            String[] parameters = inputCommand.split(" ");
            Player player1 = new Player('X', parameters[1]);
            Player player2 = new Player('O', parameters[2]);
            Switcher<Player> playerSwitcher = new Switcher<>(List.of(player1, player2));

            // generate board
            List<List<Character>> board = constructBoard("_________");
            printBoard(board);

            // start the current game loop
            String coordinates;
            do{
                Player currentPlayer = playerSwitcher.getValue();
                if(currentPlayer.isAI()){ // AI move
                    System.out.printf("Making move level \"%s\"\n", currentPlayer.getLevel());
                    if("easy".equals(currentPlayer.getLevel())){
                        coordinates = generateEasyLevelMove(board, currentPlayer);
                    }
                    else if("medium".equals(currentPlayer.getLevel())){
                        coordinates = generateMediumAIMove(board, currentPlayer);
                    }
                    else{ // hard AI
                        coordinates = generateHardLevelMove(board, currentPlayer);
                    }
                }
                else{ //player move
                    System.out.print("Enter the coordinates: ");
                    coordinates = scanner.nextLine();
                    if("exit".equals(coordinates)){
                        inputCommand = "exit";
                        continue;
                    }
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
                    coordinates = "exit";
                }
                playerSwitcher.goNext();
            }while (!coordinates.equals("exit")); // current game loop
        }while (!inputCommand.equals("exit")); // all games loop

    } // main end

    private static String generateHardLevelMove(List<List<Character>> board, Player currentPlayer) {
        // determine the AI's move symbol to maximize it and its opponent's symbol to minimize it.
        char myMove = currentPlayer.getMoveSymbol();
        char enemyMove = currentPlayer.getMoveSymbol() == 'X' ? 'O' : 'X';

        int bestScore = Integer.MAX_VALUE;
        String move = "";

        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.getFirst().size(); c++) {
                if (board.get(r).get(c) == '_') { // Spot available
                    board.get(r).set(c, myMove);

                    int score = minimax(board, 0, false, enemyMove); // after making my move, minimize enemy move

                    board.get(r).set(c, '_'); // Undo move

                    if (score < bestScore) {
                        bestScore = score;
                        move = (r + 1) + " " + (c + 1);
                    }
                }
            }
        }
        return move;
    }

    private static int minimax(List<List<Character>> board, int depth, boolean isMaximizing, Character currentSymbol) {
        String currentState = determineOutcome(board);
        if (currentState.contains("wins")) {
            return scores.get(currentState.charAt(0)) - depth; // Encourage quick wins
        }
        if (currentState.equals("Draw")) {
            return scores.get('D') - depth; // Encourage quicker draws
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        char enemySymbol = currentSymbol == 'X' ? 'O' : 'X';

        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.getFirst().size(); c++) {
                if (board.get(r).get(c) == '_') {
                    board.get(r).set(c, currentSymbol);
                    int score = minimax(board, depth + 1, !isMaximizing, enemySymbol);
                    board.get(r).set(c, '_'); // Undo move
                    bestScore = isMaximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private static String generateEasyLevelMove(List<List<Character>> board, Player currentPlayer) {
        List<List<Integer>> emptyCells = new ArrayList<>();
        for(int r = 0; r < board.size(); r++){
            List<Character> row = board.get(r);
            for(int c = 0; c < row.size(); c++){
                if(board.get(r).get(c) == '_'){
                    emptyCells.add(List.of(r + 1, c + 1));
                }
            }
        }
        Random rnd = new Random();
        List<Integer> move = emptyCells.get(rnd.nextInt(emptyCells.size()));
        return String.format("%d %d", move.getFirst(), move.getLast());
    }

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

    private static String generateMediumAIMove(List<List<Character>> board, Player AI) {
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
        if(AI.getLevel().equals("easy")){
            Random rnd = new Random();
            List<Integer> move = emptyCells.get(rnd.nextInt(emptyCells.size()));
            return String.format("%d %d", move.getFirst(), move.getLast());
        }
        // look for a winning cell
        String winningMove = generateTacticalMove(emptyCells, AI, "winning", board);
        if(!winningMove.isEmpty()){
            return winningMove;
        }
        // look for a blocking cell
        String blockingMove = generateTacticalMove(emptyCells, AI, "blocking", board);
        if(!blockingMove.isEmpty()){
            return blockingMove;
        }
        Random rnd = new Random();
        List<Integer> move = emptyCells.get(rnd.nextInt(emptyCells.size()));
        return String.format("%d %d", move.getFirst(), move.getLast());
    }

    private static String generateTacticalMove(List<List<Integer>> emptyCells, Player AI, String tactic, List<List<Character>> board) {
        Character moveSymbol;
        if("winning".equals(tactic)){
            moveSymbol = AI.getMoveSymbol();
        }
        else{
            moveSymbol = AI.getMoveSymbol() == 'X' ? 'O' : 'X';
        }
        List<Integer> coordinates = new ArrayList<>();
        // look for an appropriate board cell from empty cells
        // horizontal search
        for(List<Integer> emptyCell : emptyCells){
            int x = emptyCell.getFirst() - 1;
            int y = emptyCell.getLast() - 1;
            long symbolCount = board.get(x).stream().filter(c -> c == moveSymbol).count();
            if(symbolCount == board.size() - 1){
                coordinates.add(x + 1);
                coordinates.add(y + 1);
                return String.format("%d %d", coordinates.getFirst(), coordinates.getLast());
            }
        }
        // vertical search
        for(List<Integer> emptyCell : emptyCells){
            int x = emptyCell.getFirst() - 1;
            int y = emptyCell.getLast() - 1;
            int counter = 0;
            for(int i = 0; i < board.size(); i++){
                if(board.get(i).get(y) == moveSymbol){
                    counter++;
                }
            }
            if(counter == board.size() - 1){
                coordinates.add(x + 1);
                coordinates.add(y + 1);
                return String.format("%d %d", coordinates.getFirst(), coordinates.getLast());
            }
        }
        // main diagonal search
        for(List<Integer> emptyCell : emptyCells){
            int x = emptyCell.getFirst() - 1;
            int y = emptyCell.getLast() - 1;
            int mainDiagonalCounter = 0;
            int antiDiagonalCounter = 0;
            if (x == y || x + y == board.size() - 1) { // Only check relevant diagonals
                for (int i = 0; i < board.size(); i++) {
                    if (x == y && board.get(i).get(i) == moveSymbol) { // Check main diagonal
                        mainDiagonalCounter++;
                    }
                    if (x + y == board.size() - 1 && board.get(i).get(board.size() - 1 - i) == moveSymbol) { // Check anti-diagonal
                        antiDiagonalCounter++;
                    }
                }
                if (mainDiagonalCounter == board.size() - 1 || antiDiagonalCounter == board.size() - 1) {
                    coordinates.add(x + 1);
                    coordinates.add(y + 1);
                    return String.format("%d %d", coordinates.getFirst(), coordinates.getLast());
                }
            }
        }
        return "";
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

    /*private static String generateHardLevelMove(List<List<Character>> board, Player currentPlayer) {
        char nextPlayerMove;
        if(currentPlayer.getMoveSymbol() == 'X') {
            nextPlayerMove = 'O';
        }
        else{
            nextPlayerMove = 'X';
        }
        Switcher<Character> tempPlayerSwitcher = new Switcher<>(List.of(currentPlayer.getMoveSymbol(), nextPlayerMove));
        int bestScore = Integer.MIN_VALUE;
        String move = "";
        for(int r = 0; r < board.size(); r++){
            for(int c = 0; c < board.getFirst().size(); c++){
                if(board.get(r).get(c) == '_'){ // the spot is available, find the best move for it
                    board.get(r).set(c, currentPlayer.getMoveSymbol());
                    int score = minimax(board, 0, false, tempPlayerSwitcher);
                    board.get(r).set(c, '_');
                    if(score > bestScore){
                        bestScore = score;
                        move = String.format("%d %d", r+1, c+1);
                    }
                }
            }
        }
        return move;
    }*/


    /*private static int minimax(List<List<Character>> board, int dept, boolean isMaximizing, Switcher<Character> tempPlayerSwitcher) {
        String currentState = determineOutcome(board);
        if(currentState.contains("wins")){
            return scores.get(currentState.charAt(0)) - dept;
        }
        if(currentState.equals("Draw")){
            return scores.get('D') - dept;
        }
        if(isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < board.size(); r++) {
                for (int c = 0; c < board.getFirst().size(); c++) {
                    if(board.get(r).get(c) == '_'){
                        board.get(r).set(c, tempPlayerSwitcher.getValue());
                        tempPlayerSwitcher.goNext();
                        int score = minimax(board, dept+1, false, tempPlayerSwitcher);
                        bestScore = Math.max(score, bestScore);
                        board.get(r).set(c, '_');
                    }
                }
            }
            return bestScore;
        }
        else{
            int bestScore = Integer.MAX_VALUE;
            for (int r = 0; r < board.size(); r++) {
                for (int c = 0; c < board.getFirst().size(); c++) {
                    if(board.get(r).get(c) == '_'){
                        tempPlayerSwitcher.goNext();
                        board.get(r).set(c, tempPlayerSwitcher.getValue());
                        int score = minimax(board, dept+1, true, tempPlayerSwitcher);
                        bestScore = Math.min(score, bestScore);
                        board.get(r).set(c, '_');
                    }
                }
            }
            return bestScore;
        }
    }*/