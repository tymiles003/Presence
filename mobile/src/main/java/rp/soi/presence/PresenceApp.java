package rp.soi.presence;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.PushService;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;

/**
 * Created by geraldlim on 27/7/15.
 */
public class PresenceApp  extends android.app.Application implements BootstrapNotifier, BeaconConsumer{

    protected static final String TAG ="PRESENCE";
    BeaconManager beaconManager;

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
        beaconManager.setBackgroundScanPeriod(60000l);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

    }

    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }


}
