package org.cespedes.android.carlog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService();
        setContentView(R.layout.activity_main);
    }

    public void startService() {
        startService(new Intent(getApplicationContext(), MyService.class));
    }
    public void stopService() {
        stopService(new Intent(getApplicationContext(), MyService.class));
    }

    public void startService(View view) {
        startService();
    }
    public void stopService(View view) {
        stopService();
    }
}
