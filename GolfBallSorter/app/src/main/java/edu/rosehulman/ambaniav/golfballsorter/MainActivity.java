package edu.rosehulman.ambaniav.golfballsorter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.RobotActivity;

import static edu.rosehulman.ambaniav.golfballsorter.R.*;

public class MainActivity extends AccessoryActivity {

    TextView mBall1TextView, mBall2TextView, mBall3TextView;
    int mLocationColor[] = {-1, -1, -1};
    boolean isBlack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        mBall1TextView = findViewById(id.Ball1_TextView);
        mBall2TextView = findViewById(id.Ball2_TextView);
        mBall3TextView = findViewById(id.Ball3_TextView);

    }
    //Button clicks

    public void handle3White(View view) {
        mBall3TextView.setText("White");
        mLocationColor[2] = 5;
    }

    public void handle2White(View view) {
        mBall2TextView.setText("White");
        mLocationColor[1] = 5;

    }

    public void handle1White(View view) {
        mBall1TextView.setText("White");
        mLocationColor[0] = 5;

    }

    public void handle3Black(View view) {
        mBall3TextView.setText("Black");
        mLocationColor[2] = 0;

    }

    public void handle2Black(View view) {
        mBall2TextView.setText("Black");
        mLocationColor[1] = 0;

    }

    public void handle1Black(View view) {
        mBall1TextView.setText("Black");
        mLocationColor[0] = 0;

    }

    public void handle3Green(View view) {
        mBall3TextView.setText("Green");
        mLocationColor[2] = 2;

    }

    public void handle2Green(View view) {
        mBall2TextView.setText("Green");
        mLocationColor[1] = 2;

    }

    public void handle1Green(View view) {
        mBall1TextView.setText("Green");
        mLocationColor[0] = 2;

    }

    public void handle3Yellow(View view) {
        mBall3TextView.setText("Yellow");
        mLocationColor[2] = 4;

    }

    public void handle2Yellow(View view) {
        mBall2TextView.setText("Yellow");
        mLocationColor[1] = 4;

    }

    public void handle1Yellow(View view) {
        mBall1TextView.setText("Yellow");
        mLocationColor[0] = 4;

    }

    public void handle3Red(View view) {
        mBall3TextView.setText("Red");
        mLocationColor[2] = 3;

    }

    public void handle2Red(View view) {
        mBall2TextView.setText("Red");
        mLocationColor[1] = 3;

    }

    public void handle1Red(View view) {
        mBall1TextView.setText("Red");
        mLocationColor[0] = 3;

    }

    public void handle3Blue(View view) {
        mBall3TextView.setText("Blue");
        mLocationColor[2] = 1;

    }

    public void handle2Blue(View view) {
        mBall2TextView.setText("Blue");
        mLocationColor[1] = 1;

    }

    public void handle1Blue(View view) {
        mBall1TextView.setText("Blue");
        mLocationColor[0] = 1;

    }

    public void handle3None(View view) {
        mBall3TextView.setText("___");
        mLocationColor[2] = -1;

    }

    public void handle2none(View view) {
        mBall2TextView.setText("___");
        mLocationColor[1] = -1;


    }

    public void handle1None(View view) {
        mBall1TextView.setText("___");
        mLocationColor[0] = -1;

    }

    public void handlePerformBallTest(View view) {
        sendCommand("getBallColors");
    }

    public void handleClear(View view) {
        mBall1TextView.setText("___");
        mBall2TextView.setText("___");
        mBall3TextView.setText("___");
    }

    public void handleGo(View view) {
        //
        // Toast.makeText(this, mLocationColor[0] + " " + mLocationColor[1] + " " + mLocationColor[2], Toast.LENGTH_SHORT).show();
        goScript();
    }

    private void goScript() {
        String firstMessage = "Remove location ";
        String secondMessage = ", then";
        String thirdMessage = ", then";
        boolean isBlack = false;
        int kickorder[] = {-1, -1, -1};

        for (int i = 0; i < mLocationColor.length; i++) {
            if (mLocationColor[i] == 4) {//Yello or
                kickorder[0] = i;
                firstMessage += (i + 1) + "(yellow)";
            } else if (mLocationColor[i] == 1) {//Blue
                kickorder[0] = i;
                firstMessage += (i + 1) + "(blue)";
            }
        }
        for (int i = 0; i < mLocationColor.length; i++) {
            if (mLocationColor[i] == 5) {// White
                kickorder[1] = i;
                secondMessage += (i + 1) + "(white)";
            } else if (mLocationColor[i] == 0) { //Black Dont kick
                kickorder[1] = i;
                isBlack = true;
            }
        }

        for (int i = 0; i < mLocationColor.length; i++) {


            if (isBlack) { // there is a blackball
                if (mLocationColor[i] == 2) {//Green or
                    kickorder[2] = i;
                    thirdMessage = (i + 1) + "(green). Location " + (kickorder[1] + 1) + "(black) stays.";
                } else if (mLocationColor[i] == 3) {//Red
                    kickorder[2] = i;
                    thirdMessage = (i + 1) + "(red). Location " + (kickorder[1] + 1) + "(black) stays.";

                }
            } else {
                if (mLocationColor[i] == 2) {//Green or
                    kickorder[2] = i;
                    thirdMessage += (i + 1) + "(green).";
                } else if (mLocationColor[i] == 3) {//Red
                    kickorder[2] = i;
                    thirdMessage += (i + 1) + "(red).";

                }
            }
            Toast.makeText(this, firstMessage + secondMessage + thirdMessage, Toast.LENGTH_LONG).show();
        }
    }


