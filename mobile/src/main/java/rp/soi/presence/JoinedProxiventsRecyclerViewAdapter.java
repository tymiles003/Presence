package rp.soi.presence;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by geraldlim on 7/10/15.
 */
public class JoinedProxiventsRecyclerViewAdapter extends RecyclerView.Adapter<JoinedProxiventsRecyclerViewAdapter.ViewHolder> {

    protected static final String TAG = "JOINED";
    private ArrayList<ParseObject> participations = new ArrayList<ParseObject>();
    private ViewGroup parent;
    private Intent intent;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View view;
        private TextView textView;
        private TextView tvScreenName;


        //private final ImageButton imageButton;


        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.title_proxivent_card);
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

        public TextView getScreenNameTextView(){ return tvScreenName; }

        //public ImageButton getImageButton(){ return imageButton; }
    }


    public JoinedProxiventsRecyclerViewAdapter(ArrayList<ParseObject> list){
        this.participations = list;
    }

    @Override
    public JoinedProxiventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// create a new view
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_joined_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final JoinedProxiventsRecyclerViewAdapter.ViewHolder holder, final int position) {

        ParseObject participation = participations.get(position);
        String proxiventId = participation.getString("proxiventId");
        Log.d(TAG, "Retrieving proxivent...");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Proxivent");
        query.getInBackground(proxiventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Log.d(TAG, "Retrieved proxivent");
                ParseUser user = parseObject.getParseUser("owner");
                Log.d(TAG, "Proxivent title= " + parseObject.getString("title"));
                Log.d(TAG, "Proxivent status= " + parseObject.getString("status"));
                try {
                    user.fetchIfNeeded();
                } catch (ParseException pe){
                    pe.printStackTrace();
                }
                holder.getProxiventTitleTextView().setText(parseObject.getString("title"));
                holder.getScreenNameTextView().setText(user.getString("screenName"));
            }
        });



        /*holder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PRESENCE", "Cardview menu button clicked at position " + position);
            }
        });*/

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Log.d("PRESENCE", "Cardview at position " + position + " clicked");
                ParseObject parseObject = new ParseObject("selectedNearbyProxivent");
                parseObject.put("id", participations.get(position).getObjectId());
                parseObject.put("proxiventObj",participations.get(position));
                parseObject.pinInBackground();
                intent = new Intent(view.getContext(), NearbyProxiventDetailsActivity.class);
                intent.putExtra("ProxiventId", participations.get(position).getObjectId());
                Log.d("PRESENCE", "Selected proxivent at position " + position + " has ID: " + participations.get(position).getObjectId());
                parent.getContext().startActivity(intent);*/

            }
        });

    }

    @Override
    public int getItemCount() {
        return participations.size();
    }
}
