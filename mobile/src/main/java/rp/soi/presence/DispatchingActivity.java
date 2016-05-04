package rp.soi.presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class DispatchingActivity extends Activity {

    protected static final String TAG = "PRESENCE";
    private BluetoothAdapter mBluetoothAdapter;
    private Intent intent;
    private ParseObject proxivent;
    private ArrayList<ParseObject> proxivents = new ArrayList<ParseObject>();


    public DispatchingActivity() {
    }

    //included some comments
    // some more comments
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Associate Installation to userid
        ParseInstallation.getCurrentInstallation().saveInBackground();
        //checkForBluetooth();
        // Check if there is current user info

        if (ParseUser.getCurrentUser() != null){
            if(ParseUser.getCurrentUser().getBoolean("emailVerified") || ParseUser.getCurrentUser().getBoolean("isFBUser")) {
                // Associate Installation to userid
                if (ParseUser.getCurrentUser().get("welcomeProxivent")==null) {
                    createWelcomeProxivent();
                }

                // Start an intent for the logged in activity
                Intent previousIntent = getIntent();
                intent = new Intent(this, MainNavDrawerActivity.class);
                if(previousIntent.hasExtra("fragment")){
                    Log.d(TAG, "Intent fragment1: " + previousIntent.getStringExtra("fragment"));
                    intent.putExtra("fragment", previousIntent.getStringExtra("fragment"));
                }else {
                    Log.d(TAG,"Intent fragment2: " + previousIntent.getStringExtra("fragment"));
                    intent.putExtra("fragment", "OwnProxiventsFragment");
                }
                startActivity(intent);

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Email Address Verification Needed");
                builder.setMessage("We have sent you an email at " + ParseUser.getCurrentUser().getString("email") + ". Please follow the verification instructions before you proceed.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ParseUser user = ParseUser.getCurrentUser();
                        user.logOut();
                        intent = new Intent(DispatchingActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }else {
            // Start and intent for the logged out activity
            intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }


    }

    void createWelcomeProxivent(){
        ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
        ParseInstallation.getCurrentInstallation().saveInBackground();
        proxivent = new ParseObject("Proxivent");
        proxivent.put("owner", ParseUser.getCurrentUser());
        proxivent.put("title", getResources().getString(R.string.sample_proxivent_title));
        proxivent.put("description", getResources().getString(R.string.sample_proxivent_description));
        proxivent.put("uuid", ParseInstallation.getCurrentInstallation().getInstallationId());
        proxivent.put("major", 0);
        proxivent.put("minor", 0);
        proxivent.put("status", "inactive");
        //proxivent.saveInBackground(new SaveCallback() {
        proxivent.pinInBackground();
        proxivent.saveEventually();
        ParseUser user = ParseUser.getCurrentUser();
        user.put("welcomeProxivent", proxivent);
        user.saveInBackground();

            /*@Override
            public void done(ParseException e) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("welcomeProxivent", proxivent);
                user.saveInBackground();

            }
        });*/
    }

    public void checkForBluetooth() {
        //Check whether user has turn on bluetooth on device
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth Adapter Not Found");
            builder.setMessage("Presence requires Bluetooth feature. Your device does not support Bluetooth.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
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
                    finish();

                }
            });
            builder.create();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        } else {
            //Start the PresenceIntentService

                /*beaconManager = BeaconManager.getInstanceForApplication(this);
                beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                beaconManager.setBackgroundScanPeriod(3000);
                beaconManager.setBackgroundBetweenScanPeriod(57000);
                beaconManager.bind(this);
                Region region = new Region("rp.soi.presence.DispatchingActivity", null, null, null);
                regionBootstrap = new RegionBootstrap(this, region);*/
        }
    }



}