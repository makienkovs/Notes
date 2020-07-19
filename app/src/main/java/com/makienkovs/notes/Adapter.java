package com.makienkovs.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    private ArrayList<Note> notes;
    private Context c;
    private Note note;

    public Adapter(ArrayList<Note> notes, Context c) {
        this.notes = notes;
        this.c = c;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(c).inflate(R.layout.note, null);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                notifyDataSetChanged();
                note = (Note) getItem(position);
                for (int i = 0; i < notes.size() ; i++) {
                    notes.get(i).setSelected(false);
                }
                note.setSelected(true);
            }
        });

        fillView(convertView, position);

        return convertView;
    }

    void nullNote() {
        if (note == null) return;
        note.setSelected(false);
        note = null; }

    Note getNote() {
        return note;
    }

    private void fillView(View v, int position) {
        final Note n = (Note) getItem(position);
        TextView content = v.findViewById(R.id.content);
        TextView time = v.findViewById(R.id.time);
        String timeString = (String) DateFormat.format("dd.MM.yyyy, HH.mm.ss", n.getTime());

        if (n.getContent() == null)
            content.setText("");
        else if (n.isCancel())
            content.setText(Html.fromHtml("<s>" + n.getContent() + "</s>", Html.FROM_HTML_MODE_LEGACY));
        else
            content.setText(n.getContent());

        if (n.getTime() == 0)
            time.setText("");
        else if (n.isCancel())
            time.setText(Html.fromHtml("<s>" + timeString + "</s>", Html.FROM_HTML_MODE_LEGACY));
        else
            time.setText(timeString);

        if (n.isSelected() && n.isDone()) {
            v.setBackgroundColor(Color.rgb(216, 191, 93));
        } else if (n.isSelected()) {
            v.setBackgroundColor(Color.rgb(212, 212, 212));
        } else if (n.isDone()) {
            v.setBackgroundColor(Color.rgb(255, 221, 54));
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}