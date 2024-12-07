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
    private DecisionHandler decisionHandler;

    Map<String, JButton> actionMapper = new HashMap<>();
    Map<String, Boolean> recordKeeper = new HashMap<>();
    Stack<String> clickHistory = new Stack<>();

    String lastClicked;
    JLabel decisionLabel;

    public TicTacToe(){
        decisionHandler = new DecisionHandler();
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
            callDecisionHandler();
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
        lastClicked = lastClicked.equals("O")? "X" : "O";
    }

    private void callDecisionHandler(){
        DecisionTuple decisionTuple = decisionHandler.lookForDecision(actionMapper, recordKeeper);
        if (decisionTuple.decision) {
            postGameCleanup(decisionTuple.decisionLabel);
        }
    }

    private void postGameCleanup(String resultMessage){
        for (String id: actionMapper.keySet()) {
            recordKeeper.put(id, true);
        }

        decisionLabel.setText(resultMessage);
        decisionLabel.setVisible(true);
        clickHistory.clear();

        JOptionPane.showMessageDialog(frame, resultMessage);
    }

}


