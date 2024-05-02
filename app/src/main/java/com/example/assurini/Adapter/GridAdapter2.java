package com.example.assurini.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatTextView;
import com.example.assurini.R;

public class GridAdapter2 extends BaseAdapter {

    private Context mContext;
    private final int[] imageResources;
    private final String[] names;

    public GridAdapter2(Context context, int[] imageResources, String[] names) {
        this.mContext = context;
        this.imageResources = imageResources;
        this.names = names;
    }

    @Override
    public int getCount() {
        return imageResources.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.company_assurance_grid, null);

            // Set ImageView and TextView
            ImageView imageView = gridView.findViewById(R.id.imageView);
            AppCompatTextView textView = gridView.findViewById(R.id.textView);
            AppCompatTextView contenu = gridView.findViewById(R.id.contenu);

            imageView.setImageResource(imageResources[position]);


            // If you have more than 2 items, the 3rd and 4th should not be shown.
            if (position >= 2) {
                gridView.setVisibility(View.GONE);
            }

        } else {
            gridView = convertView;
        }

        return gridView;
    }
}
