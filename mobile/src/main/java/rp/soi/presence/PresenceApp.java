package rp.soi.presence;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by geraldlim on 27/7/15.
 */
public class PresenceApp extends android.app.Application implements BootstrapNotifier, BeaconConsumer {

    protected static final String TAG = "PRESENCE";
    private static final int WEARABLE_NOTIFICATION_ID = 123;
    private BeaconManager beaconManager;
    private Region region;
    private RegionBootstrap regionBootstrap;
    private BluetoothAdapter mBluetoothAdapter;
    private BackgroundPowerSaver backgroundPowerSaver;
    private ArrayList<Beacon> previousBeacons = new ArrayList<Beacon>();
    private Boolean newBeaconFound = false;
    private Boolean exists = false;
    private ArrayList<Beacon> iBeacons = new ArrayList<Beacon>();
    private Beacon beacon;
    private Boolean needToLookUpProxivent = false;
    private Boolean needToUpDatePreviousBeacons = false;
    private Boolean hasProxivent = false;

    public PresenceApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Parse SDK.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lyqZPUjNKWS49HhSDErMm9bmOS4Ky1IRv6sCTpjt", "xffTEpyindpwEYvEYkJ9G33gjRCHe3wjDKDwR9rZ");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
        // Specify an Activity to handle all pushes by default.
        //PushService.setDefaultPushCallback(this, MainActivity.class);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.setBackgroundScanPeriod(3000);
        beaconManager.setBackgroundBetweenScanPeriod(57000);
        beaconManager.bind(this);
        region = new Region("rp.soi.presence.DispatchingActivity", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            /*@Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (needToUpDatePreviousBeacons) {
                    hasProxivent=false;
                    for (Beacon beacon : beacons) {
                        Log.d(TAG, "iBeacons " + String.format("%.1f", beacon.getDistance()) + " meters away.");
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Proxivent");
                        parseQuery.whereEqualTo("uuid", beacon.getId1().toString());
                        parseQuery.whereEqualTo("major", beacon.getId2().toInt());
                        parseQuery.whereEqualTo("minor", beacon.getId3().toInt());
                        parseQuery.whereEqualTo("status", "active");
                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if(list.size() >0){
                                    if(needToLookUpProxivent){
                                        Log.d(TAG,"Send Notification");
                                        displayAndroidWearNotification("Presence Detected Proxivents!", "Touch to open see more");
                                    }
                                    ParseObject proxivent = new ParseObject("neabyProxivent");
                                    proxivent.put("proxiventObj", list.get(0));
                                    proxivent.pinInBackground();
                                }
                            }
                        });
                    }
                }
                //needToLookUpProxivent = false;
                //needToUpDatePreviousBeacons = false;
            }*/

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                if (needToLookUpProxivent && beacons.size() > 0) {
                    for (Beacon i : beacons) {
                        if (lookupProxivent(i)) {
                            //send notification
                            Log.d(TAG, "RangeNotifier: didRangeBeaconsInRegion: beacon" + i.getId1().toString() + " is " + String.format("%.1f", i.getDistance()) + " meters away.");
                            break;
                        }
                    }
                }
                if (needToUpDatePreviousBeacons) {
                    previousBeacons = (ArrayList<Beacon>) beacons;
                }
                needToLookUpProxivent = false;
                needToUpDatePreviousBeacons = false;
                /*if (beacons.size() > 0) {
                    Log.d(TAG, "RangeNotifier: didRangeBeaconsInRegion: Previous beacons size:" + previousBeacons.size() + " , Beacons size: " + beacons.size());
                }*/
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "BootrapNotifier: didEnterRegion");
        needToLookUpProxivent = true;
        needToUpDatePreviousBeacons = true;
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "BootrapNotifier: didExitRegion");
        needToUpDatePreviousBeacons = true;
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(TAG, "BootrapNotifier: didDetermineStateForRegion: " + i);
        needToUpDatePreviousBeacons = true;
    }

    public Boolean lookupProxivent(Beacon beacon) {

        Log.d(TAG, "iBeacons " + String.format("%.1f", beacon.getDistance()) + " meters away.");
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Proxivent");
        parseQuery.whereEqualTo("uuid", beacon.getId1().toString());
        parseQuery.whereEqualTo("major", beacon.getId2().toInt());
        parseQuery.whereEqualTo("minor", beacon.getId3().toInt());
        parseQuery.whereEqualTo("status", "active");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() == 0) {
                    exists = false;
                } else {
                    displayAndroidWearNotification("Presence Detected Proxivents!", "Touch to open see more");
                    exists = true;
                }
            }
        });

        return exists;
    }

    /*public void checkForBluetooth() {
        //Check whether user has turn on bluetooth on device
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        {
            if (mBluetoothAdapter == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth Adapter Not Found");
                builder.setMessage("Presence requires Bluetooth feature. Your device does not support Bluetooth.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(1);

                    }
                });
                builder.create();

            } else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("BLE Not Supported");
                builder.setMessage("Presence requires Bluetooth Low Energy feature. Your device does not have BLE.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(1);

                    }
                });
                builder.create();
            } else if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
            } else {
                beaconManager = BeaconManager.getInstanceForApplication(this);
                beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                beaconManager.setBackgroundScanPeriod(3000);
                beaconManager.setBackgroundBetweenScanPeriod(57000);
                beaconManager.bind(this);
                Region region = new Region("rp.soi.presence.DispatchingActivity", null, null, null);
                regionBootstrap = new RegionBootstrap(this, region);
            }


        }
        while (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) ;

    }
*/


    private void displayAndroidWearNotification(String title, String text) {
        //---creating a WearableExtender to
        // add special functionality for wearables---
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);

        Intent intent = new Intent(this, DispatchingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fragment", "NearbyProxiventsFragment");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //---creating a NotificationCompat.Builder---
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_bluetooth_searching_white_24dp)
                        .extend(wearableExtender)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        //---set the sound and lights---
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        //---gets an instance of the NotificationManager service---
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        //---build the notification and issues it
        // with notification manager---
        notificationManager.notify(WEARABLE_NOTIFICATION_ID, notification);
    }


}
