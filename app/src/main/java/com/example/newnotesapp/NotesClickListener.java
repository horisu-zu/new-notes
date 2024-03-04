package com.example.newnotesapp;

import androidx.cardview.widget.CardView;

import com.example.newnotesapp.Models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
