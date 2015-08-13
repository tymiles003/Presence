package rp.soi.presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity {

    protected static final String TAG ="PRESENCE";
    ArrayList<String> proxiventNames = new ArrayList<String>();
    Beacon beacon;
    BeaconParser beaconParser;
    BeaconTransmitter beaconTransmitter;
    String uuid = "0c407bbb-015f-4ad1-a33c-66151a96f5ec"; // To be ramdomised
    String major ="65535";
    String minor = "0";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("PRESENCE", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UUID",uuid);
        editor.putString("Major",major);
        editor.putString("Minor",minor);
        editor.commit();

        // Track app opens.
        //ParseAnalytics.trackAppOpenedInBackground(getIntent());
        //Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "6te9X5fuh4L6gvvHTEwbiTBpsytqNHuTFt3miiw2", "IpdRRmXSuDDk2VV7VumtuGobi2240w5xUwNlUnuY");
//        ParseObject proxiventObj = new ParseObject("Proxivent");
//        proxiventObj.put("proxiventName", "Staff Sharing");
//        proxiventObj.put("UUID","someBLEUUID");
//        proxiventObj.put("major","1111");
//        proxiventObj.put("minor","2222");
//        proxiventObj.saveInBackground();


        updateListView();


        Button btn = (Button) findViewById(R.id.buttonDetectBeacons);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
                query.whereEqualTo("major", "1111");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> proxivents, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + proxivents.size() + " proxivents");
                            for (ParseObject p : proxivents) {
                                proxiventNames.add(p.getString("proxiventName"));
                            }

                            ListView lv = (ListView) findViewById(R.id.listView);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, proxiventNames);
                            lv.setAdapter(adapter);
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });*/

                Intent i = new Intent(getBaseContext(), BeaconDetectActivity.class);
                startActivity(i);
            }
        });

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "Inside onItemClick");

                beacon = new Beacon.Builder()
                        .setId1(ParseInstallation.getCurrentInstallation().getInstallationId().toString())
                        .setId2(major)
                        .setId3(minor)
                        .setManufacturer(0x004c) // Apple Inc.  Change this for other beacon layouts
                        .setTxPower(-59)
                                //.setDataFields(Arrays.asList(new Long[]{0l})) // Remove this for beacon layouts without d: fields
                        .build();
// Change the layout below for other beacon types
                //beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
                beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                int result = BeaconTransmitter.checkTransmissionSupported(getApplicationContext());
                if (result == BeaconTransmitter.SUPPORTED) {
                    beaconTransmitter.startAdvertising(beacon);
                    Log.i(TAG, "Phone has started transmitting as a Beacon...");
                    Toast toast = Toast.makeText(getApplicationContext(), "Phone has started transmitting as a Beacon...", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    int duration = 3;
                    Log.i(TAG, "Reason: Beacon cannot txb not supported");
                    Toast toast = Toast.makeText(getApplicationContext(), "Your device does not support BLE Transmission", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        ImageButton btnStopBeacon = (ImageButton) findViewById(R.id.buttonStopBeacon);
        btnStopBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beaconTransmitter.stopAdvertising();
                Log.i(TAG, "Your device has stopped BLE Transmission");
                Toast toast = Toast.makeText(getApplicationContext(), "Your device has stopped BLE Transmission", Toast.LENGTH_LONG);
                toast.show();

            }
        });

        ImageButton btnAddProxevents = (ImageButton) findViewById(R.id.buttonGetProxevents);
        btnAddProxevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewProxeventFragment newPEFrag = new NewProxeventFragment();
                FragmentManager fragmentManager = getFragmentManager();
                newPEFrag.show(fragmentManager, "tag");
                updateListView();


            }
        });

        /*
        ImageButton btnGetProxevents = (ImageButton) findViewById(R.id.buttonGetProxevents);
        btnGetProxevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //beaconTransmitter.stopAdvertising();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
                query.whereEqualTo("major", "1111");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> proxivents, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + proxivents.size() + " proxivents");
                            for (ParseObject p : proxivents) {
                                proxiventNames.add(p.getString("proxiventName"));
                            }

                            ListView lv = (ListView) findViewById(R.id.listView);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, proxiventNames);
                            lv.setAdapter(adapter);
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });
            }
        }); */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent intent = new Intent(MainActivity.this, DispatchingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateListView(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.whereEqualTo("UUID", "0c407bbb-015f-4ad1-a33c-66151a96f5ec");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> proxivents, ParseException e) {
                if (e == null) {
                    Log.d("PRESENCE", "Retrieved " + proxivents.size() + " proxivents");
                    proxiventNames.clear();
                    for (ParseObject p : proxivents) {
                        proxiventNames.add(p.getString("Title"));
                    }

                    ListView lv = (ListView) findViewById(R.id.listView);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, proxiventNames);
                    lv.setAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }



}
