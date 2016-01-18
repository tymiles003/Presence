package rp.soi.presence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProxiventParticipantsActivity extends Activity {

    protected static final String TAG = "PARTICIPANTS";
    private ArrayList<String> participantsNames = new ArrayList<String>();
    private ArrayList<ParseUser> participants = new ArrayList<ParseUser>();
    private ArrayList<ParseObject> participations = new ArrayList<ParseObject>();
    private String proxiventId;
    private ProgressDialog dialog;
    private ListView lv;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxivent_participants);
        Intent i = getIntent();
        proxiventId = i.getStringExtra("ProxiventId");
        Log.d(TAG, "ProxiventId= " + proxiventId);
        lv = (ListView) findViewById(R.id.listView_participants);

    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Retrieving participants...");
        dialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Participation");
        query.whereEqualTo("proxiventId", proxiventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                Log.d(TAG, "In done()");
                participants.clear();
                participations.clear();
                participantsNames.clear();
                participations = (ArrayList) list;
                Log.d(TAG,"Num of participations= " + participations.size());
                for(ParseObject p : participations){
                    ParseUser user = p.getParseUser("participant");
                    try{
                        user.fetchIfNeeded();
                    }catch (ParseException ex){
                        ex.printStackTrace();
                    }
                    participants.add(user);
                    participantsNames.add(user.getString("screenName"));
                }
                arrayAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, participantsNames);
                lv.setAdapter(arrayAdapter);
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_proxivent_participants, menu);
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
