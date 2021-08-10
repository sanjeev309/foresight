package com.cliqueraft.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.cliqueraft.android.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class ControlActivity extends AppCompatActivity implements JoystickView.OnMoveListener {

    JoystickView joystickView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        initView();

        setListeners();
    }

    private void setListeners() {
        joystickView.setOnMoveListener(this);
    }

    private void initView() {
        joystickView = findViewById(R.id.joystick);
    }

    @Override
    public void onMove(int angle, int strength) {
        Log.d("String: ", angle +" " + strength);
    }
}