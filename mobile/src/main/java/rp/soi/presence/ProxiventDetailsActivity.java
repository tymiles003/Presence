package rp.soi.presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.ArrayList;
import java.util.List;


public class ProxiventDetailsActivity extends Activity {

    protected static final String TAG = "PRESENCE";
    private String proxiventId;
    private ProgressDialog dialog;

    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvUUID;
    private TextView tvMajor;
    private TextView tvMinor;
    private Button startStopButton;
    private ParseObject proxivent;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BeaconManager beaconManager;
    private BeaconParser beaconParser;
    private Beacon beacon;
    private static BeaconTransmitter beaconTransmitter;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxivent_details);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        Intent intent = getIntent();
        proxiventId = intent.getStringExtra("ProxiventId");
        Log.d("PRESENCE", "Selected proxivent ID: " + proxiventId);
        tvTitle = (TextView) findViewById(R.id.textView_proxivent_title);
        tvDesc = (TextView) findViewById(R.id.textView_proxivent_description);
        tvUUID = (TextView) findViewById(R.id.textView_proxivent_uuid);
        tvMajor = (TextView) findViewById(R.id.textView_proxivent_major);
        tvMinor = (TextView) findViewById(R.id.textView_proxivent_minor);
        startStopButton = (Button) findViewById(R.id.button_proxivent_start_stop);
        if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
            startStopButton.setEnabled(false);
        }
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Start-Stop Button clicked");
                if (proxivent.getString("status").equalsIgnoreCase("active")) {
                    Log.d(TAG, "Inside active");
                    // toggle to inactive and stop beaconing
                    dialog = new ProgressDialog(ProxiventDetailsActivity.this);
                    dialog.setMessage("Turning OFF beacon transmitter...");
                    dialog.show();
                    proxivent.put("status", "inactive");
                    proxivent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d(TAG, "Toggled status to: " + proxivent.getString("status"));
                            beaconTransmitter.stopAdvertising();
                            updateUI();
                            dialog.dismiss();
                        }
                    });
                    return;
                }
                if (proxivent.getString("status").equalsIgnoreCase("inactive")) {
                    Log.d(TAG, "Inside inactive");
                    //toggle to active and start beaconing
                    dialog = new ProgressDialog(ProxiventDetailsActivity.this);
                    dialog.setMessage("Turning ON beacon transmitter...");
                    dialog.show();
                    proxivent.put("status", "active");
                    proxivent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d(TAG, "Toggled status to: " + proxivent.getString("status"));
                            startedProxivent();
                            updateUI();
                            dialog.dismiss();

                        }
                    });
                    return;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProxivent();
        //updateUI();

        /*proxivent.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                proxivent=parseObject;
                updateUI();
            }
        });*/

    }

    private void getProxivent() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Retrieving proxivent details...");
        dialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.getInBackground(proxiventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Log.d("PRESENCE", "Selected proxivent title: " + parseObject.getString("title"));
                proxivent = parseObject;
                updateUI();
                dialog.dismiss();
            }
        });
    }

    private void updateUI() {
        tvTitle.setText(proxivent.getString("title"));
        tvDesc.setText(proxivent.getString("description"));
        tvUUID.setText(proxivent.getString("uuid"));
        tvMajor.setText(String.valueOf(proxivent.getInt("major")));
        tvMinor.setText(String.valueOf(proxivent.getInt("minor")));
        if(startStopButton.isEnabled()){
            if (proxivent.getString("status").equalsIgnoreCase("inactive")) {
                startStopButton.setBackgroundColor(getResources().getColor(R.color.green));
                startStopButton.setText("START ADVERTISEMENT");
            }
            if (proxivent.getString("status").equalsIgnoreCase("active")) {
                startStopButton.setBackgroundColor(getResources().getColor(R.color.red));
                startStopButton.setText("STOP ADVERTISEMENT");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_proxivent_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_proxivent) {
            Intent intent = new Intent(this, ProxiventEditActivity.class);
            intent.putExtra("ProxiventId", proxiventId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean startedProxivent() {
        //Check whether user has turn on bluetooth on device
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth Adapter Not Found");
            builder.setMessage("Presence requires Bluetooth feature. Your device does not support Bluetooth.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();

        } else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("BLE Not Supported");
            builder.setMessage("Presence requires Bluetooth Low Energy feature. Your device does not have BLE.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();

        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(enableBtIntent, 0);
        } else {
            if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
                beacon = new Beacon.Builder()
                        .setId1(proxivent.getString("uuid"))
                        .setId2(String.valueOf(proxivent.getInt("major")))
                        .setId3(String.valueOf(proxivent.getInt("minor")))
                        .setManufacturer(0x004c) // Apple Inc.  Change this for other beacon layouts
                        .setTxPower(-59)
                                //.setDataFields(Arrays.asList(new Long[]{0l})) // Remove this for beacon layouts without d: fields
                        .build();
                // Change the layout below for other beacon types
                //beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
                beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                beaconTransmitter.startAdvertising(beacon);
                Log.i(TAG, "Phone has started transmitting as a Beacon...");
                Toast toast = Toast.makeText(getApplicationContext(), "Phone has started transmitting as a Beacon...", Toast.LENGTH_LONG);
                toast.show();
                return true;
            } else {
                Log.i(TAG, "Reason: Your device does not support BLE Transmission");
                Toast toast = Toast.makeText(getApplicationContext(), "Your device does not support BLE Transmission", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            Log.d(TAG, "RequestCode=" + requestCode + " " + "ResultCode=" + resultCode);
        }
    }
}