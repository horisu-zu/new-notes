package com.example.newnotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newnotesapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {

    EditText editTitle, editNote, editTag;
    ImageView imageViewSave;
    Notes notes;
    boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        editNote = findViewById(R.id.editText_note);
        editTitle = findViewById(R.id.editText_title);
        editTag = findViewById(R.id.editText_tag);
        imageViewSave = findViewById(R.id.imageViewSave);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("note");
            editTitle.setText(notes.getTitle());
            editNote.setText(notes.getNotes());
            editTag.setText(notes.getTag());
            isOldNote = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                String title = editTitle.getText().toString();
                String note = editNote.getText().toString();
                String tag = editTag.getText().toString();

                if(note.isEmpty()) {
                    Toast.makeText(NotesTakerActivity.this,
                            "Замітка не може бути пустою", Toast.LENGTH_SHORT).show();
                }

                else if(!note.isEmpty()) {

                    if(!isOldNote) {
                        notes = new Notes();
                    }

                    notes.setTitle(title);
                    notes.setTag(tag);
                    notes.setNotes(note);
                    notes.setDate(format.format(date));
                    notes.setFolder_id(1);
                    notes.setArchived(false);

                    Intent intent = new Intent();
                    intent.putExtra("note", notes);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}