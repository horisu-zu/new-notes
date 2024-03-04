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

import com.example.newnotesapp.Adapters.FolderAdapter;
import com.example.newnotesapp.Adapters.NotesListAdapter;
import com.example.newnotesapp.Database.RoomDB;
import com.example.newnotesapp.FolderClickListener;
import com.example.newnotesapp.FolderTakerActivity;
import com.example.newnotesapp.Models.Folder;
import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.NotesClickListener;
import com.example.newnotesapp.NotesTakerActivity;
import com.example.newnotesapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class FolderFragment extends Fragment{
    RecyclerView recyclerView;
    FolderAdapter folderAdapter;
    FloatingActionButton floatingActionButton;
    List<Folder> folders = new ArrayList<>();
    RoomDB database;
    SearchView searchView_home;
    Folder selectedFolder;
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

        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchView_home = view.findViewById(R.id.searchView);
        sortButton = view.findViewById(R.id.sortButton);
        sortTextView = view.findViewById(R.id.sortTypeView);
        arrowSortButton = view.findViewById(R.id.sortTypeButton);
        typeButton = view.findViewById(R.id.typeView);
        floatingActionButton = view.findViewById(R.id.createButton);

        sortButton.setBackgroundColor(0);
        arrowSortButton.setBackgroundColor(0);
        typeButton.setBackgroundColor(0);

        database = RoomDB.getInstance(getContext());
        folders = database.mainDAO().getAllFolders();

        updateRecycler(folders);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FolderTakerActivity.class);
                startActivityForResult(intent, 1914);
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
                List<Folder> sortedFolders = new ArrayList<>();

                arrowButtonInstance = !arrowButtonInstance;
                int newImageResource = arrowButtonInstance
                        ? R.drawable.ic_arrow_upward
                        : R.drawable.ic_arrow_type;
                arrowSortButton.setImageResource(newImageResource);

                /*sortedNotes = arrowButtonInstance
                        ? (item_id == R.id.menu_sort_by_date) ?
                        database.mainDAO().getArchivedSortedByDateReverse():
                        database.mainDAO().getArchivedSortedByNameReverse()
                        : (item_id == R.id.menu_sort_by_date) ?
                        database.mainDAO().getArchivedSortedByDate() :
                        database.mainDAO().getArchivedSortedByName();*/

                folders.clear();
                folders.addAll(sortedFolders);
                folderAdapter.notifyDataSetChanged();
            }
        });

        /*sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortPopupMenu(view);
            }
        });*/

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

    private final FolderClickListener folderClickListener = new FolderClickListener() {
        @Override
        public void onClick(Folder folder) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new NotesFolderFragment(folder.getFolder_id(),
                            folder.getTagTitle()))
                    .addToBackStack(null)
                    .commit();
        }
        @Override
        public void onLongClick(CardView cardView, Folder folder) {
            selectedFolder = folder;

            showFolderPopup(cardView, selectedFolder);
        }
    };

    private void showFolderPopup(View view, Folder folder) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            item_id = itemId;

            if(itemId == R.id.delete) {
                deleteFolder(folder);
            }

            else if(itemId == R.id.redaction) {
                Intent intent = new Intent(getContext(), FolderTakerActivity.class);
                intent.putExtra("folder", folder);

                startActivityForResult(intent, 1918);

                return true;
            }

            return false;
        });

        popupMenu.inflate(R.menu.folder_popup);
        popupMenu.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1914) {
            if(resultCode == Activity.RESULT_OK) {
                Folder new_folder = (Folder) data.getSerializableExtra("folder");

                database.mainDAO().insertFolder(new_folder);
                folders.clear();
                folders.addAll(database.mainDAO().getAllFolders());
                folderAdapter.notifyDataSetChanged();
            }
        }

        else if(requestCode == 1918) {
            if(resultCode == Activity.RESULT_OK) {
                Folder new_folder = (Folder) data.getSerializableExtra("folder");

                database.mainDAO().updateFolder(new_folder.getFolder_id(), new_folder.getTagTitle(),
                        new_folder.getItemCount());
                folders.clear();
                folders.addAll(database.mainDAO().getAllFolders());
                folderAdapter.notifyDataSetChanged();
            }
        }
    }

    private void deleteFolder(Folder folder) {
        long newFolderId = 1;

        database.mainDAO().moveNotesToFolder(folder.getFolder_id(), newFolderId);
        database.mainDAO().deleteFolder(folder);

        folders.clear();
        folders.addAll(database.mainDAO().getAllFolders());
        folderAdapter.notifyDataSetChanged();
    }

    private void filter(String newText) {
        List<Folder> filteredList = new ArrayList<>();

        for(Folder folder : folders) {
            if(folder.getTagTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(folder);
            }
        }

        folderAdapter.filterList(filteredList);
        folderAdapter.notifyDataSetChanged();
    }

    private void updateRecycler(List<Folder> folders) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL));

        folderAdapter = new FolderAdapter(folders, folderClickListener,
                RoomDB.getInstance(getContext()));

        recyclerView.setAdapter(folderAdapter);
    }

    /*private void showSortPopupMenu(View view) {
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
    }*/

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