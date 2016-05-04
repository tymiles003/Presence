package rp.soi.presence;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class JoinedProxiventsFragment extends Fragment {
    protected static final String TAG = "JOINED";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ParseObject> participations = new ArrayList<ParseObject>();
    private ArrayList<ParseObject> proxivents = new ArrayList<ParseObject>();
    private ProgressDialog dialog;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_joined_proxivents, null , false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_joined_proxivents);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Log.d(TAG, "OnCreateView()");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getParticipations();
        Log.d(TAG, "OnResume()");
    }

    private void getParticipations(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Participation");
        query.whereEqualTo("participant", ParseUser.getCurrentUser());
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Retrieving your participations...");
        dialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                Log.d(TAG, "Retrieved participations...");
                participations = (ArrayList) list;
                /*for (ParseObject participation : participations) {
                    getProxivent(participation.getString("proxiventId"));
                }
                adapter = new JoinedProxiventsRecyclerViewAdapter(proxivents);*/
                adapter = new JoinedProxiventsRecyclerViewAdapter(participations);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }
        });
    }

    private void getProxivent(String proxiventId) {
        Log.d(TAG, "Retrieving proxivent...");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.getInBackground(proxiventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Log.d(TAG, "Retrieved proxivent");
                Log.d(TAG,"Proxivent title= " + parseObject.getString("title"));
                Log.d(TAG,"Proxivent status= " + parseObject.getString("status"));
                proxivents.add(parseObject);
            }
        });

    }


}
