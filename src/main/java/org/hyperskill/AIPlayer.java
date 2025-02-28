package org.hyperskill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AIPlayer extends Player{
    private final Map<Character, Integer> scores;
    private final String level;
    private static final Random rnd = new Random();

    public AIPlayer(Character moveSymbol, String level) {
        super(moveSymbol);
        this.level = level;
        this.scores = Map.of(moveSymbol, 10, getEnemySymbol(), -10, 'D', 0);
    }
    
    @Override
    public IndexPoint generateMove(List<List<Character>> board){
        System.out.printf("Making move level \"%s\"%n", this.level);
        IndexPoint generatedMove;
        if("easy".equals(this.level)){
            generatedMove = generateEasyLevelMove(board);
        }
        else if("medium".equals(this.level)){
            generatedMove = generateMediumAIMove(board);
        }
        else{ // hard AI
            generatedMove = generateHardLevelMove(board);
        }
        return generatedMove;
    }

    private IndexPoint generateEasyLevelMove(List<List<Character>> board) {
        List<List<Integer>> emptyCells = getEmptyCells(board);
        List<Integer> randomEmptyCell = emptyCells.get(rnd.nextInt(emptyCells.size()));
        return new IndexPoint(randomEmptyCell.get(0), randomEmptyCell.get(1));
    }

    private IndexPoint generateMediumAIMove(List<List<Character>> board) {
        // look for a winning cell
        IndexPoint winningMove = generateTacticalMove("winning", board);
        if(winningMove != null){
            return winningMove;
        }
        // look for a blocking cell
        IndexPoint blockingMove = generateTacticalMove("blocking", board);
        if(blockingMove != null){
            return blockingMove;
        }
        return generateEasyLevelMove(board);
    }

    private IndexPoint generateTacticalMove(String tactic, List<List<Character>> board) {
        List<List<Integer>> emptyCells = getEmptyCells(board);
        if(emptyCells.isEmpty()){
            return null;
        }
        Character moveSymbol;
        if("winning".equals(tactic)){
            moveSymbol = getMySymbol();
        }
        else{
            moveSymbol = getEnemySymbol();
        }
        // look for an appropriate board cell from empty cells
        // horizontal search
        for(List<Integer> emptyCell : emptyCells){
            int x = emptyCell.getFirst();
            int y = emptyCell.getLast();
            long symbolCount = board.get(x).stream().filter(c -> c == moveSymbol).count();
            if(symbolCount == board.size() - 1){
                return new IndexPoint(x ,y);
            }
        }
        // vertical search
        for(List<Integer> emptyCell : emptyCells){
            int x = emptyCell.getFirst();
            int y = emptyCell.getLast();
            int counter = 0;
            for (List<Character> characters : board) {
                if (characters.get(y) == moveSymbol) {
                    counter++;
                }
            }
            if(counter == board.size() - 1){
                return new IndexPoint(x ,y);
            }
        }
        // main diagonal search
        for(List<Integer> emptyCell : emptyCells){
            int x = emptyCell.getFirst();
            int y = emptyCell.getLast();
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
                    return new IndexPoint(x ,y);
                }
            }
        }
        return null;
    }

    private IndexPoint generateHardLevelMove(List<List<Character>> board) {
        int bestScore = Integer.MIN_VALUE;
        IndexPoint move = null;

        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.getFirst().size(); c++) {
                if (board.get(r).get(c) == '_') { // Spot available
                    board.get(r).set(c, getMySymbol());

                    int score = minimax(board, 0, false, getEnemySymbol()); // after making my move, minimize enemy move

                    board.get(r).set(c, '_'); // Undo move

                    if (score > bestScore) {
                        bestScore = score;
                        move = new IndexPoint(r, c);
                    }
                }
            }
        }
        return move;
    }

    private int minimax(List<List<Character>> board, int depth, boolean isMaximizing, Character currentSymbol) {
        String currentState =  Board.determineOutcome(board);
        if (currentState.contains("wins")) {
            return scores.get(currentState.charAt(0)) - depth; // Encourage quick wins
        }
        if (currentState.equals("Draw")) {
            return scores.get('D') - depth; // Encourage quicker draws
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        char nextSymbol = currentSymbol == 'X' ? 'O' : 'X';

        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.getFirst().size(); c++) {
                if (board.get(r).get(c) == '_') {
                    board.get(r).set(c, currentSymbol);
                    int score = minimax(board, depth + 1, !isMaximizing, nextSymbol);
                    board.get(r).set(c, '_'); // Undo move
                    bestScore = isMaximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private static List<List<Integer>> getEmptyCells(List<List<Character>> board) {
        List<List<Integer>> emptyCells = new ArrayList<>();
        for(int r = 0; r < board.size(); r++){
            List<Character> row = board.get(r);
            for(int c = 0; c < row.size(); c++){
                if(board.get(r).get(c) == '_'){
                    emptyCells.add(List.of(r, c));
                }
            }
        }
        return emptyCells;
    }
}
