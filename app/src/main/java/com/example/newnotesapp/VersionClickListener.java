package com.example.newnotesapp;

import androidx.cardview.widget.CardView;

import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.Models.VersionNote;

public interface VersionClickListener {
    void onClick(Notes notes);

    void onLongClick(VersionNote note, CardView cardView, int position);
}
