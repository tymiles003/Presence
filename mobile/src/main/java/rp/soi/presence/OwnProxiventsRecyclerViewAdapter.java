package rp.soi.presence;

import android.app.ProgressDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        public ViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.title_proxivent_card);
        }

        public TextView getProxiventTitleTextView(){
            return textView;
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)



    public OwnProxiventsRecyclerViewAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    public OwnProxiventsRecyclerViewAdapter(ArrayList<ParseObject> myDataset) {
        ownProxivents = myDataset;
        Log.d("PRESENCE", "RecyclerView constructor: num items: " + myDataset.size());
        for(ParseObject obj: myDataset){
            Log.d("PRESENCE", "RecyclerView constructor: " + obj.getString("title"));
            //ownProxivents.add(obj);
        }
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        //holder.getProxiventTitleTextView().setText(mDataset[position]);
        String title = ownProxivents.get(position).getString("title");
        Log.d("PRESENCE","onBindViewHolder: " + title);
        holder.getProxiventTitleTextView().setText(title);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ownProxivents.size();
        //return mDataset.length;

    }

}
