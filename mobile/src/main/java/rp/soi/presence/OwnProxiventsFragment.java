package rp.soi.presence;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OwnProxiventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ParseObject> ownProxivents = new ArrayList<ParseObject>();
    private String[] mDataset;
    private ProgressDialog dialog;


    private static final int DATASET_COUNT = 60;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initDataset();
        initProxivents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_own_proxivents, null , false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_own_proxivents);
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        //adapter = new OwnProxiventsRecyclerViewAdapter(mDataset);
        //adapter = new OwnProxiventsRecyclerViewAdapter(ownProxivents);
        //recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment

        ImageButton btnAdd = (ImageButton) v.findViewById(R.id.button_add_new_proxivent);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateProxiventFragment newProxivent = new CreateProxiventFragment();
                FragmentManager fragmentManager = getFragmentManager();
                newProxivent.show(fragmentManager,"PRESENCE");
            }
        });
        return v;
    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }

    private void initProxivents() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Retrieving your proxivents...");
        dialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ownProxivents = (ArrayList) list;
                if (e == null) {
                    for (ParseObject proxivent : list) {
                        Log.d("PRESENCE", "After ParseQuery: " + proxivent.getString("title"));
                        //ownProxivents.add(proxivent);
                    }
                } else {
                    e.printStackTrace();
                }
                dialog.dismiss();
                adapter = new OwnProxiventsRecyclerViewAdapter(ownProxivents);
                recyclerView.setAdapter(adapter);
            }
        });
    }

}
