package rp.soi.presence;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class DispatchingActivity extends Activity {

    public DispatchingActivity() {
    }
    //included some comments
    // some more comments
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Associate Installation to userid
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
        // Associate Installation to userid
            if(ParseUser.getCurrentUser().isNew()){
                ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                ParseInstallation.getCurrentInstallation().saveInBackground();
                ParseObject proxivent = new ParseObject("Proxivent");
                proxivent.put("owner", ParseUser.getCurrentUser());
                proxivent.put("title", getResources().getString(R.string.sample_proxivent_title));
                proxivent.put("description", getResources().getString(R.string.sample_proxivent_description));
                proxivent.put("uuid", ParseInstallation.getCurrentInstallation().getInstallationId());
                proxivent.put("major", 0);
                proxivent.put("minor", 0);
                proxivent.put("status", "inactive");
                proxivent.saveInBackground();
            }

            // Start an intent for the logged in activity
            startActivity(new Intent(this, MainNavDrawerActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }
}