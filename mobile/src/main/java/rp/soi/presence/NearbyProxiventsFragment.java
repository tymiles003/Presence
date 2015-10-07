package rp.soi.presence;


import android.app.Application;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyProxiventsFragment extends Fragment implements BeaconConsumer {

    protected static final String TAG = "PRESENCE";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ParseObject> nearbyProxivents = new ArrayList<ParseObject>();
    private ProgressDialog dialog;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager beaconManager;
    private Boolean didEnter = false;
    private Boolean didExit = false;
    private Boolean didDetermine = false;

    private static final int DATASET_COUNT = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nearby_proxivents, null, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_nearby_proxivents);
        //recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.setBackgroundScanPeriod(3000);
        beaconManager.setBackgroundBetweenScanPeriod(57000);
        //beaconManager.bind(this);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        //initProxivents();
        Log.d("PRESENCE", "OnResume()");

    }


    private void initProxivents() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Retrieving your proxivents...");
        dialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                nearbyProxivents = (ArrayList) list;
                dialog.dismiss();
                adapter = new OwnProxiventsRecyclerViewAdapter(nearbyProxivents);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d(TAG, "In Nearby: didRangeBeaconsInRegion");
                if (didExit && beacons.size() == 0) {
                    nearbyProxivents.clear();
                }

                if (((didEnter && beacons.size() > 0))) {
                    nearbyProxivents.clear();
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
                                if (list.size() > 0) {
                                    nearbyProxivents.add(list.get(0));
                                }
                            }
                        });
                    }
                }
                adapter = new NearbyProxiventsRecyclerViewAdapter(nearbyProxivents);
                recyclerView.setAdapter(adapter);
                didExit = false;
                didEnter = false;
            }

        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "In Nearby: didEnterRegion");
                didEnter = true;
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(TAG, "In Nearby: didExitRegion");
                didExit = true;
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.d(TAG, "In Nearby: didDetermineStateForRegion" + i);

            }
        });
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}

