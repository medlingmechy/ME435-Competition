package edu.rosehulman.ambaniav.golfballsorter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.RobotActivity;

import static edu.rosehulman.ambaniav.golfballsorter.R.*;

public class MainActivity extends AccessoryActivity {

    TextView mBall1TextView,mBall2TextView,mBall3TextView;
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
    }

    public void handle2White(View view) {
        mBall2TextView.setText("White");

    }

    public void handle1White(View view) {
        mBall1TextView.setText("White");

    }

    public void handle3Black(View view) {
        mBall3TextView.setText("Black");

    }

    public void handle2Black(View view) {
        mBall2TextView.setText("Black");

    }

    public void handle1Black(View view) {
        mBall1TextView.setText("Black");

    }

    public void handle3Green(View view) {
        mBall3TextView.setText("Green");

    }

    public void handle2Green(View view) {
        mBall2TextView.setText("Green");
    }

    public void handle1Green(View view) {
        mBall1TextView.setText("Green");
    }

    public void handle3Yellow(View view) {
        mBall3TextView.setText("Yellow");
    }

    public void handle2Yellow(View view) {
        mBall2TextView.setText("Yellow");
    }

    public void handle1Yellow(View view) {
        mBall1TextView.setText("Yellow");
    }

    public void handle3Red(View view) {
        mBall3TextView.setText("Red");
    }

    public void handle2Red(View view) {
        mBall2TextView.setText("Red");
    }

    public void handle1Red(View view) {
        mBall1TextView.setText("Red");
    }

    public void handle3Blue(View view) {
        mBall3TextView.setText("Blue");
    }

    public void handle2Blue(View view) {
        mBall2TextView.setText("Blue");
    }

    public void handle1Blue(View view) {
        mBall1TextView.setText("Blue");
    }

    public void handle3None(View view) {
        mBall3TextView.setText("___");
    }

    public void handle2none(View view) {
        mBall2TextView.setText("___");

    }

    public void handle1None(View view) {
        mBall1TextView.setText("___");
    }

    public void handlePerformBallTest(View view) {

    }

    public void handleClear(View view) {
    }

    public void handleGo(View view) {
    }


}
