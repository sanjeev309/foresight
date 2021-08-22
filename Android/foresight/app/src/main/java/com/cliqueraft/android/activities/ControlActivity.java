package com.cliqueraft.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cliqueraft.android.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class ControlActivity extends AppCompatActivity implements JoystickView.OnMoveListener {

    JoystickView joystickView;
    private Thread bgThread;
    private RequestQueue queue;
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
        if (strength == 0 && angle == 0) {
            Log.d("Threading+++++++: ", String.valueOf(bgThread.isInterrupted()));
            if (bgThread.isAlive()) {
                bgThread.interrupt();
                Log.d("Thread+++++++: ", bgThread.isInterrupted() + " " + bgThread.isAlive());
            }
        } else {
            bgThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getMomentumAndDirection(angle, strength);
                }
            });
            bgThread.start();
        }
    }

    private void getMomentumAndDirection(int angle, int strength) {
        if (angle > 0 && angle < 180) {
            if (angle < 90) {
                Log.d("Direction", "Forward and right");
            } else if(angle > 90) {
                Log.d("Direction", "Forward and left");
            }
        } else if (angle > 180 && angle < 360) {
            if (angle < 270) {
                Log.d("Direction", "Backward and left");
            } else if(angle > 270) {
                Log.d("Direction", "Backward and right");
            }
        }
        sendData(angle, strength);
    }


    private void sendData(int angle, int strength) {
        String url = "https://jsonplaceholder.typicode.com/posts";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("response: ", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode = response.statusCode;
                Log.d("status code:", String.valueOf(statusCode));
                return super.parseNetworkResponse(response);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                Log.e("status code error:", volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }
        };

        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        queue.add(request);
        queue.start();
    }
}