package rp.soi.presence;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;


public class WelcomeActivity extends FragmentActivity {

    private FBAuthFragment fbFragment;
    private FragmentManager fm;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            fbFragment = new FBAuthFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.linearlayout1, fbFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            fbFragment = (FBAuthFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.linearlayout1);
        }

        // Log in button click handler
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        // Sign up button click handler
        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
            }
        });

        //FB Button handler
        /*Button fbButton = (Button) findViewById(R.id.fbAuthButton);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> fbPermissions = new ArrayList<String>();
                fbPermissions.add("public_profile");
                fbPermissions.add("email");
                dialog = new ProgressDialog(WelcomeActivity.this);

                ParseFacebookUtils.logInWithReadPermissionsInBackground(WelcomeActivity.this, fbPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        dialog.dismiss();
                        if (user != null) {
                            if (user.isNew()){
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                            }
                            Log.d("MyApp", "User logged in through Facebook!");
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            String email = object.optString("email");
                                            String uid = object.optString("id");
                                            Log.d("HelloFBApp", "Email: " + email);
                                            Log.d("HelloFBApp", "JSON: " + object.toString());

                                            //loginPresenter.loginBySocial(email, uid);
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");

                        }
                        goBackToDispatchingActivity(user);
                    }
                });
            }
        });*/
    }

    private void goBackToDispatchingActivity(ParseUser user){

        Profile profile = Profile.getCurrentProfile();

        if(profile != null){
            Log.d("MyApp", "First Name: " + profile.getFirstName());
            Log.d("MyApp", "Last Name: " + profile.getLastName());
            Log.d("MyApp", "ID: " + profile.getId());
            Log.d("MyApp", "Name: " + profile.getName());
        }
        Intent i = new Intent(WelcomeActivity.this,DispatchingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        Log.d("MyApp", "Back in onActivityResult");
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();*/


    }

}