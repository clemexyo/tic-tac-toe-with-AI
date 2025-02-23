package org.hyperskill;


import java.util.*;

class Switcher<T>{
    private final List<T> items;
    private Iterator<T> iterator;
    private T currentElement;

    public Switcher(List<T> items){
        this.items = items;
        this.iterator = items.iterator();
    }

    public T getValue() {
        if (!iterator.hasNext()) {
            reset();
        }
        return iterator.next();
    }

    public void reset() {
        iterator = items.iterator();
    }
}

class Main {
    private static final String boardRegex = "^[XO_]{9}$";
    private static final String correctFormat = "^[1-3] [1-3]$";
    private static final String outOfBoundsRegex = "[1-3 ]+";
    private static final String numbersRegex = "[0-9 ]+";

    public static void main(String[] args) {

        System.out.print("Enter the cells: ");
        Scanner scanner = new Scanner(System.in);
        String boardString = scanner.nextLine();
        if(!boardString.matches(boardRegex)) {
            System.out.println("wrong input");
            // continue
        }
        List<List<Character>> board = constructBoard(boardString);
        printBoard(board);
        String initialState = determineOutcome(board);
        if(!"Game not finished".equals(initialState)){
            System.out.println(initialState);
            return;
        }
        boolean wrongCommand = true;
        String coordinates = "";
        int x = -1;
        int y = -1;
        while(wrongCommand){
            System.out.print("Enter the coordinates: ");
            coordinates = scanner.nextLine();
            if(!coordinates.matches(numbersRegex)){
                System.out.println("You should enter numbers!");
                continue;
            }
            if(!coordinates.matches(outOfBoundsRegex)){
                System.out.println("Coordinates should be from 1 to 3!");
                continue;
            }
            x = coordinates.charAt(0) - 48 - 1;
            y = coordinates.charAt(2) - 48 - 1;
            if(board.get(x).get(y) != '_'){
                System.out.println("This cell is occupied! Choose another one!");
                continue;
            }
            if(coordinates.matches(correctFormat)){
                wrongCommand = false;
            }
        }

        Character move = determineFirstMove(boardString);
        Character secondMove = determineFirstSecondMove(move);
        Switcher<Character> moveSwitcher = new Switcher<>(Arrays.asList(move, secondMove));
        board.get(x).set(y, moveSwitcher.getValue());
        printBoard(board);

        String outcome = determineOutcome(board);
        System.out.println(outcome);

    } // main end

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
            if(board.get(i).getLast() == '_'){
                break;
            }
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
