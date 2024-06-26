package com.example.newnotesapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newnotesapp.Adapters.NotesListAdapter;
import com.example.newnotesapp.Database.RoomDB;
import com.example.newnotesapp.Models.Folder;
import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.NotesClickListener;
import com.example.newnotesapp.NotesTakerActivity;
import com.example.newnotesapp.R;

import java.util.ArrayList;
import java.util.List;

public class NotesFolderFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private long folderId;
    private String folderTitle;

    public NotesFolderFragment(long folderId, String folderTitle) {
        this.folderId = folderId;
        this.folderTitle = folderTitle;
    }

    List<Notes> notesList;
    List<Folder> folders;
    NotesListAdapter notesListAdapter;
    RecyclerView recyclerView;
    RoomDB database;
    Notes selectedNote;
    TextView folderTitleView;
    ImageButton sortButton;
    TextView sortTextView;
    ImageButton arrowSortButton;
    ImageButton typeButton;
    private boolean arrowButtonInstance = false;
    private boolean isNetType = true;
    private int item_id = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes_folder, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        sortButton = view.findViewById(R.id.sortButton);
        sortTextView = view.findViewById(R.id.sortTypeView);
        arrowSortButton = view.findViewById(R.id.sortTypeButton);
        typeButton = view.findViewById(R.id.typeView);
        folderTitleView = view.findViewById(R.id.folderTitle);
        database = RoomDB.getInstance(getContext());

        sortButton.setBackgroundColor(0);
        arrowSortButton.setBackgroundColor(0);
        typeButton.setBackgroundColor(0);

        folders = database.mainDAO().getAllFolders();
        folderTitleView.setText(folderTitle);

        notesList = database.mainDAO().getAllByFolderId(folderId);

        updateRecycler(notesList);

        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypePopupMenu(view);
            }
        });

        arrowSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Notes> sortedNotes = new ArrayList<>();

                arrowButtonInstance = !arrowButtonInstance;
                int newImageResource = arrowButtonInstance
                        ? R.drawable.ic_arrow_upward
                        : R.drawable.ic_arrow_type;
                arrowSortButton.setImageResource(newImageResource);

                sortedNotes = arrowButtonInstance
                        ? (item_id == R.id.menu_sort_by_date) ?
                        database.mainDAO().getAllSortedByDateReverseInFolder(folderId) :
                        database.mainDAO().getAllSortedByNameReverseInFolder(folderId)
                        : (item_id == R.id.menu_sort_by_date) ?
                        database.mainDAO().getAllSortedByDateInFolder(folderId) :
                        database.mainDAO().getAllSortedByNameInFolder(folderId);

                notesList.clear();
                notesList.addAll(sortedNotes);
                notesListAdapter.notifyDataSetChanged();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortPopupMenu(view);
            }
        });

        return view;
    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();

        for(Notes singleNote : notesList) {
            if(singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }

        notesListAdapter.filterList(filteredList);
        notesListAdapter.notifyDataSetChanged();
    }

    private void updateRecycler(List<Notes> notesList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL));

        notesListAdapter = new NotesListAdapter(getContext(),
                notesList, notesClickListener);

        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            /*Intent intent = new Intent(getContext(), NotesTakerActivity.class);
            intent.putExtra("note", notes);

            startActivityForResult(intent, 1648);*/
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = notes;

            showPopup(cardView);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == 1618) {
            if(resultCode == Activity.RESULT_OK) {
                Notes n_notes = (Notes) data.getSerializableExtra("note");

                database.mainDAO().insert(n_notes);
                notesList.clear();
                notesList.addAll(database.mainDAO().getAllByFolderId(folderId));
                notesListAdapter.notifyDataSetChanged();
            }
        }*/

        if(requestCode == 1648) {
            if(resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(),
                        new_notes.getTag(), new_notes.getNotes(), new_notes.getColor());
                notesList.clear();
                notesList.addAll(database.mainDAO().getAllByFolderId(folderId));
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);

        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.pin_view) {
            if (selectedNote.isPin()) {
                database.mainDAO().pin(selectedNote.getID(), false);
                Toast.makeText(getContext(), "Відкріплено", Toast.LENGTH_SHORT).show();
            } else if(!selectedNote.isPin()) {
                database.mainDAO().pin(selectedNote.getID(), true);
                Toast.makeText(getContext(), "Закріплено",
                        Toast.LENGTH_SHORT).show();
            }

            notesList.clear();
            notesList.addAll(database.mainDAO().getAllByFolderId(folderId));
            notesListAdapter.notifyDataSetChanged();
            return true;
        }

        else if(itemId == R.id.archive) {
            if(selectedNote.isPin())
                Toast.makeText(getContext(), "Неможливо архівувати закріплену замітку",
                        Toast.LENGTH_SHORT).show();
            else if (!selectedNote.isPin()) {
                database.mainDAO().archive(selectedNote.getID(), true);

                notesList.clear();
                notesList.addAll(database.mainDAO().getAllByFolderId(folderId));
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Архівовано", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if (itemId == R.id.toFolder) {
            DialogFragment addToFolderDialogFragment = new DialogFragment(folders, selectedNote,
                    database);
            if(getFragmentManager() != null) {
                addToFolderDialogFragment.show(getFragmentManager(), "AddToFolderFragment");
            }
            return true;
        }
        return false;
    }

    private void showSortPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            item_id = itemId;

            if (itemId == R.id.menu_sort_by_date || itemId == R.id.menu_sort_by_name) {

                List<Notes> sortedNotes = new ArrayList<>();

                if(!arrowButtonInstance) {
                    sortedNotes = (itemId == R.id.menu_sort_by_date)
                            ? database.mainDAO().getAllSortedByDateInFolder(folderId) :
                            database.mainDAO().getAllSortedByNameInFolder(folderId);
                }

                else if(arrowButtonInstance) {
                    sortedNotes = (itemId == R.id.menu_sort_by_date)
                            ? database.mainDAO().getAllSortedByDateReverseInFolder(folderId) :
                            database.mainDAO().getAllSortedByNameReverseInFolder(folderId);
                }

                notesList.clear();
                notesList.addAll(sortedNotes);
                notesListAdapter.notifyDataSetChanged();

                String textView = (itemId == R.id.menu_sort_by_date)
                        ? "Дата створення"
                        : "Назва";

                String toastMessage = (itemId == R.id.menu_sort_by_date)
                        ? "Відсортовано за датою створення"
                        : "Відсортовано за назвою";
                Toast.makeText(getContext(), toastMessage,
                        Toast.LENGTH_SHORT).show();

                sortTextView.setText(textView);

                return true;
            }
            return false;
        });

        popupMenu.inflate(R.menu.sort_popup);
        popupMenu.show();
    }

    private void showTypePopupMenu(View view) {
        PopupMenu typePopupMenu = new PopupMenu(getContext(), view);

        typePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if(itemId == R.id.type_net || itemId == R.id.type_list) {
                    if (itemId == R.id.type_net) {
                        if(isNetType) {
                            Toast.makeText(getContext(), "Уже обрано тип \"Сітка\"",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                                    LinearLayoutManager.VERTICAL));
                            isNetType = !isNetType;
                        }
                    } else if (itemId == R.id.type_list) {
                        if(!isNetType) {
                            Toast.makeText(getContext(), "Уже обрано тип \"Список\"",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));
                            isNetType = !isNetType;
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        typePopupMenu.inflate(R.menu.type_popup);
        typePopupMenu.show();
    }
}