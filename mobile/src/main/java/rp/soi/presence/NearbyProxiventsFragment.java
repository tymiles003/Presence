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
public class NearbyProxiventsFragment extends Fragment {

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


        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PRESENCE", "OnResume()");
        //retrieveNearbyProxiventsFromLocalStore();
        getNearProxiventsFromPresenceApp();
        adapter = new NearbyProxiventsRecyclerViewAdapter(nearbyProxivents);
        recyclerView.setAdapter(adapter);

    }

    private void getNearProxiventsFromPresenceApp() {
        nearbyProxivents = PresenceApp.getNearbyProxivents();
        Log.d(TAG, "NearbyProxiventsFragment: nearbyProxivents size: " + nearbyProxivents.size());
        for(ParseObject p : nearbyProxivents){
            Log.d(TAG, "NearbyProxiventsFragment: nearbyProxivents distance: " + p.getDouble("distance"));

        }
    }


}

