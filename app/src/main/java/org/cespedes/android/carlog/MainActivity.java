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
        Intent i = new Intent(getBaseContext(), MyService.class);
        i.putExtra("start", true);
        startService(i);
    }
    public void stopService() {
        Intent i = new Intent(getBaseContext(), MyService.class);
        i.putExtra("start", false);
        startService(i);
    }

    public void startService(View view) {
        startService();
    }
    public void stopService(View view) {
        stopService();
    }
}
