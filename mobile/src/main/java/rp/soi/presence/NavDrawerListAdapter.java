package rp.soi.presence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by geraldlim on 18/8/15.
 */
public class NavDrawerListAdapter extends ArrayAdapter<String> {

    private final String[] navItems;
    private Context context;

    public NavDrawerListAdapter(Context context, String[] navItems) {
        super(context, R.layout.nav_drawer_row, navItems);
        this.context = context;
        this.navItems = navItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.title);
        ImageView ivIcon = (ImageView) rowView.findViewById(R.id.icon);
        tvTitle.setText(navItems[position]);
        ivIcon.setImageResource(android.R.drawable.ic_dialog_map);
        return rowView;

    }
}
