package com.example.newnotesapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.newnotesapp.Adapters.NotesListAdapter;
import com.example.newnotesapp.Database.RoomDB;
import com.example.newnotesapp.Models.Folder;
import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.NotesClickListener;
import com.example.newnotesapp.NotesTakerActivity;
import com.example.newnotesapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class NotesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener,
        FragmentMenu.MenuFragmentInteractionListener{
    RecyclerView recyclerView;
    CardView selectedItem;
    CardView pinnedViewCard;
    NotesListAdapter notesListAdapter;
    List<Notes> notesList = new ArrayList<>();
    List<Notes> pinnedNotes, notPinnedNotes;
    List<Folder> folders;
    RoomDB database;
    FloatingActionButton fab_add;
    Folder baseFolder;
    SearchView searchView_home;
    Notes selectedNote;
    ImageButton sortButton;
    ImageButton pinViewButton;
    TextView sortTextView;
    ImageButton arrowSortButton;
    ImageButton typeButton;
    int selectedColor = Color.WHITE;
    private boolean arrowButtonInstance = false;
    private boolean isMenuVisible = false;
    private boolean isFunctionalityEnabled = true;
    private boolean isNetType = true;
    private boolean isPinnedButtonActive = false;
    private int item_id = 0;
    private boolean isSortByName = false;
    private boolean isArrowUp = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        fab_add = view.findViewById(R.id.createButton);
        searchView_home = view.findViewById(R.id.searchView);
        sortButton = view.findViewById(R.id.sortButton);
        sortTextView = view.findViewById(R.id.sortTypeView);
        arrowSortButton = view.findViewById(R.id.sortTypeButton);
        pinViewButton = view.findViewById(R.id.pinnedItemsView);
        typeButton = view.findViewById(R.id.typeView);
        pinnedViewCard = view.findViewById(R.id.pinnedItemsCard);

        sortButton.setBackgroundColor(0);
        arrowSortButton.setBackgroundColor(0);
        typeButton.setBackgroundColor(0);
        pinViewButton.setBackgroundColor(0);
        pinnedViewCard.setBackgroundResource(R.drawable.default_card_background);

        database = RoomDB.getInstance(getContext());
        notesList = database.mainDAO().getAll();
        folders = database.mainDAO().getAllFolders();

        updateRecycler(notesList);

        notesList.addAll(getNotesByStatus());

        checkEmptyFolder();

        pinViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesList.clear();

                if(!isPinnedButtonActive) {

                    pinnedNotes = database.mainDAO().getAllNotesByPinStatus(true);
                    notPinnedNotes = database.mainDAO().getAllNotesByPinStatus(false);

                    pinnedViewCard.setBackgroundResource(R.drawable.active_pinned_card);
                    //pinViewButton.setBackgroundColor(getResources().getColor(R.color.color3));
                    setUIEnabled(false);

                    pinnedNotes.addAll(notPinnedNotes);
                    notesList.addAll(pinnedNotes);
                    notesListAdapter.filterList(notesList);
                    notesListAdapter.notifyDataSetChanged();
                }
                else if(isPinnedButtonActive) {
                    pinnedViewCard.setBackgroundResource(R.drawable.default_card_background);
                    //pinViewButton.setBackgroundColor(0);

                    setUIEnabled(true);
                    notesList.addAll(getNotesByStatus());
                    notesListAdapter.filterList(notesList);
                    notesListAdapter.notifyDataSetChanged();
                }

                isPinnedButtonActive = !isPinnedButtonActive;
            }
        });

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
                        database.mainDAO().getAllSortedByDateReverse() :
                        database.mainDAO().getAllSortedByNameReverse()
                        : (item_id == R.id.menu_sort_by_date) ?
                        database.mainDAO().getAllSortedByDate() :
                        database.mainDAO().getAllSortedByName();

                isArrowUp = arrowButtonInstance;

                notesList.clear();
                notesList.addAll(sortedNotes);
                notesListAdapter.filterList(notesList);
                notesListAdapter.notifyDataSetChanged();
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NotesTakerActivity.class);
                startActivityForResult(intent, 1618);
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
                setFilterSearch(newText);
                return false;
            }
        });

        return view;
    }

    private void setFilterSearch(String query) {
        List<Notes> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(notesList);
        } else if (query.startsWith("#")) {
            String tagQuery = query.substring(1).toLowerCase();

            for (Notes singleNote : notesList) {
                if (singleNote.getTag().toLowerCase().contains(tagQuery)) {
                    filteredList.add(singleNote);
                }
            }
        } else {
            String searchString = query.toLowerCase();

            for (Notes singleNote : notesList) {
                String title = singleNote.getTitle().toLowerCase();
                String notes = singleNote.getNotes().toLowerCase();

                if (title.contains(searchString) || notes.contains(searchString)) {
                    filteredList.add(singleNote);
                }
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
            selectedItem = cardView;

            showPopup(cardView);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1618) {
            if(resultCode == Activity.RESULT_OK) {
                Notes n_notes = (Notes) data.getSerializableExtra("note");

                database.mainDAO().insert(n_notes);
                notesList.addAll(getNotesByStatus());
                notesListAdapter.notifyDataSetChanged();
            }
        }

        else if(requestCode == 1648) {
            if(resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(),
                        new_notes.getTag(), new_notes.getNotes(), new_notes.getColor());
                notesList.addAll(getNotesByStatus());
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
            notesList.clear();
            if (selectedNote.isPin()) {
                database.mainDAO().pin(selectedNote.getID(), false);
                Toast.makeText(getContext(), "Відкріплено", Toast.LENGTH_SHORT).show();
            } else if (!selectedNote.isPin()) {
                database.mainDAO().pin(selectedNote.getID(), true);
                Toast.makeText(getContext(), "Закріплено",
                        Toast.LENGTH_SHORT).show();
            }

            if (isPinnedButtonActive) {
                pinnedNotes = database.mainDAO().getAllNotesByPinStatus(true);
                notPinnedNotes = database.mainDAO().getAllNotesByPinStatus(false);

                pinnedNotes.addAll(notPinnedNotes);
                notesList.addAll(pinnedNotes);
                notesListAdapter.notifyDataSetChanged();
            } else {
                notesList.addAll(getNotesByStatus());
                notesListAdapter.notifyDataSetChanged();
            }

            return true;
        } else if (itemId == R.id.archive) {
            if (selectedNote.isPin())
                Toast.makeText(getContext(), "Неможливо архівувати закріплену замітку",
                        Toast.LENGTH_SHORT).show();
            else if (!selectedNote.isPin()) {
                database.mainDAO().archive(selectedNote.getID(), true);
                notesList.clear();

                if (isPinnedButtonActive) {
                    pinnedNotes = database.mainDAO().getAllNotesByPinStatus(true);
                    notPinnedNotes = database.mainDAO().getAllNotesByPinStatus(false);

                    pinnedNotes.addAll(notPinnedNotes);
                    notesList.addAll(pinnedNotes);
                } else {
                    notesList.addAll(getNotesByStatus());
                    notesListAdapter.notifyDataSetChanged();
                }

                Toast.makeText(getContext(), "Архівовано", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.toFolder) {
            DialogFragment addToFolderDialogFragment = new DialogFragment(folders, selectedNote,
                    database);
            if(getFragmentManager() != null) {
                addToFolderDialogFragment.show(getFragmentManager(), "AddToFolderFragment");
            }
            return true;
        }
        else if(itemId == R.id.colorSet) {
            showColorPickerDialog();
            return true;
        }

        return false;
    }

    private void showColorPickerDialog() {
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(getContext(),
                selectedNote.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        selectedColor = color;
                        database.mainDAO().updateColor(selectedNote.getID(), selectedColor);
                        notesList.addAll(getNotesByStatus());
                        notesListAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Обрано колір: #" +
                                        Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }
                });

        colorPickerDialog.show();
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
                            ? database.mainDAO().getAllSortedByDate()
                            : database.mainDAO().getAllSortedByName();
                    isSortByName = itemId != R.id.menu_sort_by_date;
                    /*isSortByName = (itemId == R.id.menu_sort_by_date)
                            ? false
                            : true;*/
                }

                else if(arrowButtonInstance) {
                    sortedNotes = (itemId == R.id.menu_sort_by_date)
                            ? database.mainDAO().getAllSortedByDateReverse()
                            : database.mainDAO().getAllSortedByNameReverse();
                    isSortByName = itemId != R.id.menu_sort_by_date;
                }

                notesList.clear();
                notesList.addAll(sortedNotes);
                notesListAdapter.filterList(notesList);
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

    private void checkEmptyFolder() {
        if(database.mainDAO().getFolder() == null) {
            baseFolder = new Folder();
            baseFolder.setTagTitle("Default Folder");
            baseFolder.setFolder_id(1);
            baseFolder.setItemCount(0);

            database.mainDAO().insertFolder(baseFolder);
        }
    }

    private List<Notes> getNotesByStatus() {
        List<Notes> sortedNotes = new ArrayList<>();
        notesList.clear();

        if(isSortByName) {
            if(isArrowUp) {
                sortedNotes.addAll(database.mainDAO().getAllSortedByNameReverse());
            }
            else
                sortedNotes.addAll(database.mainDAO().getAllSortedByName());
        } else{
            if(isArrowUp) {
                sortedNotes.addAll(database.mainDAO().getAllSortedByDateReverse());
            }
            else
                sortedNotes.addAll(database.mainDAO().getAllSortedByDate());
        }

        //notesListAdapter.notifyDataSetChanged();

        return sortedNotes;
    }

    @Override
    public void onMenuFragmentDisplayed() {
        setUIEnabled(false);
    }

    @Override
    public void onMenuFragmentHidden() {
        setUIEnabled(true);
    }
}
