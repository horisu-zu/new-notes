package com.example.newnotesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.newnotesapp.Adapters.VersionAdapter;
import com.example.newnotesapp.Models.VersionNote;
import com.example.newnotesapp.utils.Utils;

import java.util.List;

public class VersionActivity extends AppCompatActivity {

    private static VersionActivity instance;

    Button createButton;
    RecyclerView recyclerView;

    public List<VersionNote> versionNoteList;

    VersionAdapter versionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        instance = this;

        createButton = findViewById(R.id.versionCreate);

        recyclerView = findViewById(R.id.versionNotesRecycler);

        loadVersionNotes();

        Log.d("VersionActivity", "Number of notes loaded: " + versionNoteList.size());

        versionAdapter = new VersionAdapter(instance, versionNoteList);

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

    private void loadVersionNotes() {
       versionNoteList = Utils.loadNotes();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNote(VersionNote versionNote) {
        versionNoteList.add(versionNote);
        versionAdapter.notifyDataSetChanged();
        Utils.saveNotes();
    }

    public static VersionActivity getInstance() {
        return instance;
    }
}