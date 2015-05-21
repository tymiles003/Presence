package rp.soi.presence;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;


public class BeaconDetectActivity extends Activity implements BeaconConsumer {

    protected static final String TAG ="";
    BeaconManager beaconManager;
    TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detect);
        tvMsg = (TextView) findViewById(R.id.textView);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_detect, menu);
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

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                //tvMsg.setText("I just saw an beacon for the first time!");
                Toast toast = Toast.makeText(getApplicationContext(),"I just saw an beacon for the first time!" , Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void didExitRegion(Region region) {
                //tvMsg.setText("I no longer see an beacon");
                Toast toast = Toast.makeText(getApplicationContext(),"I no longer see an beacon." , Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                //tvMsg.setText("I have just switched from seeing/not seeing beacons: " + state);
                Toast toast = Toast.makeText(getApplicationContext(),"I have just switched from seeing/not seeing beacons: " + state , Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

}
