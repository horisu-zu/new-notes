package com.example.newnotesapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.collection.ArrayMap;

import com.example.newnotesapp.MainActivity;
import com.example.newnotesapp.Models.VersionNote;
import com.example.newnotesapp.VersionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    private static String readNotes() {
        StringBuilder result = new StringBuilder();
        File notesFile = new File(VersionActivity.getInstance().getFilesDir()
                .getAbsolutePath(), "version_notes.json");

        if (!notesFile.exists()) {
            try {
                notesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream in = VersionActivity.getInstance()
                    .openFileInput("version_notes.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = br.readLine()) != null)
                result.append(line).append("\n");

        } catch (Exception e) {
            Log.e("version_NOTES", e.getMessage());
        }

        return result.toString();
    }

    private static void saveNotes(String file) {
        try {
            FileOutputStream out = VersionActivity.getInstance()
                    .openFileOutput("version_notes.json", Context.MODE_PRIVATE);

            out.write(file.getBytes());
            out.close();

            Toast.makeText(VersionActivity.getInstance(), "Файл збережено!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(VersionActivity.getInstance(), "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static JSONArray createNoteJSONObject(List<VersionNote> notes) throws JSONException {
        JSONArray notesArray = new JSONArray();

        for (int i = 0; i < notes.size(); i++) {
            JSONObject noteObject = new JSONObject();

            noteObject.put("versionTitle", notes.get(i).getVersionTitle());
            noteObject.put("versionNote", notes.get(i).getVersionNote());
            noteObject.put("versionDate", notes.get(i).getVersionDate());

            notesArray.put(noteObject);
        }

        return notesArray;
    }

    public static List<VersionNote> loadNotes() {
        List<VersionNote> notes = new ArrayList<>();
        String result = readNotes();

        Log.e("version_notes", result);

        if (result.equals("")) {
            Log.d("version_notes", "No notes found in the file.");
            return notes;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray notesArray = jsonObject.getJSONArray("version_notes");

            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject noteObject = notesArray.getJSONObject(i);

                String title = noteObject.optString("versionTitle", "");
                String desc = noteObject.optString("versionNote", "");
                String date = noteObject.optString("versionDate", "");

                notes.add(0, new VersionNote(title, date, desc));
            }
        } catch (JSONException e) {
            Log.e("version_notes", "Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return notes;
    }



    public static void saveNote(String title, String note, String date) {
        String result = readNotes();

        Log.e("version_notes", result);

        try {
            JSONObject jsonObject;
            JSONObject noteObject = new JSONObject();

            JSONArray notesArray = null;

            if (result.equals("")) {
                jsonObject = new JSONObject();
                notesArray = new JSONArray();
            } else {
                jsonObject = new JSONObject(result);
                notesArray = jsonObject.getJSONArray("version_notes");
            }

            noteObject.put("versionTitle", title);
            noteObject.put("versionNote", note);
            noteObject.put("versionDate", date);

            notesArray.put(noteObject);
            jsonObject.put("version_notes", notesArray);

            Log.e("version_notes", jsonObject.toString());

            saveNotes(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveNotes() {
        JSONObject jsonObject = new JSONObject();

        try {
            List<VersionNote> versionNotesData = VersionActivity.getInstance().versionNoteList;

            jsonObject.put("version_notes", createNoteJSONObject(versionNotesData));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveNotes(jsonObject.toString());
    }
}
