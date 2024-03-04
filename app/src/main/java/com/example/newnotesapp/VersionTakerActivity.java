package com.example.newnotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.newnotesapp.Models.VersionNote;
import com.example.newnotesapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VersionTakerActivity extends AppCompatActivity {

    EditText versionTitle, versionNoteText;
    ImageView saveButton;
    VersionNote versionNote;

    boolean isOldVersion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_notes_taker);

        saveButton = findViewById(R.id.imageViewSave);
        versionNoteText = findViewById(R.id.editText_note);
        versionTitle = findViewById(R.id.editText_title);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                String title = versionTitle.getText().toString();
                String note = versionNoteText.getText().toString();

                versionNote = new VersionNote();

                versionNote.setVersionTitle(title);
                versionNote.setVersionNote(note);
                versionNote.setVersionDate(format.format(date));

                VersionActivity.getInstance().addNote(versionNote);

                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}