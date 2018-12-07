package com.example.android.newsfeed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import java.util.Date;
import java.text.SimpleDateFormat;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final News currentNews = getItem(position);

        /* This shows the updated title of the news event that happened today */
        TextView titleText = (TextView) listItemView.findViewById(R.id.titles);
        String output = currentNews.getTitle();
        titleText.setText(output);

        TextView authorText = (TextView) listItemView.findViewById(R.id.author);
        String authorName = currentNews.getAuthor();
        authorText.setText(authorName);

        String stringDate = currentNews.getDate();
        String[] parts = stringDate.split("(?=T)");
        String parts1 = parts[0];
        String newDate = "";

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            Date date = df.parse(parts1);
            newDate = formatDate(date);
        } catch(java.text.ParseException e) {
            Log.v("Error", "Something happened to the parsing", e);
        }



        TextView dateText = (TextView) listItemView.findViewById(R.id.date);

        dateText.setText(newDate);

        TextView sectionText = (TextView) listItemView.findViewById(R.id.section);
        sectionText.setText(currentNews.getSection());

        titleText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent titleIntent = new Intent(Intent.ACTION_VIEW);
               titleIntent.setData(Uri.parse(currentNews.getUrl()));
               getContext().startActivity(titleIntent);
           }
        });

        sectionText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent sectionIntent = new Intent(Intent.ACTION_VIEW);
               sectionIntent.setData(Uri.parse(currentNews.getUrl()));
               getContext().startActivity(sectionIntent);
           }
        });

        return listItemView;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyy");
        return dateFormat.format(dateObject);
    }
}
