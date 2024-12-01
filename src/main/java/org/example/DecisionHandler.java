package org.example;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DecisionHandler {
    
    public DecisionTuple lookForDecision(Map<String, JButton> actionMapper,
                                          Map<String, Boolean> recordKeeper){

        ArrayList<String> readings = new ArrayList<>();
        readings.add(actionMapper.get("0").getText() + actionMapper.get("1").getText() + actionMapper.get("2").getText());
        readings.add(actionMapper.get("3").getText() + actionMapper.get("4").getText() + actionMapper.get("5").getText());
        readings.add(actionMapper.get("6").getText() + actionMapper.get("7").getText() + actionMapper.get("8").getText());

        readings.add(actionMapper.get("0").getText() + actionMapper.get("3").getText() + actionMapper.get("6").getText());
        readings.add(actionMapper.get("1").getText() + actionMapper.get("4").getText() + actionMapper.get("7").getText());
        readings.add(actionMapper.get("2").getText() + actionMapper.get("5").getText() + actionMapper.get("8").getText());


        readings.add(actionMapper.get("0").getText() + actionMapper.get("4").getText() + actionMapper.get("8").getText());
        readings.add(actionMapper.get("2").getText() + actionMapper.get("4").getText() + actionMapper.get("6").getText());


        DecisionTuple winLossTuple = checkForWin(readings);
        DecisionTuple drawTuple = checkForEnding(recordKeeper);

        if (winLossTuple.decision) {
            return winLossTuple;
        } else if (drawTuple.decision) {
            return drawTuple;
        } else {
            return new DecisionTuple(false, "");
        }
    }

    private DecisionTuple checkForWin(ArrayList<String> readings){
        DecisionTuple result = new DecisionTuple(false, "");
        for (String reading: readings) {
            if (reading.equals("XXX")) {
                result.decision = true;
                result.decisionLabel = "X won";
                break;
            } else if (reading.equals("OOO")) {
                result.decision = true;
                result.decisionLabel = "O won";
                break;
            }
        }
        return result;
    }

    private DecisionTuple checkForEnding(Map<String, Boolean> recordKeeper){
        DecisionTuple result = new DecisionTuple(false, "");
        for (String id: recordKeeper.keySet()) {
            if (!recordKeeper.get(id)) {
                return result; // game is not done yet
            }
        }
        result.decision = true;
        result.decisionLabel = "Match is drawn";
        return result; // game has ended
    }

}
