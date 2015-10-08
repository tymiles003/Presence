package rp.soi.presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class NearbyProxiventDetailsActivity extends Activity {
    protected static final String TAG = "PRESENCE";
    private String proxiventId;
    private ProgressDialog dialog;

    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvUUID;
    private TextView tvMajor;
    private TextView tvMinor;
    private TextView tvOwner;
    private Button participateButton;
    private ParseObject proxivent;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_proxivent_details);

        Intent intent = getIntent();
        proxiventId = intent.getStringExtra("ProxiventId");
        Log.d("PRESENCE", "Selected proxivent ID: " + proxiventId);
        tvOwner = (TextView) findViewById(R.id.textView_proxivent_owner_name);
        tvTitle = (TextView) findViewById(R.id.textView_proxivent_title);
        tvDesc = (TextView) findViewById(R.id.textView_proxivent_description);
        tvUUID = (TextView) findViewById(R.id.textView_proxivent_uuid);
        tvMajor = (TextView) findViewById(R.id.textView_proxivent_major);
        tvMinor = (TextView) findViewById(R.id.textView_proxivent_minor);
        participateButton = (Button) findViewById(R.id.button_proxivent_participate);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getProxivent();
    }

    private void getProxivent() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Retrieving nearby proxivent details...");
        dialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("selectedNearbyProxivent");
        query.fromLocalDatastore();
        query.whereEqualTo("id", proxiventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(list.size() > 0){
                    Log.d("PRESENCE", "Selected proxivent title: " + list.get(0).getString("title"));
                    proxivent = list.get(0).getParseObject("proxiventObj");
                    updateUI();
                    dialog.dismiss();
                }
            }
        });

    }

    private void updateUI() {
        tvTitle.setText(proxivent.getString("title"));
        tvDesc.setText(proxivent.getString("description"));
        tvUUID.setText(proxivent.getString("uuid"));
        tvMajor.setText(String.valueOf(proxivent.getInt("major")));
        tvMinor.setText(String.valueOf(proxivent.getInt("minor")));
        ParseUser user = proxivent.getParseUser("owner");
        try {
            user.fetchIfNeeded();
        }catch (ParseException e){
            e.printStackTrace();
        }
        tvOwner.setText(user.getString("screenName"));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nearby_proxivent_details, menu);
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
