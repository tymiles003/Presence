package rp.soi.presence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by geraldlim on 28/7/15.
 */
public class CreateProxiventFragment extends DialogFragment {

    View view;
    SharedPreferences sharedPreferences;
    Context context;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("PRESENCE", 0);
        fragmentManager = getFragmentManager();
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
                        proxiventObj.put("title", etTitle.getText().toString());
                        proxiventObj.put("description", etDesc.getText().toString());
                        proxiventObj.put("uuid", ParseInstallation.getCurrentInstallation().getInstallationId());
                        proxiventObj.put("major", 0);
                        proxiventObj.put("minor", 0);
                        proxiventObj.put("status", "inactive");
                        proxiventObj.put("owner", ParseUser.getCurrentUser());
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Saving proxivent");
                        progressDialog.show();
                        proxiventObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                progressDialog.dismiss();
                                OwnProxiventsFragment ownProxiventsFragment = new OwnProxiventsFragment();
                                transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.content_frame, ownProxiventsFragment);
                                transaction.commit();
                            }
                        });
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
