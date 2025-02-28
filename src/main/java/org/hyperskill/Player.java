package org.hyperskill;

import java.util.List;
import java.util.Scanner;

class Player {
    private static final String CORRECT_FORMAT = "^[1-3] [1-3]$";
    private static final String OUT_OF_BOUNDS_REGEX = "[1-3 ]+";
    private static final String NUMBERS_REGEX = "[0-9 ]+";

    private final Character moveSymbol;

    public Player(Character moveSymbol) {
        this.moveSymbol = moveSymbol;
    }

    public Character getMySymbol(){
        return this.moveSymbol;
    }

    public Character getEnemySymbol(){
        return this.moveSymbol == 'X' ? 'O' : 'X';
    }

    public IndexPoint generateMove(List<List<Character>> board){
        boolean validMove = false;
        IndexPoint generatedMove = null;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.print("Enter the coordinates: ");
            String coordinates = scanner.nextLine();
            if(validateUserMove(coordinates, board)){
                validMove = true;
                generatedMove = new IndexPoint(coordinates.charAt(0) - 48 - 1, coordinates.charAt(2) - 48 - 1);
            }
        }while (!validMove);
        return generatedMove;
    }

    private boolean validateUserMove(String coordinates, List<List<Character>> board){
        if(!coordinates.matches(NUMBERS_REGEX)){
            System.out.println("You should enter numbers!");
            return false;
        }
        if(!coordinates.matches(OUT_OF_BOUNDS_REGEX)){
            System.out.println("Coordinates should be from 1 to 3!");
            return false;
        }
        int x = coordinates.charAt(0) - 48 - 1;
        int y = coordinates.charAt(2) - 48 - 1;
        if(board.get(x).get(y) != '_'){
            System.out.println("This cell is occupied! Choose another one!");
            return false;
        }
        return coordinates.matches(CORRECT_FORMAT);
    }
}