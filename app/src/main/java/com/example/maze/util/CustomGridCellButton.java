package com.example.maze.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.example.maze.PathActivity;
import com.example.maze.R;

/**
 * A custom button that changes color and status when clicked, representing a different type of cell in the grid/maze.
 */
@SuppressLint("AppCompatCustomView")
public class CustomGridCellButton extends Button implements View.OnClickListener {
    private Point coordinates;
    private String status;
    private GridManager gridManager;
    private PathActivity pathActivity;


    private String orientation;

    public CustomGridCellButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        this.status = "Empty";
    }

    public CustomGridCellButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.status = "Empty";
    }

    public CustomGridCellButton(Context context, GridManager grid, PathActivity pathActivity) {
        super(context);
        init();
        this.pathActivity = pathActivity;
        this.gridManager = grid;
        this.status = "Empty";
        this.orientation = "";
    }

    private void init() {
        setOnClickListener(this);
        setStateListAnimator(null);
        this.setBackgroundResource(R.color.cell_default_button);

        this.setMinimumWidth(50);
        this.setMinimumHeight(50);
    }

    @Override
    public void onClick(View v) {
        if(pathActivity.getStage()>1) {
            this.gridManager.changeButtonState(this, pathActivity.getStage());
            repaint(v);
            pathActivity.columnCoordinate.setText(this.coordinates.x+":"+this.coordinates.y);
            if(pathActivity.getStage()==4) {
                pathActivity.columnCoordinate.setText(Integer.toString(gridManager.getObstacles()));
            }
        }
    }

    /**
     * Update the view according to the status selected by the user
     *
     * @param v the view that has to be updated
     */
    public void repaint(View v) {
        Button btn = (Button) v;

        switch (this.status) {
            case "Empty":
                btn.setBackgroundResource(R.color.cell_default_button);
                if(pathActivity.getStage()==2 || pathActivity.getStage()==3)
                    pathActivity.forwardBtn.setVisibility(View.INVISIBLE);
                break;
            case "Start":
                if (this.orientation.length() != 0) {
                    switch (this.orientation) {
                        // This is not good but I couldn't find any way around
                        case "North":
                            this.setBackgroundResource(R.drawable.arrow_up_bold_box_outline);
                            break;
                        case "South":
                            this.setBackgroundResource(R.drawable.arrow_down_bold_box_outline);
                            break;
                        case "East":
                            this.setBackgroundResource(R.drawable.arrow_right_bold_box_outline);
                            break;
                        case "West":
                            this.setBackgroundResource(R.drawable.arrow_left_bold_box_outline);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + this.orientation);
                    }
                } else
                    btn.setBackgroundResource(R.color.start_button);
                if(pathActivity.getStage()==2)
                    pathActivity.forwardBtn.setVisibility(View.VISIBLE);
                break;
            case "Obstacle":
                btn.setBackgroundResource(R.color.obstacle_button);
                break;
            case "End":
                btn.setBackgroundResource(R.color.end_button);
                if(pathActivity.getStage()==3)
                    pathActivity.forwardBtn.setVisibility(View.VISIBLE);
                break;
            case "Solution":
                btn.setBackgroundResource(R.color.solution_button);
                break;
        }
    }

    //Getters & Setters

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public String getStatus() {
        return status;
    }

    public GridManager getGridManager() {
        return gridManager;
    }

    public void setGridManager(GridManager gridManager) {
        this.gridManager = gridManager;
    }

    public String getOrientation() {
        return this.orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public void setStatus(String status) {
        this.status = status;

    }
}
