package org.hyperskill;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final String WIN_MESSAGE = "%c wins";
    private Board(){}

    public static String determineOutcome(List<List<Character>> board) {
        String outcome = "Game not finished";
        long empties = 0;

        // horizontal check
        for(List<Character> row : board){
            Character firstCharacter = row.getFirst();
            long count = row.stream().filter(c -> c != '_' && c == firstCharacter).count();
            empties += row.stream().filter(c -> c == '_').count();
            if(count == row.size()){
                return String.format(WIN_MESSAGE, firstCharacter);
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
                return String.format(WIN_MESSAGE, board.getFirst().get(c));
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
            return String.format(WIN_MESSAGE, board.getFirst().getFirst());
        }

        // anti diagonal check
        counter = 1;
        for(int i = 0; i < board.size() - 1; i++){
            if(board.get(i).get(board.size() - 1 - i) != '_' && board.get(i).get(board.size() - 1 - i) == board.get(i + 1).get(board.size() - 1 -1 -i)){
                counter++;
            }
        }
        if(counter == board.size()){
            return String.format(WIN_MESSAGE, board.getLast().getFirst());
        }

        // draw check
        if(empties == 0){
            outcome = "Draw";
        }
        return outcome;
    }


    public static List<List<Character>> constructEmptyBoard(){
        List<Character> row1 = new ArrayList<>(List.of('_', '_', '_'));
        List<Character> row2 = new ArrayList<>(List.of('_', '_', '_'));
        List<Character> row3 = new ArrayList<>(List.of('_', '_', '_'));
        return List.of(row1, row2, row3);
    }

    public static void printBoard(List<List<Character>> board){
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
