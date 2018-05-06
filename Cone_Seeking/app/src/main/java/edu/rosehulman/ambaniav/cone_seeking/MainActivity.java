package edu.rosehulman.ambaniav.cone_seeking;

import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGps;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.FieldOrientation;
import edu.rosehulman.me435.FieldOrientationListener;
import edu.rosehulman.me435.NavUtils;
import edu.rosehulman.me435.RobotActivity;

public class MainActivity extends RobotActivity implements FieldGpsListener, FieldOrientationListener {


    private TextView mHighLevelState_TextView, mMission_TextView, mGPSTextView, mGpsInfoTextView, mTargetxyTextview;
    private TextView mTurnamountTextView, mCommandTextView, mTargetHeadingTextView;
    private TextView mBlueBallTextView, mRedBallTextView, mWhiteBallTextView;
    private long mStateStartTime;
    private Timer mTimer;
    private static final int LOOP_INTERVAL_MS = 4000;
    private double mTargetX, mTargetY;
    private int mTargetHeading;
    private double mTurnAmount;
    private String mCommand;
    private State mState = State.READY_FOR_MISSION;
    private Handler mCommandHandler = new Handler();
    private TextView mSensorOrientationTextView;
    private TextView mKeepGoingTextView;
    private boolean keepGoing = false;

//    private FieldGps mFieldGps;
//    private FieldOrientation mFieldOrientation;
    private boolean mSetFieldOrientationWithGpsHeading = false;


    // ---------------------- Mission strategy values ----------------------
    /**
     * Constants for the known locations.
     */
    public static final long NEAR_BALL_GPS_X = 90;
    public static final long FAR_BALL_GPS_X = 240;


    /**
     * Variables that will be either 50 or -50 depending on the balls we get.
     */
    private double mNearBallGpsY = 50;
    private double mFarBallGpsY;



    // NavUtils
    public static final int LOWEST_DESIRABLE_DUTY_CYCLE = 150;
    public static final int LEFT_PWM_VALUE_FOR_STRAIGHT = 245;
    public static final int RIGHT_PWM_VALUE_FOR_STRAIGHT = 255;

    /**
     * If that ball is present the values will be 1, 2, or 3.
     * If not present the value will be 0.
     * For example if we have the black ball, then mWhiteBallLocation will equal 0.
     */
    public int mNearBallLocation, mFarBallLocation, mWhiteBallLocation;
    // ----------------- End of mission strategy values ----------------------

    // ---------------------------- Driving area ---------------------------------
    /**
     * When driving towards a target, using a seek strategy, consider that state a success when the
     * GPS distance to the target is less than (or equal to) this value.
     */
    public static final double ACCEPTED_DISTANCE_AWAY_FT = 20.0; // Within 10 feet is close enough.

    /**
     * Multiplier used during seeking to calculate a PWM value based on the turn amount needed.
     */
    private static final double SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER = 3.0;  // units are (PWM value)/degrees

    /**
     * Variable used to cap the slowest PWM duty cycle used while seeking. Pick a value from -255 to 255.
     */
    private static final int LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE = 150;

    /**
     * PWM duty cycle values used with the drive straight dialog that make your robot drive straightest.
     */
    public int mLeftStraightPwmValue = 240, mRightStraightPwmValue = 255;
    // ------------------------ End of Driving area ------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHighLevelState_TextView = findViewById(R.id.HighLevelState_TextView);
        mMission_TextView = findViewById(R.id.Mission_TextView);
        mGPSTextView = findViewById(R.id.GPS_TextView);
        mTargetxyTextview = findViewById(R.id.TargetXY_TextView);
        mTurnamountTextView = findViewById(R.id.TurnAmount_TextView);
        mCommandTextView = findViewById(R.id.Command_TextView);
        mBlueBallTextView = findViewById(R.id.Blueball_TextView);
        mRedBallTextView = findViewById(R.id.Redball_TextView);
        mWhiteBallTextView = findViewById(R.id.Whiteball_TextView);
        mTargetHeadingTextView = findViewById(R.id.TargetHeading_TextView);
        mCommandTextView = findViewById(R.id.Command_TextView);
        mSensorOrientationTextView = findViewById(R.id.SensorOrientation_TextView);

