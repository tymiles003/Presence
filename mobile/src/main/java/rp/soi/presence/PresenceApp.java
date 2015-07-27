package rp.soi.presence;

import com.parse.Parse;
import com.parse.PushService;

/**
 * Created by geraldlim on 27/7/15.
 */
public class PresenceApp  extends android.app.Application{
    public PresenceApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Parse SDK.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lyqZPUjNKWS49HhSDErMm9bmOS4Ky1IRv6sCTpjt", "xffTEpyindpwEYvEYkJ9G33gjRCHe3wjDKDwR9rZ");

        // Specify an Activity to handle all pushes by default.
        //PushService.setDefaultPushCallback(this, MainActivity.class);
    }
}
