package rp.soi.presence;

import android.os.RemoteException;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.PushService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

/**
 * Created by geraldlim on 27/7/15.
 */
public class PresenceApp  extends android.app.Application implements BootstrapNotifier, BeaconConsumer{

    protected static final String TAG ="PRESENCE";
    public BeaconManager beaconManager;
    private RegionBootstrap regionBootstrap;

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
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.setBackgroundScanPeriod(5000);
        beaconManager.setBackgroundBetweenScanPeriod(30000);
        beaconManager.bind(this);
        Region region = new Region("rp.soi.presence.DispatchingActivity", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "BootrapNotifier: didEnterRegion");

    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "BootrapNotifier: didExitRegion");

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }


}
