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
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), MyService.class));
    }
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), MyService.class));
    }

}
