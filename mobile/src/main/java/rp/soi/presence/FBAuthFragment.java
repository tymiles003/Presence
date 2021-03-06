package rp.soi.presence;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;


public class FBAuthFragment extends Fragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProgressDialog dialog;
    private String email;
    private String uid;
    private ParseUser currentUser;

    public FBAuthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fbauth, container, false);
        loginButton = (LoginButton) view.findViewById(R.id.fbAuthButton);
        loginButton.setReadPermissions("public_profile", "email");
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                email = object.optString("email");
                                String uid = object.optString("id");
                                Log.d("PRESENCE", "Email: " + email);
                                Log.d("PRESENCE", "JSON: " + object.toString());
                                dialog = new ProgressDialog(getActivity());
                                dialog.setMessage("Logging into Presence...");
                                dialog.show();
                                ParseFacebookUtils.logInInBackground(accessToken, new LogInCallback() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {

                                        if((parseUser!=null) && (parseUser.isNew())){

                                            //currentUser = ParseUser.getCurrentUser();
                                            Profile profile = Profile.getCurrentProfile();
                                            if(profile != null){
                                                Log.d("PRESENCE", "First Name: " + profile.getFirstName());
                                                Log.d("PRESENCE", "Last Name: " + profile.getLastName());
                                                Log.d("PRESENCE", "ID: " + profile.getId());
                                                Log.d("PRESENCE", "Name: " + profile.getName());
                                                Log.d("PRESENCE", "Email: " + email);

                                                parseUser.setEmail(email);
                                                parseUser.put("firstName", profile.getFirstName());
                                                parseUser.put("lastName", profile.getLastName());
                                                parseUser.put("isFBUser", true);
                                                parseUser.put("screenName", profile.getFirstName() + " " + profile.getLastName().substring(0,1) + ".");
                                                //parseUser.put("emailVerified", true);
                                                parseUser.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                        if(dialog.isShowing()){
                                            dialog.dismiss();
                                        }
                                        goBackToDispatchingActivity();
                                    }
                                });
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void goBackToDispatchingActivity(){

        Intent i = new Intent(getActivity().getBaseContext(),DispatchingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
