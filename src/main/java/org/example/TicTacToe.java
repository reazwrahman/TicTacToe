package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutionException;



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
            actionMapper.get(buttonId).setText(lastClicked);
            recordKeeper.put(buttonId, true);
            clickHistory.add(buttonId);
            callDecisionHandler();
            performAIMoveAsync();
            switchMarker();
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

    public String getNextMarker() {
        return lastClicked;
    }

    private void callDecisionHandler(){
        DecisionTuple decisionTuple = decisionHandler.lookForDecision(actionMapper, recordKeeper);
        if (decisionTuple.decision) {
            postGameCleanup(decisionTuple.decisionLabel);
        }
    }

    public void performAIMoveAsync(){
        // Check if game is still active (not all cells filled/game not ended)
        boolean gameActive = false;
        for (Boolean occupied : recordKeeper.values()) {
            if (!occupied) {
                gameActive = true;
                break;
            }
        }

        if (!gameActive) {
            return;  // Game is finished, don't make AI move
        }

        new SwingWorker<Map<String, Integer>, Void>() {
            @Override
            protected Map<String, Integer> doInBackground() throws Exception {
                // This runs on a background thread
                decisionLabel.setText("Opponent is thinking ...");
                decisionLabel.setVisible(true);

                String[][] board = get2DBoard();
                System.out.println(Arrays.deepToString(board));

                // Make the API call on background thread
                return AIAgent.getNextMove(board, getNextMarker());
            }

            @Override
            protected void done() {
                // This runs on the EDT after doInBackground completes
                try {
                    Map<String, Integer> nextMove = get();
                    System.out.println(nextMove);
                    setButtonMarker(getButtonId(nextMove.get("row"), nextMove.get("col")), getNextMarker());
                    switchMarker();  // Switch marker back to player's turn
                    decisionLabel.setText("");
                    callDecisionHandler();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
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

    public String[][] get2DBoard() {
        String[][] board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                String text = actionMapper.get(Integer.toString(index)).getText();
                board[i][j] = text.isEmpty() ? " " : text;
            }
        }
        return board;
    }
    
    public String getButtonId(int row, int col) {
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            throw new IllegalArgumentException("Row and column must be between 0 and 2.");
        }
        int id = row * 3 + col;
        return Integer.toString(id);
    }

    private void setButtonMarker(String buttonId, String marker) {
        if (!actionMapper.containsKey(buttonId)) {
            throw new IllegalArgumentException("Invalid button ID: " + buttonId);
        }
        if (recordKeeper.get(buttonId)) {
            throw new IllegalArgumentException("Cell is already occupied.");
        }
        if (!"X".equals(marker) && !"O".equals(marker)) {
            throw new IllegalArgumentException("Marker must be 'X' or 'O'.");
        }

        // Update the button text
        JButton button = actionMapper.get(buttonId);
        button.setText(marker);

        // Update game state
        recordKeeper.put(buttonId, true);
        clickHistory.push(buttonId);
    }

}
