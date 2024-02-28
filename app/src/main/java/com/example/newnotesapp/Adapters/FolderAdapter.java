package com.example.newnotesapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newnotesapp.Database.RoomDB;
import com.example.newnotesapp.FolderClickListener;
import com.example.newnotesapp.Models.Folder;
import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.R;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderViewHolder> {
    private List<Folder> folders;
    private FolderClickListener onFolderClickListener;
    private RoomDB roomDB;

    public FolderAdapter(List<Folder> folders, FolderClickListener onFolderClickListener,
                         RoomDB roomDB) {
        this.folders = folders;
        this.onFolderClickListener = onFolderClickListener;
        this.roomDB = roomDB;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item,
                parent, false);

        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.folderTitleView.setText(folders.get(position).getTagTitle());

        long folder_id = folders.get(position).getFolder_id();
        int numberOfNotes = countNotesInFolder(folder_id);

        holder.itemsCountFolderView.setText(String.valueOf(numberOfNotes));

        holder.folderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFolderClickListener.onClick(folders.get(holder.getAdapterPosition()));
            }
        });

        holder.folderItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onFolderClickListener.onLongClick(holder.folderItem,
                        folders.get(holder.getAdapterPosition()));
                return true;
            }
        });
    }

    private int countNotesInFolder(long folderId) {
        if (roomDB != null) {
            return roomDB.mainDAO().countNotesInFolder(folderId);
        } else {
            return 0;
        }
    }

    public void filterList(List<Folder> filteredList) {
        folders = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }
}

class FolderViewHolder extends RecyclerView.ViewHolder {
    CardView folderItem;
    TextView folderTitleView;
    TextView itemsCountFolderView, idView;
    public FolderViewHolder(@NonNull View itemView) {
        super(itemView);
        folderItem = itemView.findViewById(R.id.folderItem);

        folderTitleView = itemView.findViewById(R.id.folderNameTextView);
        itemsCountFolderView = itemView.findViewById(R.id.numItemsTextView);
    }
}
