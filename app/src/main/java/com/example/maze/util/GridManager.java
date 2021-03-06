package com.example.maze.util;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class GridManager {
    private final String[] directions = {"N", "E", "S", "W"};

    private ArrayList<CustomGridCellButton> map;
    private int size;
    private int obstacles;
    private boolean isStartSet;
    private boolean isEndSet;
    private HashMap<String, String> conversionMap;

    private CustomGridCellButton startButton;
    private CustomGridCellButton endButton;
    private ConstraintLayout gridView;
    //private TextView obstaclesCounter, robotPosition, targetPosition;


    public GridManager(ArrayList<CustomGridCellButton> gridButtons, ConstraintLayout grid, int size
                       // -->          , TextView obstaclesCounter, TextView robotPosition, TextView targetPosition
    ) {
        this.isEndSet = false;
        this.isStartSet = false;
        this.map = gridButtons;
        this.gridView = grid;
        this.size = size;
        this.obstacles = 0;
        /*
        this.obstaclesCounter = obstaclesCounter;
        this.robotPosition = robotPosition;
        this.targetPosition = targetPosition;


        //Labels init
        this.targetPosition.setText("[?,?]");
        this.robotPosition.setText("[?,?]");
        this.obstaclesCounter.setText(String.valueOf(this.obstacles));
         */

        //Conversion map init
        this.conversionMap = new HashMap<>();
        this.conversionMap.put("Empty", "_");
        this.conversionMap.put("Obstacle", "X");
        this.conversionMap.put("Start", "S");
        this.conversionMap.put("End", "E");
    }

    /**
     * Changes the state of a button according to a FSM
     *
     * @param cb the button that had been pressed
     */
    @SuppressLint("SetTextI18n")
    public void changeButtonState(CustomGridCellButton cb, int stage) {
        if (cb.getOrientation().length() == 0) {
            if (stage == 2 || stage == 1) {
                switch (cb.getStatus()) {
                    case "Start":// Start -> Empty
                        cb.setStatus("Empty");
                        // -->      this.robotPosition.setText("[?,?]");
                        this.isStartSet = false;
                        break;

                    case "Empty":// Empty -> Start
                        this.isEndSet = false;

                        if (this.isStartSet) {
                            this.startButton.setStatus("Empty");
                            this.startButton.repaint(this.gridView.getViewById(startButton.getId()));
                            // -->           this.robotPosition.setText("[?,?]");
                        }
                        cb.setStatus("Start");
                        this.isStartSet = true;
                        this.startButton = cb;
                        break;
                }
            }
            else if(stage==3) {
                switch (cb.getStatus()) {
                    case "End":// End -> Empty
                        cb.setStatus("Empty");
                        // -->      this.robotPosition.setText("[?,?]");
                        this.isEndSet = false;
                        break;

                    case "Empty":// Empty -> End
                        if (this.isEndSet) {
                            this.endButton.setStatus("Empty");
                            this.endButton.repaint(this.gridView.getViewById(endButton.getId()));
                        }

                        cb.setStatus("End");
                        this.isEndSet = true;
                        this.endButton = cb;
                        // -->   this.targetPosition.setText("[" + endButton.getCoordinates().x + "," + endButton.getCoordinates().y + "]");
                        //this.obstacles--;
                        break;
                }
            }
            else if(stage==4) {
                switch (cb.getStatus()) {
                    case "Obstacle":// Obstacle -> Empty
                        cb.setStatus("Empty");
                        // -->      this.robotPosition.setText("[?,?]");
                        this.isEndSet = false;
                        this.obstacles--;
                        break;

                    case "Empty":// Empty -> Obstacle
                        cb.setStatus("Obstacle");
                        this.obstacles++;
                        break;
                }
            }
            /*
            else if (stage == 5) {
                switch (cb.getStatus()) {
                    case "Empty":  //Empty -> Obstacle
                        cb.setStatus("Obstacle");
                        this.obstacles++;
                        break;

                    case "Solution":  // Solution -> Solution
                        //Do nothing, just repaint the view
                        break;

                    case "Obstacle":  //Obstacle -> End
                        if (this.isEndSet) {
                            this.endButton.setStatus("Empty");
                            this.endButton.repaint(this.gridView.getViewById(endButton.getId()));
                        }

                        cb.setStatus("End");
                        this.isEndSet = true;
                        this.endButton = cb;
                        // -->   this.targetPosition.setText("[" + endButton.getCoordinates().x + "," + endButton.getCoordinates().y + "]");
                        this.obstacles--;
                        break;

                    case "End":  //End -> Start
                        this.isEndSet = false;

                        if (this.isStartSet) {
                            this.startButton.setStatus("Empty");
                            this.startButton.repaint(this.gridView.getViewById(startButton.getId()));
                            // -->           this.robotPosition.setText("[?,?]");
                        }
                        cb.setStatus("Start");
                        this.isStartSet = true;
                        this.startButton = cb;
                        // -->       this.robotPosition.setText("[" + startButton.getCoordinates().x + "," + startButton.getCoordinates().y + "]");
                        break;

                    default:  //Start -> Empty
                        cb.setStatus("Empty");
                        // -->      this.robotPosition.setText("[?,?]");
                        this.isStartSet = false;
                        break;
                }
                // -->      this.obstaclesCounter.setText(String.valueOf(obstacles));
            }

             */
        }
    }

    /**
     * Convert maze solution to path in order to display the computed plan on the board
     *
     * @param mazeSolution        the computed solution plan
     * @param startingOrientation the starting orientation of the robot
     */
    public void solutionToGridButtons(String mazeSolution, String startingOrientation) {

        char currentOrientation = startingOrientation.charAt(0);
        CustomGridCellButton currentButton = this.getStartButton();

        currentButton.setStatus("Start");
        currentButton.setOrientation(startingOrientation);
        currentButton.performClick(); // Since we set the orientation, the status won't change the
        // status but we will perform a repaint


        for (int i = 0; i < mazeSolution.length(); i++) {
            char currentAction = mazeSolution.charAt(i);

            if (Character.isDigit(currentAction)) { //Move forward

                for (int stepCounter = 0; stepCounter < Character.getNumericValue(currentAction); stepCounter++) {
                    assert currentButton != null;

                    if (currentButton.equals(this.getStartButton())) {
                        currentButton = nextButton(this.getStartButton(), currentOrientation);

                    } else {
                        currentButton.setStatus("Solution");
                        currentButton.performClick(); //Performing click we're automatically repainting the view

                        currentButton = nextButton(currentButton, currentOrientation);

                    }

                }
            } else { //rotate, change currentOrientation according to the direction of the rotation
                currentOrientation = rotation(currentOrientation, currentAction);
            }
        }

    }

    /**
     * Evaluates the next step of the solution plan
     *
     * @param currentButton      the current position
     * @param currentOrientation the current orientation
     * @return the next button of the solution plan
     */
    private CustomGridCellButton nextButton(CustomGridCellButton currentButton, char currentOrientation) {
        int currentX = currentButton.getCoordinates().x;
        int currentY = currentButton.getCoordinates().y;
        int currentButtonIndex = currentButton.getId();

        switch (currentOrientation) {
            case 'N':
                return this.map.get(currentButtonIndex - this.size);
            case 'E':
                return this.map.get(currentButtonIndex + 1);
            case 'S':
                return this.map.get(currentButtonIndex + this.size);
            case 'W':
                return this.map.get(currentButtonIndex - 1);
        }
        return null;
    }

    /**
     * @param previousOrientation the previous orientation
     * @param action              the direction of the rotation
     * @return the new orientation
     */
    private char rotation(char previousOrientation, char action) {
        if (action == '+') {
            switch (previousOrientation) {
                case 'N':
                    return 'E';
                case 'E':
                    return 'S';
                case 'S':
                    return 'W';
                case 'W':
                    return 'N';
            }
        } else { // action = '-'
            switch (previousOrientation) {
                case 'N':
                    return 'W';
                case 'E':
                    return 'N';
                case 'S':
                    return 'E';
                case 'W':
                    return 'S';
            }
        }
        return ' ';
    }


    /**
     * Converts the grid of buttons to maze string
     *
     * @return the maze converted to string format,
     * comprehensible by the maze solver on the server
     */
    public String convertToMazeMap() {
        if (!this.isComplete())
            return "";

        StringBuilder sb = new StringBuilder();
        int currentRow, previousRow = 0;

        for (CustomGridCellButton cb : this.map) {
            currentRow = cb.getCoordinates().x;

            if (currentRow > previousRow)
                sb.append("\\n");
            previousRow = currentRow;

            switch (cb.getStatus()) {
                case "Empty":
                    sb.append("_ ");
                    break;
                case "Start":
                    sb.append("S ");
                    break;
                case "End":
                    sb.append("E ");
                    break;
                case "Obstacle":
                    sb.append("X ");
                    break;
            }
        }

        Log.d("Maze Instance", "Converted instance: " + sb.toString());
        return sb.toString();
    }

    public int getObstacles() {
        return obstacles;
    }

    private boolean isComplete() {
        return this.isEndSet() && this.isStartSet();
    }

    // Getters and setters
    private boolean isStartSet() {
        for (CustomGridCellButton cb : this.map) {
            if (cb.getStatus().equals("Start"))
                return true;
        }
        return false;
    }

    private boolean isEndSet() {
        for (CustomGridCellButton cb : this.map) {
            if (cb.getStatus().equals("End"))
                return true;
        }
        return false;
    }

    public CustomGridCellButton getStartButton() {
        for (CustomGridCellButton cb : this.map) {
            if (cb.getStatus().equals("Start"))
                return cb;
        }
        return null;
    }

//     void displayStartingOrientation(String orientation, int viewId) {
//        View sb = this.gridView.findViewById(viewId);
//
//        if (orientation.equals("North"))
//            sb.setBackgroundResource(R.drawable.arrow_up_bold_box_outline);
//        else if (orientation.equals("South"))
//            sb.setBackgroundResource(R.drawable.arrow_down_bold_box_outline);
//        else if (orientation.equals("East"))
//            sb.setBackgroundResource(R.drawable.arrow_right_bold_box_outline);
//        else if (orientation.equals("West"))
//            sb.setBackgroundResource(R.drawable.arrow_left_bold_box_outline);
//    }

    //Getter & setters
    int getSize() {
        return size;
    }

    void setSize(int size) {
        this.size = size;
    }

    public void displayStartingOrientation(String startingOrientation, int id) {
    }
}