        mKeepGoingTextView = findViewById(R.id.KeepGoing_TextView);
        mGpsInfoTextView = findViewById(R.id.GPS_TextView);
        mGpsCounter = 0;
        ResetScript();
        mGpsCounter = 0;
//        mFieldGps = new FieldGps(this);
//        mFieldOrientation = new FieldOrientation(this);
//        mRobotActivity = new RobotActivity();
    }

    @Override
    public void loop() {
        super.loop();

        sendWheelSpeed(0,0);
        //Update the UI to show the time in the current state.

                    //mStateTimeTextView.setText("" + getmStateTimeMs() / 1000.0);
                    if (mState.equals(State.READY_FOR_MISSION) == false) { //if not Ready
                        mHighLevelState_TextView.setText(mState.toString() + " " + getmStateTimeMs() / 1000);
                    }


                    if (keepGoing == true) {
                        switch (mState) {
                            case READY_FOR_MISSION:
                                sendWheelSpeed(0, 0);
                                break;
                            case INITIAL_STRAIGHT:
                                sendWheelSpeed(150, 150);
                                if (getmStateTimeMs() > 10000) {
                                    setState(State.GPS_SEEKING);
                                }
                                break;
                            case GPS_SEEKING:
                                seekTargetAt(90, 0);
                                break;
                            case BALL_REMOVAL_SCRIPT:
                                sendWheelSpeed(0, 0);
                                break;
                            case DRIVE_TOWARDS_NEAR_BALL:
                                break;
                            default:
                                //Catch for others
                                break;
                        }
                    }
        keepGoing = false;
        mKeepGoingTextView.setText(" "+ keepGoing);
    }

    public void handleNext(View view) {
        Toast.makeText(this,"Next ",Toast.LENGTH_SHORT).show();
        keepGoing = true;
        mKeepGoingTextView.setText(" "+ keepGoing);
    }

    public enum State {
        READY_FOR_MISSION, INITIAL_STRAIGHT, GPS_SEEKING, BALL_REMOVAL_SCRIPT, DRIVE_TOWARDS_NEAR_BALL, DRIVE_TOWARDS_HOME, WAITING_FOR_PICKUP;
    }

    protected void setState(State newstate) {
        // Write down (in a variable) the current time;
        mStateStartTime = System.currentTimeMillis();

        //Update the UI with the name of the current state
//        mHighLevelState_TextView.setText(newstate.name());

        switch (newstate) { //runscripts

            case READY_FOR_MISSION:
                break;
            case INITIAL_STRAIGHT:
                Script_initialStraight();
                break;
            case GPS_SEEKING:
                Script_GPSseeking();
                setTargets(90, 0);
                break;
            case BALL_REMOVAL_SCRIPT:
                Ball_Script1();
                break;
            case DRIVE_TOWARDS_NEAR_BALL:

                break;
        }
        mState = newstate;

    }

    private void Script_initialStraight() {
        boolean isGPSactive = true;

        while (isGPSactive) {
            if (mGuessX < 45 || mGuessY < 45) {
                isGPSactive = false;
            } else {
                mCommand = getString(R.string.wheel_speed_command, "FORWARD", 150, "FORWARD", 150);
                sendCommand(mCommand);
            }
        }
    }

    private void Script_GPSseeking() {

    }

    private void ScriptBallRemoval() {
    }

    private long getmStateTimeMs() {
        return System.currentTimeMillis() - mStateStartTime;
    }


    // Start
    private void setTargets(int Targetx, int Targety) {
        mTargetX = Targetx;
        mTargetY = Targety;
        mTargetxyTextview.setText("(" + Targetx + ", " + Targety + ")");
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFieldGps.requestLocationUpdates(this);
        mFieldOrientation.registerListener(this);

        // Schedule the Loop
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loop();
                    }
                });
            }
        }, 0, LOOP_INTERVAL_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        mTimer = null;
        mFieldGps.removeUpdates();
        mFieldOrientation.unregisterListener();
    }

    // Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mFieldGps.requestLocationUpdates(this);
    }


    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        super.onLocationChanged(x, y, heading, location);
        Toast.makeText(this,"Got GPS",Toast.LENGTH_SHORT).show();
        String gpsInfo = getString(R.string.xy_format, mCurrentGpsX, mCurrentGpsY);
        if (mCurrentGpsHeading != NO_HEADING) {
            gpsInfo += " " + getString(R.string.degrees_format, mCurrentGpsHeading);
        } else {
            gpsInfo += " ?°";
        }
        gpsInfo += "   " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);

        if (mState == State.DRIVE_TOWARDS_NEAR_BALL) {
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY, FAR_BALL_GPS_X,
                    mFarBallGpsY);
            if (distanceFromTarget < ACCEPTED_DISTANCE_AWAY_FT) {
                setState(State.BALL_REMOVAL_SCRIPT);
            }
        }

        if (mState == State.DRIVE_TOWARDS_HOME) {
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY, 0, 0);
            if (distanceFromTarget < ACCEPTED_DISTANCE_AWAY_FT) {
                setState(State.WAITING_FOR_PICKUP);
            }
        }
    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        super.onSensorChanged(fieldHeading, orientationValues);
        mSensorOrientationTextView.setText(getString(R.string.degrees_format, mCurrentSensorHeading));
    }

    private void AllNavUtilScripts() {

        mTargetHeading = (int) (Math.round(NavUtils.getTargetHeading(mCurrentGpsX, mCurrentGpsY, mTargetX, mTargetY)));
        mTargetHeadingTextView.setText(" " + mTargetHeading);


        if (NavUtils.targetIsOnLeft(mCurrentGpsX, mCurrentGpsY, mCurrentSensorHeading, mTargetX, mTargetY)) {
            int left_delta = (int) Math.round(NavUtils.getLeftTurnHeadingDelta(mCurrentSensorHeading, mTargetHeading));
            mTurnamountTextView.setText("Left " + left_delta + "º");
            int num = (255 - left_delta);
            mCommand = "WHEEL SPEED FORWARD " + (num) + " FORWARD " + (255);
            sendWheelSpeed(num,255);
        } else {
            int right_delta = (int) Math.round(NavUtils.getRightTurnHeadingDelta(mCurrentSensorHeading, mTargetHeading));
            mTurnamountTextView.setText("Right " + right_delta + "º");
            int num = (255 - right_delta);
            mCommand = "WHEEL SPEED FORWARD " + 255 + " FORWARD " + num;
            sendWheelSpeed(255,num);
        }
        mCommandTextView.setText(mCommand);
    }


    public void handleReset(View view) {
//        Toast.makeText(this, "RESET", Toast.LENGTH_SHORT).show();
        setState(State.READY_FOR_MISSION);
        mTargetxyTextview.setText("___");
        mGPSTextView.setText("___");

        mCommandTextView.setText("___");
        mTurnamountTextView.setText("___");
        mTargetHeadingTextView.setText("___");
        ResetScript();
    }

    public void handleGo(View view) {

//        Toast.makeText(this, "Go", Toast.LENGTH_SHORT).show();
        if (mState == State.READY_FOR_MISSION) {
            setState(State.INITIAL_STRAIGHT);
            mTargetxyTextview.setText("("+mTargetX+","+ mTargetY+")");
        }


    }

