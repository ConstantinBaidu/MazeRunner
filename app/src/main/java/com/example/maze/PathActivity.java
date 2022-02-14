package com.example.maze;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.maze.util.ConnectedThread;
import com.example.maze.util.CustomGridCellButton;
import com.example.maze.util.GridManager;
import com.example.maze.util.ServerConnection;

import java.util.ArrayList;

import static com.example.maze.util.ConnectedThread.isConnect;
import static com.example.maze.util.ConnectedThread.write;


public class PathActivity extends AppCompatActivity {
    // This shouldn't be here but I'll leave it for quick testing
    public final String SERVER_URL = "https://flask-maze-solver.herokuapp.com/";
    private int DEFAULT_SIZE = 5;
    private final String DEFAULT_STARTING_ORIENTATION = "North";
    private final int CONNECTION_CHECK_RATE = 5000;
    private final int REQUEST_ENABLE_BT = 1;
    private int stage = 1;
    public int pingPushes = 1;

    private ArrayList<CustomGridCellButton> gridButtons = null;
    private String startingOrientation;
    private ConstraintLayout grid;
    private GridManager gridManager;
    private ServerConnection serverConnection;
    private AlertDialog.Builder builder;
    private String mazeInstance = "";
    private String mazeSolution = "";


    public ImageView mazeLogo2;
    public ImageView columnPlusBtn;
    public ImageView columnMinusBtn;
    public TextView columnNumber;
    public TextView columnMessage;
    public Button forwardBtn;
    public Button backwardBtn;
    public TextView columnCoordinate;
    public ImageView bgImage;

