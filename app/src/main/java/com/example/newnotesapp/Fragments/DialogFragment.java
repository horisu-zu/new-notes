    package com.example.newnotesapp.Fragments;

    import android.app.AlertDialog;
    import android.app.Dialog;
    import android.content.DialogInterface;
    import android.os.Bundle;
    import android.widget.Toast;

    import androidx.annotation.NonNull;

    import com.example.newnotesapp.Adapters.NotesListAdapter;
    import com.example.newnotesapp.Database.RoomDB;
    import com.example.newnotesapp.Models.Folder;
    import com.example.newnotesapp.Models.Notes;

    import java.util.ArrayList;
    import java.util.List;

    public class DialogFragment extends androidx.fragment.app.DialogFragment {
        private List<Folder> folderList;
        private List<Notes> notesList = new ArrayList<>();
        private Notes selectedNote;
        private RoomDB database;

        public DialogFragment(List<Folder> folderList, Notes selectedNote, RoomDB database) {
            this.folderList = folderList;
            this.selectedNote = selectedNote;
            this.database = database;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Оберіть папку")
                    .setItems(getFolderNames(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Folder selectedFolder = folderList.get(which);
                            addToFolder(selectedNote, selectedFolder);
                        }
                    });
            return builder.create();
        }

        private String[] getFolderNames() {
            String[] folderNames = new String[folderList.size()];
            for (int i = 0; i < folderList.size(); i++) {
                folderNames[i] = folderList.get(i).getTagTitle();
            }
            return folderNames;
        }

        private void addToFolder(Notes notes, Folder folder) {

            database.mainDAO().addToFolder(notes.getID(), folder.getFolder_id());

            notesList.clear();
            notesList.addAll(database.mainDAO().getAll());

            Toast.makeText(getContext(), "Добавлено в папку: " + folder.getTagTitle(),
                    Toast.LENGTH_SHORT).show();
        }
    }
