package org.cespedes.android.carlog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("carlog", "MyReceiver:onReceive()");
        Toast.makeText(context, "Battery Changed", Toast.LENGTH_LONG).show();
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