//        for (int i = 0; i < mLocationColor.length; i++) {
//
//            if()
////            if (mLocationColor[i] == 0) {
////                add = i + " (Black)";
////            } else if (mLocationColor[i] == 1) {
////                add = i + " (Blue)";
////            } else if (mLocationColor[i] == 2) {
////                add = i + " (Green)";
////            } else if (mLocationColor[i] == 3) {
////                add = i + " (Red)";
////            } else if (mLocationColor[i] == 4) {
////                add = i + " (Yellow)";
////            } else if (mLocationColor[i] == 5) {
////                add = i + " (White)";
////            }
//
//            if (i == 1){
//                Message+=", then";
//            }
//
//        }


    @Override
    protected void onCommandReceived(String receivedCommand) {
        super.onCommandReceived(receivedCommand);
        //Toast.makeText(this, receivedCommand, Toast.LENGTH_SHORT).show();
        if (receivedCommand.equalsIgnoreCase("L-1")) {//None
            mBall1TextView.setText("___");
            mLocationColor[2] = -1;
        } else if (receivedCommand.equalsIgnoreCase("L0")) {//Black
            mBall1TextView.setText("Black");
            mLocationColor[0] = 0;
        } else if (receivedCommand.equalsIgnoreCase("L1")) {//Blue
            mBall1TextView.setText("Blue");
            mLocationColor[0] = 1;
        } else if (receivedCommand.equalsIgnoreCase("L2")) {//Green
            mBall1TextView.setText("Green");
            mLocationColor[0] = 2;
        } else if (receivedCommand.equalsIgnoreCase("L3")) {//Red
            mBall1TextView.setText("Red");
            mLocationColor[0] = 3;
        } else if (receivedCommand.equalsIgnoreCase("L4")) {//Yellow
            mBall1TextView.setText("Yellow");
            mLocationColor[0] = 4;
        } else if (receivedCommand.equalsIgnoreCase("L5")) {//White
            mBall1TextView.setText("White");

            mLocationColor[0] = 5;
        } else if (receivedCommand.equalsIgnoreCase("M-1")) {//None
            mBall2TextView.setText("None");
            mLocationColor[1] = -1;

        } else if (receivedCommand.equalsIgnoreCase("M0")) {//Black
            mBall2TextView.setText("Black");
            mLocationColor[1] = 0;
        } else if (receivedCommand.equalsIgnoreCase("M1")) {//Blue
            mBall2TextView.setText("Blue");
            mLocationColor[1] = 1;
        } else if (receivedCommand.equalsIgnoreCase("M2")) {//Green
            mBall2TextView.setText("Green");
            mLocationColor[1] = 2;

        } else if (receivedCommand.equalsIgnoreCase("M3")) {//Red
            mBall2TextView.setText("Red");
            mLocationColor[1] = 3;

        } else if (receivedCommand.equalsIgnoreCase("M4")) {//Yellow
            mBall2TextView.setText("Yellow");
            mLocationColor[1] = 4;

        } else if (receivedCommand.equalsIgnoreCase("M5")) {//White
            mBall2TextView.setText("White");
            mLocationColor[1] = 5;

        } else if (receivedCommand.equalsIgnoreCase("R-1")) {
            mBall3TextView.setText("None");
            mLocationColor[2] = -1;


        } else if (receivedCommand.equalsIgnoreCase("R0")) {//Black
            mBall3TextView.setText("Black");
            mLocationColor[2] = 0;

        } else if (receivedCommand.equalsIgnoreCase("R1")) {//Blue
            mBall3TextView.setText("Blue");
            mLocationColor[2] = 1;

        } else if (receivedCommand.equalsIgnoreCase("R2")) {//Green
            mBall3TextView.setText("Green");
            mLocationColor[2] = 2;

        } else if (receivedCommand.equalsIgnoreCase("R3")) {//Red
            mBall3TextView.setText("Red");
            mLocationColor[2] = 3;

        } else if (receivedCommand.equalsIgnoreCase("R4")) {//Yellow
            mBall3TextView.setText("Yellow");
            mLocationColor[2] = 4;

        } else if (receivedCommand.equalsIgnoreCase("R5")) {//White
            mBall3TextView.setText("White");
            mLocationColor[2] = 5;
        } else {

        }
    }


}
