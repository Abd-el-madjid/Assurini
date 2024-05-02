package com.example.assurini.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.assurini.R;

public class GridAdapter3 extends BaseAdapter {

    private Context mContext;
    private final int[] imageResources;
    private final String[] names;

    public GridAdapter3(Context context, int[] imageResources, String[] names) {
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
            gridView = inflater.inflate(R.layout.type_assurance_grid_grand, null);
        } else {
            gridView = convertView;
        }

        // Set ImageView and TextView
        ImageView imageView = gridView.findViewById(R.id.imageView);
        AppCompatTextView textView = gridView.findViewById(R.id.textView);
        AppCompatTextView contenu = gridView.findViewById(R.id.contenu);

        // Check if position is within the bounds of imageResources and names arrays
        if (position < imageResources.length && position < names.length) {
            // Set image resource and name for the current position
            imageView.setImageResource(imageResources[position]);
            textView.setText(names[position]);
            contenu.setText("Your Content"); // Set your content here
        }

        return gridView;
    }
}
