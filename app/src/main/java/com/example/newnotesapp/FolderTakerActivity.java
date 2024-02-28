package com.example.newnotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newnotesapp.Database.RoomDB;
import com.example.newnotesapp.Models.Folder;
import com.example.newnotesapp.Models.Notes;

public class FolderTakerActivity extends AppCompatActivity {

    RoomDB database;
    EditText editFolderTitle;
    ImageView imageViewSave;
    Folder folder;
    boolean isOldFolder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_taker);

        editFolderTitle = findViewById(R.id.editText_FolderTitle);
        imageViewSave = findViewById(R.id.imageViewSave);

        folder = new Folder();

        try {
            folder = (Folder) getIntent().getSerializableExtra("folder");
            editFolderTitle.setText(folder.getTagTitle());
            isOldFolder = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editFolderTitle.getText().toString();

                if(title.isEmpty()) {
                    Toast.makeText(FolderTakerActivity.this,
                            "Назва папки не може бути пустою", Toast.LENGTH_SHORT).show();
                }
                else {

                    if(!isOldFolder) {
                        folder = new Folder();
                    }

                    folder.setTagTitle(title);
                    folder.setItemCount(0);

                    Intent intent = new Intent();
                    intent.putExtra("folder", folder);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}