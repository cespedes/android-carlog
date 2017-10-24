package org.cespedes.android.carlog;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},0);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }

        Intent i = new Intent(getBaseContext(), MyService.class);
        startService(i);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {
        Intent i = new Intent(getBaseContext(), MyService.class);
        i.putExtra("startlog", true);
        startService(i);
    }
    public void stopService(View view) {
        Intent i = new Intent(getBaseContext(), MyService.class);
        i.putExtra("stoplog", true);
        startService(i);
    }
}
