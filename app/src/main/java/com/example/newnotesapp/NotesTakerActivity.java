package com.example.newnotesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newnotesapp.Database.RoomDB;
import com.example.newnotesapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

import yuku.ambilwarna.AmbilWarnaDialog;

public class NotesTakerActivity extends AppCompatActivity {

    EditText editTitle, editNote, editTag;
    ImageView imageViewSave, imageViewColor;
    Notes notes;
    boolean isOldNote = false;
    int color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        editNote = findViewById(R.id.editText_note);
        editTitle = findViewById(R.id.editText_title);
        editTag = findViewById(R.id.editText_tag);
        imageViewSave = findViewById(R.id.imageViewSave);
        imageViewColor = findViewById(R.id.imageViewColor);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("note");
            editTitle.setText(notes.getTitle());
            editNote.setText(notes.getNotes());
            editTag.setText(notes.getTag());
            color = notes.getColor();
            isOldNote = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        imageViewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog(color);
            }
        });

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
                    notes.setColor(color);
                    notes.setArchived(false);

                    Intent intent = new Intent();
                    intent.putExtra("note", notes);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void showColorPickerDialog(int colorSet) {
        AmbilWarnaDialog colorPickerDialog;
        //if(note != null) {
            colorPickerDialog = new AmbilWarnaDialog(this, colorSet,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int new_color) {
                            color = new_color;

                            Toast.makeText(NotesTakerActivity.this,
                                    "Обрано колір: #" + Integer.toHexString(color),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                        }
                    });
            colorPickerDialog.show();
    }

    /*private int setColorForNewNote() {
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(this, 0,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int new_color) {
                        color = new_color;
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }
                });
        colorPickerDialog.show();
        return color;
    }*/
}