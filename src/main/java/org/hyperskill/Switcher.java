package org.hyperskill;

import java.util.List;

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

    public T getNextValue(){
        if(items.isEmpty()){
            return null;
        }
        return items.get((index + 1) % items.size());
    }

    public static Switcher<Player> generatePlayerSwitcher(String inputCommand) {
        Player player1;
        Player player2;
        String[] parameters = inputCommand.split(" ");
        // determine first player
        if("hard".equals(parameters[1]) || "medium".equals(parameters[1]) || "easy".equals(parameters[1])){
            player1 = new AIPlayer('X', parameters[1]);
        }
        else { // human player
            player1 = new Player('X');
        }
        // determine second player
        if("hard".equals(parameters[2]) || "medium".equals(parameters[2]) || "easy".equals(parameters[2])){
            player2 = new AIPlayer('O', parameters[2]);
        }
        else { // human player
            player2 = new Player('O');
        }
        return new Switcher<>(List.of(player1, player2));
    }
}