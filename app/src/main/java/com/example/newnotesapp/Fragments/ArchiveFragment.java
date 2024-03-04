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
import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.NotesClickListener;
import com.example.newnotesapp.NotesTakerActivity;
import com.example.newnotesapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ArchiveFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notesList = new ArrayList<>();
    List<Notes> origignalNotesList = new ArrayList<>(notesList);
    RoomDB database;
    SearchView searchView_home;
    Notes selectedNote;
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

        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewArchive);
        searchView_home = view.findViewById(R.id.searchViewArchive);
        sortButton = view.findViewById(R.id.sortButtonArchive);
        sortTextView = view.findViewById(R.id.sortTypeViewArchive);
        arrowSortButton = view.findViewById(R.id.sortTypeButtonArchive);
        typeButton = view.findViewById(R.id.typeViewArchive);

        sortButton.setBackgroundColor(0);
        arrowSortButton.setBackgroundColor(0);
        typeButton.setBackgroundColor(0);

        database = RoomDB.getInstance(getContext());
        notesList = database.mainDAO().getAllArchived();

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
                        database.mainDAO().getArchivedSortedByDateReverse():
                        database.mainDAO().getArchivedSortedByNameReverse()
                        : (item_id == R.id.menu_sort_by_date) ?
                        database.mainDAO().getArchivedSortedByDate() :
                        database.mainDAO().getArchivedSortedByName();

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

        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
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
            Intent intent = new Intent(getContext(), NotesTakerActivity.class);
            intent.putExtra("note", notes);

            startActivityForResult(intent, 1648);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = notes;

            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);

        popupMenu.inflate(R.menu.archive_popup);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.delete) {
            database.mainDAO().delete(selectedNote);

            notesList.clear();
            notesList.addAll(database.mainDAO().getAllArchived());
            Toast.makeText(getContext(), "Замітку видалено", Toast.LENGTH_SHORT).show();
            notesListAdapter.notifyDataSetChanged();
            return true;
        }

        else if(itemId == R.id.unarchive) {
            database.mainDAO().archive(selectedNote.getID(), false);

            notesList.clear();
            notesList.addAll(database.mainDAO().getAllArchived());
            notesListAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Замітку поновлено", Toast.LENGTH_SHORT).show();
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
                            ? database.mainDAO().getArchivedSortedByDate()
                            : database.mainDAO().getArchivedSortedByName();
                }

                else if(arrowButtonInstance) {
                    sortedNotes = (itemId == R.id.menu_sort_by_date)
                            ? database.mainDAO().getArchivedSortedByDateReverse()
                            : database.mainDAO().getArchivedSortedByNameReverse();
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

    private void setUIEnabled(boolean enabled) {
        sortButton.setEnabled(enabled);
        arrowSortButton.setEnabled(enabled);
        searchView_home.setEnabled(enabled);
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