    public boolean isConnectedBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().setClipToOutline(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        grid = findViewById(R.id.gridConstraintLayout);
        sendCommand("T");
        serverConnection = new ServerConnection(SERVER_URL, this.getApplicationContext(), this);
        if(!isConnected())
            showMessage("Ricordati di attivare una connessione a Internet.");

        mazeLogo2 = (ImageView) findViewById(R.id.mazeLogo2);
        mazeLogo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pingPushes>2) {
                    if (isConnected()) {
                        pingServer();
                    }
                    else
                        showMessage("Devi attivare almeno una connessione ad Internet");
                    pingPushes = 1;
                }
                pingPushes++;
            }
        });

        columnPlusBtn = (ImageView) findViewById(R.id.columnPlusBtn);
        columnPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEFAULT_SIZE<10) {
                    increaseSizeGrid();
                    grid.removeAllViews();
                    buildingGrid();
                    columnNumber.setText(Integer.toString(DEFAULT_SIZE));
                }
            }
        });
        columnMinusBtn = (ImageView) findViewById(R.id.columnMinusBtn);
        columnMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEFAULT_SIZE>3) {
                    decreaseSizeGrid();
                    grid.removeAllViews();
                    buildingGrid();
                    columnNumber.setText(Integer.toString(DEFAULT_SIZE));
                }
            }
        });
        columnNumber = (TextView) findViewById(R.id.columnNumber);
        columnMessage = (TextView) findViewById(R.id.columnMessage);

        columnCoordinate = (TextView) findViewById(R.id.columnCoordinate);
        bgImage = (ImageView) findViewById(R.id.bgImage);

        forwardBtn = (Button) findViewById(R.id.forwardBtn);
        backwardBtn = (Button) findViewById(R.id.backwardBtn);
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stage==4) {

                    String mazeInstance = gridManager.convertToMazeMap();
                    setMazeInstance(mazeInstance);
                    mazeSolutionRoutineDialog();
                }else if(stage == 5) {
                    if(isConnectedBt) {
                        sendCommand("C"+mazeSolution+"F");
                    }else
                        showMessage("Attenzione, Non sei connesso al runner.");
                }else{
                    upStage();
                    stages(getStage());
                }
            }
        });

        backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getStage()==5) {
                    stage = 1;
                    if(isConnectedBt) {
                        sendCommand("T");
                    }else
                        showMessage("Attenzione, Non sei connesso al runner.");
                }else {
                    downStage();
                }
                stages(getStage());
            }
        });

        // Building the grid
        buildingGrid();
        grid.removeAllViews();
        stages(getStage());
    }

    //Azzioni svolte durante ogni passaggio per la creazione del Tragitto
    public void stages(int i) {
        switch (i) {
            case 1: {
                try {
                    isConnectedBt = ConnectedThread.isConnect();
                }catch(Exception ex) {
                    isConnectedBt = false;
                    showMessage("Attenzione, Non sei connesso al runner.");
                }
                deleteSolution();
                //deleteStart();
                //deleteEnd();
                //deleteObstacle();
                backwardBtn.setVisibility(View.INVISIBLE);
                columnMinusBtn.setVisibility(View.VISIBLE);
                columnPlusBtn.setVisibility(View.VISIBLE);
                columnCoordinate.setVisibility(View.INVISIBLE);
                bgImage.setVisibility(View.INVISIBLE);
                columnNumber.setVisibility(View.VISIBLE);
                columnNumber.setText(Integer.toString(DEFAULT_SIZE));
                forwardBtn.setVisibility(View.VISIBLE);
                forwardBtn.setText("Avanti");
                columnMessage.setText(R.string.path_stage_1_message);
                break;
            }
            case 2: {
                deleteEnd();
                deleteStart();
                forwardBtn.setVisibility(View.INVISIBLE);
                backwardBtn.setVisibility(View.VISIBLE);
                backwardBtn.setText("Indietro");
                columnNumber.setText("Partenza");
                columnMinusBtn.setVisibility(View.INVISIBLE);
                columnPlusBtn.setVisibility(View.INVISIBLE);
                columnCoordinate.setVisibility(View.VISIBLE);
                columnCoordinate.setText("[ : ]");
                bgImage.setVisibility(View.VISIBLE);
                bgImage.setBackgroundResource(R.color.start_button);
                columnMessage.setText(R.string.path_stage_2_message);
                break;
            }
            case 3: {
                deleteObstacle();
                deleteEnd();
                columnNumber.setText("Arrivo");
                bgImage.setBackgroundResource(R.color.end_button);
                columnCoordinate.setText("[ : ]");
                columnMessage.setText(R.string.path_stage_3_message);
                forwardBtn.setVisibility(View.INVISIBLE);
                break;
            }
            case 4: {
                forwardBtn.setText("Richiedi Tragitto al Server");
                columnNumber.setText("Ostacoli");
                columnCoordinate.setText("0");
                bgImage.setBackgroundResource(R.color.obstacle_button);
                columnCoordinate.setText(Integer.toString(0));
                columnMessage.setText(R.string.path_stage_4_message);
                break;
            }
            case 5: {
                backwardBtn.setText("Reset");
                forwardBtn.setText("Invialo al Maze Runner");
                columnCoordinate.setVisibility(View.INVISIBLE);
                bgImage.setVisibility(View.INVISIBLE);
                columnCoordinate.setVisibility(View.INVISIBLE);
                columnMessage.setText("");
                columnNumber.setText("Il percorso è stato ricevuto.\n"+mazeSolution); //gridManager.convertToMazeMap()
                break;
            }
        }
    }

    public int getStage() {
        return stage;
    }

    public void upStage() {
        if(stage<5)
            stage = stage + 1;
    }

    public void downStage() {
        if(stage>0)
            stage = stage - 1;
    }

    //Increase Size of Grid
    public void increaseSizeGrid() {
        DEFAULT_SIZE = DEFAULT_SIZE+1;
    }
    //Decrease Size of Grid
    public void decreaseSizeGrid() {
        DEFAULT_SIZE = DEFAULT_SIZE-1;
    }

    public void deleteStart() {
        if(gridButtons!=null) {
            for (CustomGridCellButton button : gridButtons) {
                if (button.getStatus().equals("Start")) {
                    button.getGridManager().changeButtonState(button, 2);
                    button.repaint(button);
                }
            }
        }
    }
    public void deleteSolution() {
        grid.removeAllViews();
        buildingGrid();
    }
    public void deleteEnd() {
        if(gridButtons!=null) {
            for (CustomGridCellButton button : gridButtons) {
                if (button.getStatus().equals("End")) {
                    button.getGridManager().changeButtonState(button, 3);
                    button.repaint(button);
                }
            }
        }
    }
    public void deleteObstacle() {
        for(CustomGridCellButton button:gridButtons) {
            if(button.getStatus().equals("Obstacle")) {
                button.getGridManager().changeButtonState(button, 4);
                button.repaint(button);
            }
        }
    }
    //Building grid
    public void buildingGrid() {
        ConstraintSet constraints = repaintGrid(DEFAULT_SIZE, DEFAULT_SIZE, grid, gridManager);
        constraints.applyTo(grid);
        this.startingOrientation = DEFAULT_STARTING_ORIENTATION;
        this.gridManager = new GridManager(gridButtons, grid, DEFAULT_SIZE);
    }

    public void showMessage(String txt) {
        Toast.makeText(this.getApplicationContext(),
                txt, Toast.LENGTH_LONG).show();
    }
    /**
     * Rebuilds the grid at its default state, with the given dimension, and returns the
     * ConstraintSet that has to be applied to the external container (Constraint Layout).
     *
     * @param rows the rows of the grid
     * @param cols the columns of the grid
     * @param grid the layout that is going to wrap the entire grid
     */
    public ConstraintSet repaintGrid(int rows, int cols, ConstraintLayout grid, GridManager
            gridManager) {
        ArrayList<CustomGridCellButton> gridButtons = new ArrayList<CustomGridCellButton>();
        ConstraintSet constraints = new ConstraintSet();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // Crazy layout operations
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        double marginToWidthRatio = 0.1;

        int btnWidth = (int) ((screenWidth-20) / (marginToWidthRatio * cols + marginToWidthRatio + cols));
        int btnHeight = btnWidth;

        int[] chainIds = new int[rows * cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                CustomGridCellButton cb = new CustomGridCellButton(this.getApplicationContext(), gridManager, this);

                int viewIndex = c + r * cols;
                cb.setCoordinates(new Point(r, c));
                cb.setId(viewIndex);
                cb.setWidth(btnWidth);
                cb.setHeight(btnHeight);
                cb.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if(this.gridButtons!=null) {
                    for (CustomGridCellButton button : this.gridButtons) {
                        if (button.getCoordinates().x == r && button.getCoordinates().y == c)
                            cb.setStatus(button.getStatus());
                        else
                            cb.setStatus("Empty");
                    }
                }else{
                    cb.setStatus("Empty");
                }

                chainIds[viewIndex] = cb.getId();
                gridButtons.add(cb);
                grid.addView(cb, viewIndex);
            }
        }

        //Building the actual grid
        constraints.clone(grid);

        int verticalMargin = (int) (btnWidth * marginToWidthRatio);

        for (int currentId = 0; currentId < cols * rows; currentId++) {
            constraints.connect(
                    grid.getViewById(currentId).getId(), ConstraintLayout.LayoutParams.TOP,
                    grid.getId(), ConstraintLayout.LayoutParams.TOP,
                    verticalMargin * ((int) Math.floor((currentId / cols)) + 1) + btnHeight * ((int) Math.floor((currentId / cols))));

            constraints.connect(
                    grid.getViewById(currentId).getId(), ConstraintLayout.LayoutParams.LEFT,
                    grid.getId(), ConstraintLayout.LayoutParams.LEFT,
                    verticalMargin * ((currentId % cols) + 1) + btnWidth * (currentId % cols));
        }

        //Update GridManager references
        this.gridButtons = gridButtons;
        return constraints;
    }

    public String getStartingOrientation() {
        return startingOrientation;
    }

    private void pingServer() {
        serverConnection.pingServer();
    }

    public boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }

    public void updateSolutionFromCallback(String response) {
        this.mazeSolution = extractSolutionPlanFromJSON(response);
        if (this.mazeSolution.length() > 0) {
            findViewById(R.id.mazeLogo2).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar3).setVisibility(View.INVISIBLE);
            if (this.mazeSolution.length() == 1) { //Impossible maze
                showMessage("Questo labirito non ha soluzione, prova a modificarlo.");
            } else { // plan found
                upStage();
                stages(getStage());
                showMessage("Questa è la soluzione: "+mazeSolution);
                this.gridManager.solutionToGridButtons(mazeSolution, getStartingOrientation());
            }
        }

    }

    /**
     * Extracts the string containing the computed plan from the JSON response
     *
     * @param json the server response in JSON format
     * @return the string containing the computed plan
     */
    public String extractSolutionPlanFromJSON(String json) {
        //String cleanup
        if (json.length() != 0) {
            json = json.replaceAll("\n", "");
            json = json.replaceAll(": ", "");
            json = json.replaceAll("\\}", "");
            json = json.replaceAll("\"", "");
            json = json.trim();

            StringBuilder sb = new StringBuilder();

            if (json.lastIndexOf("moves") == -1)
                return "";
            else {
                for (int i = json.lastIndexOf("moves"); i < json.length(); i++) {
                    if (!Character.isLetter(json.charAt(i))) {
                        if (json.charAt(i) == ',')
                            break;
                        else
                            sb.append(json.charAt(i));
                    }
                }
            }
            return sb.toString();
        }

        return "";
    }

    private void mazeSolutionRoutineDialog() {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_title_starting_orientation)
                .setSingleChoiceItems(R.array.orientations, 0,
                        (dialog, which) -> {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            setStartingOrientation((String) lw.getAdapter().getItem(lw.getCheckedItemPosition()));
                        })
                .setPositiveButton(R.string.solver, (dialog, id) -> {
//                    try {
                    gridManager.displayStartingOrientation(
                            getStartingOrientation(),
                            gridManager.getStartButton().getId());
                    findViewById(R.id.mazeLogo2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar3).setVisibility(View.VISIBLE);
                    showMessage("Richiesta al server effettuata...");

                    // Solve maze
                    sendMazeInstance();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3D8578"));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
    }

    /**
     * Send the current instance of the maze to the server if everything is connected.
     */
    private void sendMazeInstance() {
        serverConnection.sendMazeInstance(
                String.valueOf(this.startingOrientation.charAt(0)),
                this.mazeInstance);
    }

    //Getters and Setters
    public Context getActivity() {
        return this;
    }

    public GridManager getGridManager() {
        return gridManager;
    }

    public void setStartingOrientation(String startingOrientation) {
        this.startingOrientation = startingOrientation;
    }

    public void setGridManager(GridManager gridManager) {
        this.gridManager = gridManager;
    }

    public String getMazeInstance() {
        return mazeInstance;
    }

    public void setMazeInstance(String mazeInstance) {
        this.mazeInstance = mazeInstance;
    }

    public void sendCommand(String a) {
        if(isConnect()) {
            String atxt = a;
            write(atxt.getBytes());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}