package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public TicTacToe(){
        lastClicked = "O";

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

                lastClicked = "O";
                clickHistory.clear();
            }
        }
    }

    private void switchMarker(){
        if (lastClicked.equals("O")) {
            lastClicked = "X";
        } else {
            lastClicked = "O";
        }
    }
}
