package rp.soi.presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class ProxiventEditActivity extends Activity {

    private String proxiventId;
    private EditText etTitle;
    private EditText etDesc;
    private EditText etUUID;
    private EditText etMajor;
    private EditText etMinor;
    private ParseObject proxivent;
    private ProgressDialog dialog;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxivent_edit);

        Intent intent = getIntent();
        proxiventId = intent.getStringExtra("ProxiventId");
        Log.d("PRESENCE", "Selected proxivent ID: " + proxiventId);
        etTitle = (EditText) findViewById(R.id.editText_proxivent_title);
        etDesc = (EditText) findViewById(R.id.editText_proxivent_description);
        etUUID = (EditText) findViewById(R.id.editText_proxivent_uuid);
        etMajor = (EditText) findViewById(R.id.editText_proxivent_major);
        etMinor = (EditText) findViewById(R.id.editText_proxivent_minor);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Retrieving proxivent details...");
        dialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.getInBackground(proxiventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                proxivent = parseObject;
                Log.d("PRESENCE", "Selected proxivent title: " + parseObject.getString("title"));
                etTitle.setText(parseObject.getString("title"));
                etDesc.setText(parseObject.getString("description"));
                etUUID.setText(parseObject.getString("uuid"));
                etMajor.setText(String.valueOf(parseObject.getInt("major")));
                etMinor.setText(String.valueOf(parseObject.getInt("minor")));
                dialog.dismiss();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_proxivent_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_edit_proxivent) {
            finish();
            return true;
        }
        if(id == R.id.action_save_proxivent){
            editProxivent();
            return true;
        }
        if(id == R.id.action_delete_proxivent){
            deleteProxivent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void editProxivent(){
        proxivent.put("title", etTitle.getText().toString());
        proxivent.put("description", etDesc.getText().toString());
        proxivent.put("uuid", etUUID.getText().toString());
        proxivent.put("major", Integer.parseInt(etMajor.getText().toString()));
        proxivent.put("minor", Integer.parseInt(etMajor.getText().toString()));
        dialog = new ProgressDialog(ProxiventEditActivity.this);
        dialog.setMessage("Updating proxivent...");
        dialog.show();
        proxivent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                finish();
            }
        });


    }

    private void deleteProxivent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Proxivent");
        builder.setMessage("Are you sure your want to delete \"" + proxivent.getString("title") + "\" proxivent?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialog = new ProgressDialog(ProxiventEditActivity.this);
                dialog.setMessage("Deleting proxivent...");
                dialog.show();
                proxivent.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        Intent i = new Intent(ProxiventEditActivity.this, MainNavDrawerActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("fragment", "OwnProxiventsFragment");
                        startActivity(i);
                    }
                });
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

}
