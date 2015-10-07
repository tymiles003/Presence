package rp.soi.presence;



import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.ParseObject;
import java.util.ArrayList;

/**
 * Created by geraldlim on 26/9/15.
 */
public class OwnProxiventsRecyclerViewAdapter extends RecyclerView.Adapter<OwnProxiventsRecyclerViewAdapter.ViewHolder> {
    private String[] mDataset;
    private ArrayList<ParseObject> ownProxivents = new ArrayList<ParseObject>();
    private ViewGroup parent;
    private Intent intent;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final View view;
        private final TextView textView;
        private final Toolbar toolbar;


        //private final ImageButton imageButton;


        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.title_proxivent_card);
            toolbar = (Toolbar) v.findViewById(R.id.card_toolbar);
            view = v;
            //imageButton = (ImageButton) v.findViewById(R.id.btn_cardview_menu);


        }

        public View getView() {
            return view;
        }

        public TextView getProxiventTitleTextView() {
            return textView;
        }

        public Toolbar getToolbar() {
            return toolbar;
        }

        //public ImageButton getImageButton(){ return imageButton; }
    }


    // Provide a suitable constructor (depends on the kind of dataset)


    public OwnProxiventsRecyclerViewAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    public OwnProxiventsRecyclerViewAdapter(ArrayList<ParseObject> myDataset) {
        this.ownProxivents = myDataset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_proxivent_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        //holder.getProxiventTitleTextView().setText(mDataset[position]);
        ParseObject proxivent = ownProxivents.get(position);

        String title = proxivent.getString("title");
        //Log.d("PRESENCE","onBindViewHolder: " + title);
        String status = proxivent.getString("status");
        if (status.equalsIgnoreCase("inactive")) {
            holder.getToolbar().setTitleTextColor(0xFFFF1816); //red
        } else {
            holder.getToolbar().setTitleTextColor(0xFF27FF1E); //green
        }
        holder.getToolbar().setTitle(status);
        holder.getProxiventTitleTextView().setText(title);

        /*holder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PRESENCE", "Cardview menu button clicked at position " + position);
            }
        });*/
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("PRESENCE", "Cardview at position " + position + " clicked");
                intent = new Intent(view.getContext(), ProxiventDetailsActivity.class);
                intent.putExtra("ProxiventId", ownProxivents.get(position).getObjectId());
                Log.d("PRESENCE", "Selected proxivent at position " + position + " has ID: " + ownProxivents.get(position).getObjectId());
                parent.getContext().startActivity(intent);

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
