package rp.soi.presence;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.app.FragmentTransaction;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class MainNavDrawerActivity extends Activity {

    protected static final String TAG = "PRESENCE";
    private String[] navDrawerItems;
    private String[] navDrawerImages;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mMasterViewLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView tvScreenName;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String mScreenName;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    public SharedPreferences sharedPreferences;
    private OwnProxiventsFragment ownProxiventsFragment;
    private NearbyProxiventsFragment nearbyProxiventsFragment;
    private JoinedProxiventsFragment joinedProxiventsFragment;
    private String currentFragment="OwnProxiventsFragment";


    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("PRESENCE_STORE",0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav_drawer);

        if(ParseUser.getCurrentUser() == null){

        }

        fragmentManager = getFragmentManager();
        /*if (savedInstanceState == null) {
            OwnProxiventsFragment fragment = new OwnProxiventsFragment();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
        }*/

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMasterViewLayout = (LinearLayout) findViewById(R.id.master_view);

        // Set Screen name
        mScreenName = (String)ParseUser.getCurrentUser().get("screenName");
        tvScreenName = (TextView) findViewById(R.id.drawer_screen_name);
        tvScreenName.setText("\n" + "  " + mScreenName + "\n");

        // Set the ListView Menu
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        navDrawerItems = getResources().getStringArray(R.array.nav_drawer_list_items);
        navDrawerImages = getResources().getStringArray(R.array.nav_drawer_list_images);
        NavDrawerListAdapter ndla = new NavDrawerListAdapter(this, navDrawerItems,navDrawerImages);
        mDrawerList.setAdapter(ndla);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView)view.findViewById(R.id.title);
                String selectedListItemStr = (String) tv.getText();

                //Scan around item selected
                if(selectedListItemStr.equalsIgnoreCase("Scan for Proxivents")){
                    loadNearbyProxiventsFragment();
                }
                //load own proxivents fragment
                if(selectedListItemStr.equalsIgnoreCase("Your Proxivents")){
                    loadOwnProxiventsFragment();
                }
                //Scan arou item selected
                if(selectedListItemStr.equalsIgnoreCase("Joined/Others")){
                    loadJoinedProxiventsFragment();
                }
                // Logout
                if (selectedListItemStr.equalsIgnoreCase("Logout")){
                    logOut();
                }
            }
        });

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.drawer_open, R.string.drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        Intent i = getIntent();
        if(i.getStringExtra("fragment").equals("OwnProxiventsFragment")){
            loadOwnProxiventsFragment();
        }
        if(i.getStringExtra("fragment").equals("NearbyProxiventsFragment")){
            loadNearbyProxiventsFragment();
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_refresh){
            Log.d(TAG, "Refresh menu icon clicked: " + currentFragment);
            if(currentFragment.equalsIgnoreCase("NearbyProxiventsFragment")){
                loadNearbyProxiventsFragment();
            }
            if(currentFragment.equalsIgnoreCase("OwnProxiventsFragment")){
                loadOwnProxiventsFragment();
            }
            if(currentFragment.equalsIgnoreCase("JoinedProxiventsFragment")){
                loadJoinedProxiventsFragment();
            }
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mMasterViewLayout);
        menu.setGroupVisible(0,!drawerOpen);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void loadOwnProxiventsFragment(){
        currentFragment="OwnProxiventsFragment";
        ownProxiventsFragment = new OwnProxiventsFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, ownProxiventsFragment);
        transaction.commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void loadNearbyProxiventsFragment(){
        currentFragment="NearbyProxiventsFragment";
        nearbyProxiventsFragment = new NearbyProxiventsFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, nearbyProxiventsFragment);
        transaction.commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void loadJoinedProxiventsFragment(){
        currentFragment="JoinedProxiventsFragment";
        joinedProxiventsFragment = new JoinedProxiventsFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, joinedProxiventsFragment);
        transaction.commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void logOut(){
        final ProgressDialog dialog = new ProgressDialog(MainNavDrawerActivity.this);
        dialog.setMessage("Logging out of Presence...");
        dialog.show();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                LoginManager.getInstance().logOut();
                dialog.dismiss();
                Intent intent = new Intent(MainNavDrawerActivity.this, DispatchingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


}
