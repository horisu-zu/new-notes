package com.example.newnotesapp;

import android.view.View;

import androidx.cardview.widget.CardView;

import com.example.newnotesapp.Models.Folder;

public interface FolderClickListener {
    void onClick(Folder folder);
    void onLongClick(CardView cardView, Folder folder);
}
