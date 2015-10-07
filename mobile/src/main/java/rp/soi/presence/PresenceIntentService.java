package rp.soi.presence;

import android.app.IntentService;
import android.content.Intent;

import org.altbeacon.beacon.BeaconConsumer;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class PresenceIntentService extends IntentService implements BeaconConsumer {


    public PresenceIntentService() {
        super("PresenceIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public void onBeaconServiceConnect() {

    }
}
