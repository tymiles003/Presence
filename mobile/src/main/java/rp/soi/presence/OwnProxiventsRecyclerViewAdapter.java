package rp.soi.presence;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geraldlim on 26/9/15.
 */
public class OwnProxiventsRecyclerViewAdapter extends RecyclerView.Adapter<OwnProxiventsRecyclerViewAdapter.ViewHolder>{
    private String[] mDataset;
    private ArrayList<ParseObject> ownProxivents = new ArrayList<ParseObject>();


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView textView;
        private final Toolbar toolbar;
        private final ImageButton imageButton;


        public ViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.title_proxivent_card);
            toolbar = (Toolbar) v.findViewById(R.id.card_toolbar);
            imageButton = (ImageButton) v.findViewById(R.id.btn_cardview_menu);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("PRESENCE", "Cardview at position " + getPosition() + " clicked");
                }
            });

        }

        public TextView getProxiventTitleTextView(){ return textView; }

        public Toolbar getToolbar(){ return toolbar; }

        public ImageButton getImageButton(){ return imageButton; }
    }


    // Provide a suitable constructor (depends on the kind of dataset)



    public OwnProxiventsRecyclerViewAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    public OwnProxiventsRecyclerViewAdapter(ArrayList<ParseObject> myDataset) {
        ownProxivents = myDataset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_proxivent_cardview, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        //holder.getProxiventTitleTextView().setText(mDataset[position]);

        String title = ownProxivents.get(position).getString("title");
        //Log.d("PRESENCE","onBindViewHolder: " + title);
        holder.getToolbar().setTitle(ownProxivents.get(position).getString("status"));
        holder.getProxiventTitleTextView().setText(title);

        holder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PRESENCE", "Cardview menu button clicked at position " + position);
            }
        });



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ownProxivents.size();
        //return mDataset.length;

    }

}
