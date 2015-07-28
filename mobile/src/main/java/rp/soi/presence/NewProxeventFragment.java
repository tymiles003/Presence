package rp.soi.presence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;

/**
 * Created by geraldlim on 28/7/15.
 */
public class NewProxeventFragment extends DialogFragment {

    View view;
    SharedPreferences sharedPreferences;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.new_proxevent_layout, null);
        builder.setView(view)
                .setMessage("Create a Proxevent")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Create a Proxevent in Parse.com
                        Log.i("PRESENCE","Dialog add button clicked");
                        EditText etTitle = (EditText) view.findViewById(R.id.editText2);
                        EditText etDesc = (EditText) view.findViewById(R.id.editText3);

                        ParseObject proxiventObj = new ParseObject("Proxivent");
                        proxiventObj.put("Title", etTitle.getText().toString());
                        proxiventObj.put("Description", etDesc.getText().toString());
                        proxiventObj.put("UUID",sharedPreferences.getString("UUID", "0c407bbb-015f-4ad1-a33c-66151a96f5ec"));
                        proxiventObj.put("Major", sharedPreferences.getString("Major", "1111"));
                        proxiventObj.put("Minor",sharedPreferences.getString("Minor", "2222"));
                        proxiventObj.saveInBackground();
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i("PRESENCE","Dialog Cancel button clicked");

                    }
                });

        return builder.create();
    }
}
