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
        Log.d("Carlog", "MyReceiver:onReceive()");
//        Intent startServiceIntent = new Intent(context, MyService.class);
        context.startService(new Intent(context.getApplicationContext(), MyService.class));
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
