package com.example.touseef.eventmanagement;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListEventAdapter extends ArrayAdapter<ListEvent> {
    public ListEventAdapter(Context context, int resources, List<ListEvent> objects){
        super(context,resources,objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.date);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.description);
        TextView descriptionTextView = (TextView)convertView.findViewById(R.id.task);
        ListEvent message = getItem(position);

        String isPhoto = message.getPhotoUrl();
        if (isPhoto == "true") {
            photoImageView.setVisibility(View.GONE);
        } else {
            photoImageView.setVisibility(View.VISIBLE);
        }
        messageTextView.setText(message.getDate());
        authorTextView.setText(message.getName());
        descriptionTextView.setText(message.getText());

        return convertView;
    }
}
