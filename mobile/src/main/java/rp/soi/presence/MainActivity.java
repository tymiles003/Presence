package rp.soi.presence;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {

    ArrayList<String> proxiventNames = new ArrayList<String>();
    Beacon beacon;
    BeaconParser beaconParser;
    BeaconTransmitter beaconTransmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "6te9X5fuh4L6gvvHTEwbiTBpsytqNHuTFt3miiw2", "IpdRRmXSuDDk2VV7VumtuGobi2240w5xUwNlUnuY");

//        ParseObject proxiventObj = new ParseObject("Proxivent");
//        proxiventObj.put("proxiventName", "Staff Sharing");
//        proxiventObj.put("UUID","someBLEUUID");
//        proxiventObj.put("major","1111");
//        proxiventObj.put("minor","2222");
//        proxiventObj.saveInBackground();

        Button btn = (Button) findViewById(R.id.buttonGetProxiventData);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                beacon = new Beacon.Builder()
                        .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                        .setId2("1")
                        .setId3("2")
                        .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
                        .setTxPower(-59)
                        .setDataFields(Arrays.asList(new Long[]{0l})) // Remove this for beacon layouts without d: fields
                        .build();
// Change the layout below for other beacon types
                beaconParser = new BeaconParser()
                        .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                beaconTransmitter.startAdvertising(beacon);
            }
        });

        Button btnStopBeacon = (Button) findViewById(R.id.buttonStopBeacon);
        btnStopBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beaconTransmitter.stopAdvertising();
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
