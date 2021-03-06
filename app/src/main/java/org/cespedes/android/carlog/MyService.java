package org.cespedes.android.carlog;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;
import android.widget.Toast;
import android.util.Log;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class MyService extends Service implements LocationListener {
    public class MyData {
        boolean logging = false;
        String email = "unknown";
        Location loc = new Location("none");
    }
    MyData mydata = new MyData();
    public int counter=0;
    LocationManager locationManager;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private BluetoothAdapter mBluetoothAdapter;

    public MyService() {
    }

    @Override
    public void onLocationChanged(Location loc) {
        mydata.loc = loc;
        Log.d("Carlog", "MyService:onLocationChanged();");
        Toast.makeText(this,
                "Location changed (" + loc.getProvider() + ")",
                Toast.LENGTH_SHORT).show();
        sendData();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Carlog", "MyService:onProviderDisabled();");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Carlog", "MyService:onProviderEnabled();");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Carlog", "MyService:onStatusChanged();");
    }

    private void startGPSLocation() {
        Log.d("Carlog", "startGPSLocation()");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
            locationManager = null;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    private void startNetworkLocation() {
        Log.d("Carlog", "startNetworkLocation()");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
            locationManager = null;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 5 * 60 * 1000, 0, this);
        }
    }

    @Override
    public void onCreate() {
        Log.d("Carlog", "MyService:onCreate();");
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
        for (Account account : accounts) {
            Log.d("Carlog", "account=" + account.name);
            if (emailPattern.matcher(account.name).matches()) {
                mydata.email = account.name;
                break;
            }
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startNetworkLocation();
        startTimer();
        Toast.makeText(this, "Carlog service created", Toast.LENGTH_LONG).show();
    }

    private void StartLog() {
        mydata.logging = true;
        addNotification();
        Toast.makeText(this, "Carlog: StartLog", Toast.LENGTH_LONG).show();
        startGPSLocation();
    }

    private void StopLog() {
        mydata.logging = false;
        delNotification();
        Toast.makeText(this, "Carlog: StopLog", Toast.LENGTH_LONG).show();
        startNetworkLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Carlog", "MyService:onStartCommand();");
        if (intent != null && intent.getExtras() != null) {
            if (intent.getBooleanExtra("startlog", false)) {
                Log.d("Carlog", "received startlog");
                StartLog();
            }
            if (intent.getBooleanExtra("stoplog", false)) {
                Log.d("Carlog", "received stoplog");
                StopLog();
            }
        }
//        startTimer();
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Carlog", "MyService:onBind();");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.car)
                        .setOngoing(true)
                        .setContentTitle("CarLog")
                        .setContentText("Service is running");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void delNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0); // Notification ID to cancel
    }
    @Override
    public void onDestroy() {
        Log.d("Carlog", "MyService:onDestroy();");
        stopTimer();
        delNotification();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Carlog", "mReceiver.onReceive()");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("Carlog", "Bluetooth: \"" + device.getName() + "\" [" + device.getAddress() + "]");
            }
        }
    };

    private Timer timer = null;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        if (timer == null) {
            Log.d("Carlog", "MyService:startTimer(): starting timer");
            timer = new Timer();
            initializeTimerTask();
            timer.schedule(timerTask, 1000, 1*60*1000); // discover Bluetooth devices every minute
        }
    }
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Carlog", "MyService:timerTask:run() ++++ " + (counter++));
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mBluetoothAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
/*
                try {
                    if (mydata.logging) {
                        sendData();
                    }
                } catch (Exception e) {
                    Log.i("Carlog", "MyService:sendData(): Exception " + e);
                }
*/
            }
        };
    }
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private void sendData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Carlog", "MyService:sendData(): email=" + mydata.email + " lat=" + mydata.loc.getLatitude() + " lon=" + mydata.loc.getLongitude() + " acc=" + mydata.loc.getAccuracy());
                    HttpURLConnection client = null;
                    URL url = new URL("https://kermit.cespedes.org/carlog/");
                    client = (HttpURLConnection) url.openConnection();

                    client.setRequestMethod("POST");
                    client.setDoOutput(true);
                    client.setInstanceFollowRedirects(false);
                    client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    client.setRequestProperty("charset", "utf-8");
                    StringBuilder postData = new StringBuilder();
                    postData.append("us=" + mydata.email);
                    postData.append("&lg" + mydata.logging);
                    postData.append("&la=" + mydata.loc.getLatitude());
                    postData.append("&lo=" + mydata.loc.getLongitude());
                    postData.append("&sp=" + mydata.loc.getSpeed());
                    postData.append("&ac=" + mydata.loc.getAccuracy());
                    postData.append("&ti=" + mydata.loc.getTime());
                    postData.append("&pr=" + mydata.loc.getProvider());
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                    client.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    client.getOutputStream().write(postDataBytes);

                    client.connect();
                    int resCode = client.getResponseCode();

                    if (client != null) {
                        client.disconnect();
                    }

                } catch (Exception e) {
                    Log.i("Carlog", "MyService:sendData(); Exception " + e);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
