package rp.soi.presence;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by geraldlim on 7/10/15.
 */
public class NearbyProxiventsRecyclerViewAdapter extends RecyclerView.Adapter<NearbyProxiventsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ParseObject> nearbyProxivents = new ArrayList<ParseObject>();
    private ViewGroup parent;
    private Intent intent;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View view;
        private TextView textView;
        private Toolbar toolbar;
        private TextView tvScreenName;


        //private final ImageButton imageButton;


        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.title_proxivent_card);
            toolbar = (Toolbar) v.findViewById(R.id.card_toolbar);
            tvScreenName = (TextView) v.findViewById(R.id.textView_proxivent_owner_name);
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

        public TextView getScreenNameTextView(){ return tvScreenName; }

        //public ImageButton getImageButton(){ return imageButton; }
    }


    public NearbyProxiventsRecyclerViewAdapter(ArrayList<ParseObject> list){
        this.nearbyProxivents = list;
    }

    @Override
    public NearbyProxiventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// create a new view
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_nearby_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;    }

    @Override
    public void onBindViewHolder(NearbyProxiventsRecyclerViewAdapter.ViewHolder holder, final int position) {
        ParseObject proxivent = nearbyProxivents.get(position);

        String title = proxivent.getString("title");
        Double distance = proxivent.getDouble("distance");
        ParseUser user = proxivent.getParseUser("owner");
        try {
            user.fetchIfNeeded();
        }catch (ParseException e){
            e.printStackTrace();
        }

        //Log.d("PRESENCE","onBindViewHolder: " + title);
        holder.getToolbar().setTitleTextColor(0xFFBB6008); //light blue
        holder.getToolbar().setTitle("Distance: " + Double.toString(distance) + " away");
        holder.getProxiventTitleTextView().setText(title);
        holder.getScreenNameTextView().setText(user.getString("screenName"));

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
                ParseObject parseObject = new ParseObject("selectedNearbyProxivent");
                parseObject.put("id", nearbyProxivents.get(position).getObjectId());
                parseObject.put("proxiventObj",nearbyProxivents.get(position));
                parseObject.pinInBackground();
                intent = new Intent(view.getContext(), NearbyProxiventDetailsActivity.class);
                intent.putExtra("ProxiventId", nearbyProxivents.get(position).getObjectId());
                Log.d("PRESENCE", "Selected proxivent at position " + position + " has ID: " + nearbyProxivents.get(position).getObjectId());
                parent.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return nearbyProxivents.size();
    }
}
