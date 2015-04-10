package com.starboardland.pedometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class CounterActivity extends Activity implements SensorEventListener, OnMapReadyCallback {

    private static final int MIN_IN_MILLIS = 60000;
    private static final int NUM_TEXT_VIEWS = 8;
    private SensorManager sensorManager;
    boolean activityRunning;
    Date checkTimePassed = null;
    long start, end;
    int textViewCounter = 0;
    int numSteps = 0;
    private TextView count1, count2, count3, count4, count5, count6, count7, count8, totalCount;
    private TextView[] textViewArray = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        count1 = (TextView) findViewById(R.id.count1);
        count2 = (TextView) findViewById(R.id.count2);
        count3 = (TextView) findViewById(R.id.count3);
        count4 = (TextView) findViewById(R.id.count4);
        count5 = (TextView) findViewById(R.id.count5);
        count6 = (TextView) findViewById(R.id.count6);
        count7 = (TextView) findViewById(R.id.count7);
        count8 = (TextView) findViewById(R.id.count8);
        totalCount = (TextView) findViewById(R.id.total);

        if (textViewArray == null) {
            textViewArray = new TextView[NUM_TEXT_VIEWS];
            textViewArray[0] = count1;
            textViewArray[1] = count2;
            textViewArray[2] = count3;
            textViewArray[3] = count4;
            textViewArray[4] = count5;
            textViewArray[5] = count6;
            textViewArray[6] = count7;
            textViewArray[7] = count8;

        }

        if (checkTimePassed == null) {
            checkTimePassed = new Date();
            start = checkTimePassed.getTime();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this); 
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int segmentSteps;
        Date endTime = new Date();
        DatabaseHandler db = new DatabaseHandler(this);
        end = endTime.getTime();
        Long totalTime = end - start;
        Log.d("Total Time", totalTime.toString());

        if (numSteps == 0)
            numSteps = (int) event.values[0];

        if ((end - start) > MIN_IN_MILLIS) {

            segmentSteps = (int) event.values[0] - numSteps;
            segmentSteps = segmentSteps - 1;
            StepsTaken segSteps = new StepsTaken(segmentSteps);

            segSteps.setId(textViewCounter);

            textViewCounter++;
            start = end;

            if (textViewCounter < 9)
                Toast.makeText(this, "You took " + segmentSteps + " steps in segment " + textViewCounter, Toast.LENGTH_LONG).show();
            db.addStepsTaken(segSteps);
            StepsTaken testDb = db.getStepsTaken(textViewCounter);
            Integer stepsCheck = testDb.getSteps();
            Log.d("Steps from DB: ", stepsCheck.toString());

            numSteps = (int) event.values[0];

        }

        if (activityRunning) {
            int textViewNumber = textViewCounter + 1;
            if (textViewCounter <= 7)
                textViewArray[textViewCounter].setText("Segment" + textViewNumber + " Steps: " + (event.values[0] - numSteps));
            if (textViewCounter == 8) {
                int totalSteps = 0;

                for (int i = 1; i < (NUM_TEXT_VIEWS + 1); i++) {
                    StepsTaken totalStepsTaken = db.getStepsTaken(i);
                    totalSteps = totalSteps + totalStepsTaken.getSteps();

                }
                totalCount.setText("Total Steps: " + totalSteps);
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }
}
