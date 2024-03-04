package com.example.newnotesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.example.newnotesapp.Adapters.VersionAdapter;
import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.Models.VersionNote;
import com.example.newnotesapp.utils.Utils;

import java.util.List;

public class VersionActivity extends AppCompatActivity {
    private static VersionActivity instance;
    Button createButton;
    RecyclerView recyclerView;
    public List<VersionNote> versionNoteList;
    VersionNote selectedNote;
    public VersionAdapter versionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        instance = this;

        createButton = findViewById(R.id.versionCreate);

        recyclerView = findViewById(R.id.versionNotesRecycler);

        loadVersionNotes();

        Log.d("VersionActivity", "Number of notes loaded: " + versionNoteList.size());

        versionAdapter = new VersionAdapter(instance, versionNoteList, versionClickListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(versionAdapter);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VersionActivity.getInstance(),
                        VersionTakerActivity.class);
                startActivity(intent);
            }
        });
    }

    private final VersionClickListener versionClickListener = new VersionClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(VersionActivity.getInstance(), VersionTakerActivity.class);
            intent.putExtra("note", notes);

            startActivityForResult(intent, 1);
        }

        @Override
        public void onLongClick(VersionNote note, CardView cardView, int position) {
            selectedNote = note;
            showPopupMenu(cardView, selectedNote, position);
        }
    };

    private void showPopupMenu(CardView cardView, VersionNote versionNote, int position) {
        PopupMenu popupMenu = new PopupMenu(cardView.getContext(), cardView);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.version_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.edit) {
                    return true;
                } else if (itemId == R.id.delete) {
                    Utils.deleteNote(position);
                    removeItem(position);
                    versionAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    public void removeItem(int position) {
        versionNoteList.remove(position);
    }

    private void loadVersionNotes() {
       versionNoteList = Utils.loadNotes();
    }

    public void addNote(VersionNote versionNote) {
        versionNoteList.add(versionNote);
        Utils.saveNotes();
    }

    public static VersionActivity getInstance() {
        return instance;
    }
}