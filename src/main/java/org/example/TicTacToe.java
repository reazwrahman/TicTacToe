package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;



public class TicTacToe implements ActionListener {
    private static final int ROWS = 4;
    private static final int COLUMNS = 3;

    private JFrame frame;
    private Container contentPane;

    Map<String, JButton> actionMapper = new HashMap<>();
    Map<String, Boolean> recordKeeper = new HashMap<>();
    Stack<String> clickHistory = new Stack<>();

    String lastClicked;
    String decision;
    JLabel decisionLabel;

    public TicTacToe(){
        lastClicked = "O";
        decisionLabel = new JLabel();
        decisionLabel.setVisible(false);

        // create the buttons
        for (int i=0; i<9; i++) {
            JButton button = new JButton();
            button.setActionCommand(Integer.toString(i));
            actionMapper.put(Integer.toString(i), button);
            recordKeeper.put(Integer.toString(i), false);
        }
    }

    public void start()
    {
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = frame.getContentPane();

        // grid layout
        contentPane.setLayout(new GridLayout(ROWS,COLUMNS));
        organizeButtons();

        contentPane.add(decisionLabel);
        frame.setSize(600,300);
        frame.setVisible(true);
    }

    private void organizeButtons(){
        for (String key: actionMapper.keySet()) {
            JButton button = actionMapper.get(key);
            contentPane.add(button);
            button.addActionListener(this);
        }
        JButton undoButton = new JButton("Undo");
        JButton resetButton = new JButton("Reset");

        undoButton.setActionCommand("undo");
        resetButton.setActionCommand("reset");

        contentPane.add(undoButton);
        contentPane.add(resetButton);
        undoButton.addActionListener(this);
        resetButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        handleUndo(e);
        handleReset(e);

        String buttonId = e.getActionCommand();
        if(actionMapper.get(buttonId) != null && !recordKeeper.get(buttonId)){
            switchMarker();
            actionMapper.get(buttonId).setText(lastClicked);
            recordKeeper.put(buttonId, true);
            clickHistory.add(buttonId);
            lookForDecision();
        }
    }

    private void handleUndo(ActionEvent e){
        String buttonId = e.getActionCommand();
        if (buttonId.equals("undo")) {
            if (!clickHistory.empty()) {
                String id = clickHistory.pop();
                JButton lastButton = actionMapper.get(id);
                lastButton.setText("");
                recordKeeper.put(id, false);
                switchMarker();
            }
        }
    }

    private void handleReset(ActionEvent e){
        String buttonId = e.getActionCommand();
        if (buttonId.equals("reset")) {
            for (String id: actionMapper.keySet()) {
                JButton button = actionMapper.get(id);
                button.setText("");
                recordKeeper.put(id, false);
            }
            decisionLabel.setVisible(false);
            lastClicked = "O";
            clickHistory.clear();
        }
    }

    private void switchMarker(){
        if (lastClicked.equals("O")) {
            lastClicked = "X";
        } else {
            lastClicked = "O";
        }
    }

    private void lookForDecision(){

        ArrayList<String> readings = new ArrayList<>();
        readings.add(actionMapper.get("0").getText() + actionMapper.get("1").getText() + actionMapper.get("2").getText());
        readings.add(actionMapper.get("3").getText() + actionMapper.get("4").getText() + actionMapper.get("5").getText());
        readings.add(actionMapper.get("6").getText() + actionMapper.get("7").getText() + actionMapper.get("8").getText());

        readings.add(actionMapper.get("0").getText() + actionMapper.get("3").getText() + actionMapper.get("6").getText());
        readings.add(actionMapper.get("1").getText() + actionMapper.get("4").getText() + actionMapper.get("7").getText());
        readings.add(actionMapper.get("2").getText() + actionMapper.get("5").getText() + actionMapper.get("8").getText());


        readings.add(actionMapper.get("0").getText() + actionMapper.get("4").getText() + actionMapper.get("8").getText());
        readings.add(actionMapper.get("2").getText() + actionMapper.get("4").getText() + actionMapper.get("6").getText());

        if (checkForWin(readings) || checkForEnding()) {
            postGameCleanup();
            showDecision();
        }

    }

    private boolean checkForWin(ArrayList<String> readings){
        for (String reading: readings) {
            if (reading.equals("XXX")) {
                decision = "X won";
                return true;
            } else if (reading.equals("OOO")) {
                decision = "O won";
                return true;
            }
        }
        return false;
    }

    private boolean checkForEnding(){
        for (String id: recordKeeper.keySet()) {
            if (!recordKeeper.get(id)) {
                return false; // game is not done yet
            }
        }
        decision = "Match is drawn";
        return true; // game has ended
    }

    private void postGameCleanup(){
        for (String id: actionMapper.keySet()) {
            recordKeeper.put(id, true);
        }

        decisionLabel.setText(decision);
        decisionLabel.setVisible(true);
        clickHistory.clear();

        JOptionPane.showMessageDialog(frame, decision);
    }

}