//    public void handleNotSeen(View view) {
//        if (mSubstate == SubState.IMAGE_REC_SEEKING) {
//            setSubstate(mSubstate.GPS_SEEKING);
//        }
//    }
//
//    public void handleSeenSmall(View view) {
//
////        Toast.makeText(this, "Seen Small", Toast.LENGTH_SHORT).show();
//        if (mSubstate == SubState.GPS_SEEKING) {
//            setSubstate(mSubstate.IMAGE_REC_SEEKING);
//        }
//    }
//
//    public void handleSeenBig(View view) {
//
//        //Toast.makeText(this, "Seen Big", Toast.LENGTH_SHORT).show();
//        if (mSubstate == SubState.IMAGE_REC_SEEKING) {
//            setSubstate(SubState.OPTIONAL_SCRIPT);
//        }
//    }

    public void handleMissionComplete(View view) {

        //Toast.makeText(this, "Mission Complete", Toast.LENGTH_SHORT).show();

        if (mState == State.BALL_REMOVAL_SCRIPT) {
            setState(State.READY_FOR_MISSION);
            ResetScript();
        }
    }

    public void handleStop(View view) {
        setState(State.READY_FOR_MISSION);
    }

    public void handleSetOrigin(View view) {
        mFieldGps.setCurrentLocationAsOrigin();
    }

    public void handleSetXAxis(View view) {
        mFieldGps.setCurrentLocationAsLocationOnXAxis();
    }

    public void handleZeroHeading(View view) {
        mFieldOrientation.setCurrentFieldHeading(0);
    }

    //BUTTON HANDLERS END

    private void ResetScript() {
        setState(State.READY_FOR_MISSION);
        mRedBallTextView.setText("Red\nBall");
        mWhiteBallTextView.setText("White\nBall");
        mBlueBallTextView.setText("Blue\nBall");
        mHighLevelState_TextView.setText("Ready For Mission");
        mGpsCounter = 0;
        mGPSTextView.setText("___");
        mTargetxyTextview.setText("___");
    }

    private void seekTargetAt(double xTarget, double yTarget) {
        Toast.makeText(this,"Seeking",Toast.LENGTH_SHORT).show();
        int leftDutyCycle = LEFT_PWM_VALUE_FOR_STRAIGHT;
        int rightDutyCycle = RIGHT_PWM_VALUE_FOR_STRAIGHT;
        double targetHeading = NavUtils.getTargetHeading(mCurrentGpsX, mCurrentGpsY, xTarget, yTarget);
        double leftTurnAmount = NavUtils.getLeftTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        double rightTurnAmount = NavUtils.getRightTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        if (leftTurnAmount < rightTurnAmount) {
            leftDutyCycle = LEFT_PWM_VALUE_FOR_STRAIGHT - (int) (leftTurnAmount *SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER) ; // Using a VERY simple plan. :)
            leftDutyCycle = Math.max(leftDutyCycle, LOWEST_DESIRABLE_DUTY_CYCLE);
        } else {
            rightDutyCycle = RIGHT_PWM_VALUE_FOR_STRAIGHT - (int) (rightTurnAmount*SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER); // Could also scale it.
            rightDutyCycle = Math.max(rightDutyCycle, LOWEST_DESIRABLE_DUTY_CYCLE);
        }

        mCommand ="WHEEL SPEED FORWARD " + leftDutyCycle + " FORWARD " + rightDutyCycle;
        mCommandTextView.setText(mCommand);
        sendWheelSpeed((int) leftDutyCycle, (int) rightDutyCycle);
    }

    private void updateMissionStrategyVariables() {
        // Goal is to set these values
        mNearBallGpsY = -50;
        mFarBallGpsY = 50;
        mNearBallLocation = 3;
        mWhiteBallLocation = 0;
        mFarBallLocation = 1;

//        // Example of how you might write this code:
//        for (int i = 0; i < 3; i++) {
//            BallColor currentLocationColor = mLocationColors[i];
//            if (currentLocationColor == BallColor.WHITE) {
//                mWhiteBallLocation = i + 1;
//            }
//        }
//
//
//        if (mOnRedTeam) {
//            Log.d(TAG, "I'm on the red team!");
//        } else {
//            Log.d(TAG, "I'm on the blue team!");
//        }
//        Log.d(TAG, "Near ball location: " + mNearBallLocation + "  drop off at " + mNearBallGpsY);
//        Log.d(TAG, "Far ball location: " + mFarBallLocation + "  drop off at " + mFarBallGpsY);
//        Log.d(TAG, "White ball location: " + mWhiteBallLocation);
    }


    // ---------------------------- Driving area ---------------------------------
    private void Ball_Script1 (){
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, -9, 107, -61, -157, 119);
                sendCommand(command);
            }
        }, 500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, -9, 107, -61, -180, 119);
                sendCommand(command);
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, -19, 107, -61, -180, 119);
                sendCommand(command);
            }
        }, 3000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, -19, 107, -61, -125, 119);
                sendCommand(command);
            }
        }, 4000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, 0, 90, 0, -90, 90);
                sendCommand(command);
            }
        }, 4500);

    }
    private void Ball_Script2 (){
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, 0, 129, -82, -180, 90);
                sendCommand(command);
            }
        }, 500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, 0, 127, -79, -180, 90);
                sendCommand(command);
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, 0, 108, -66, -174, 90);
                sendCommand(command);
            }
        }, 3000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = getString(R.string.position_command, 0, 90, 0, -90, 90);
                sendCommand(command);
            }
        }, 3500);

        setState(State.READY_FOR_MISSION);

    }
}