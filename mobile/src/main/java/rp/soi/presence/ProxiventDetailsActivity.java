package rp.soi.presence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProxiventDetailsActivity extends Activity {
    private String proxiventId;
    private ProgressDialog dialog;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvUUID;
    private TextView tvMajor;
    private TextView tvMinor;
    private ArrayList<ParseObject> proxivents = new ArrayList<ParseObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxivent_details);

        Intent intent = getIntent();
        proxiventId = intent.getStringExtra("ProxiventId");
        Log.d("PRESENCE", "Selected proxivent ID: " + proxiventId);
        tvTitle = (TextView) findViewById(R.id.textView_proxivent_title);
        tvDesc = (TextView) findViewById(R.id.textView_proxivent_description);
        tvUUID = (TextView) findViewById(R.id.textView_proxivent_uuid);
        tvMajor = (TextView) findViewById(R.id.textView_proxivent_major);
        tvMinor = (TextView) findViewById(R.id.textView_proxivent_minor);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Retrieving proxivent details...");
        dialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.getInBackground(proxiventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Log.d("PRESENCE", "Selected proxivent title: " + parseObject.getString("title"));
                tvTitle.setText(parseObject.getString("title"));
                tvDesc.setText(parseObject.getString("description"));
                tvUUID.setText(parseObject.getString("uuid"));
                tvMajor.setText(String.valueOf(parseObject.getInt("major")));
                tvMinor.setText(String.valueOf(parseObject.getInt("minor")));

                dialog.dismiss();
            }
        });

        /*query.whereEqualTo("objectId", proxiventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                proxivents = (ArrayList) list;
                Log.d("PRESENCE", "Selected proxivent title: " + proxivents.get(0).getString("title"));
                String title = proxivents.get(0).getString("title");
                tvTitle.setText(title);
                dialog.dismiss();
            }
        });*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_proxivent_details, menu);
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
