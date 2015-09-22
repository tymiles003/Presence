package rp.soi.presence;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by geraldlim on 18/8/15.
 */
public class NavDrawerListAdapter extends ArrayAdapter<String> {

    private final String[] navItems;
    private final String[] navImages;
    private Context context;


    public NavDrawerListAdapter(Context context, String[] navItems, String[] navImages) {
        super(context, R.layout.nav_drawer_row, navItems);
        this.context = context;
        this.navItems = navItems;
        this.navImages = navImages;

        for(int i=0; i<navItems.length;i++){
            Log.d("PRESENCE", "navItems" + i + " " + navItems[i]);
            Log.d("PRESENCE", "navImages" + i + " " + navImages[i]);

        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.title);
        ImageView ivIcon = (ImageView) rowView.findViewById(R.id.icon);
        //TextView tvCount = (TextView) rowView.findViewById(R.id.count);
        //ivIcon.setImageResource(R.mipmap.ic_bluetooth_searching_white_24dp);
        int rId = context.getResources().getIdentifier(navImages[position], "mipmap", context.getPackageName());
        ivIcon.setImageResource(rId);
        tvTitle.setText(navItems[position]);
        return rowView;

    }
}
