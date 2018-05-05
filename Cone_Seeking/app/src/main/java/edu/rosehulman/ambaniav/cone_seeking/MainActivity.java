package edu.rosehulman.ambaniav.cone_seeking;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.FieldOrientationListener;
import edu.rosehulman.me435.NavUtils;
import edu.rosehulman.me435.RobotActivity;

public class MainActivity extends RobotActivity implements FieldGpsListener,FieldOrientationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public State mState;

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        super.onLocationChanged(x, y, heading, location);

        String gpsInfo = getString(R.string.xy_format, mCurrentGpsX, mCurrentGpsY);
        if (mCurrentGpsHeading != NO_HEADING) {
            gpsInfo += " " + getString(R.string.degrees_format, mCurrentGpsHeading);
        } else {
            gpsInfo += " ?°";
        }
        gpsInfo += "   " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);

        if (mState == State.DRIVE_TOWARDS_FAR_BALL) {
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY, FAR_BALL_GPS_X,
                    mFarBallGpsY);
            if (distanceFromTarget < ACCEPTED_DISTANCE_AWAY_FT) {
                setState(State.FAR_BALL_SCRIPT);
            }
        }

        if (mState == State.DRIVE_TOWARDS_HOME) {
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY, 0,0);
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

    public enum State{
        READY_FOR_MISSION, INITIAL_STRAIGHT,GPS_SEEKING,BALL_REMOVAL_SCRIPT;
    }
}