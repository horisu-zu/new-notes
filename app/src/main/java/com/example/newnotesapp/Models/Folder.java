package com.example.newnotesapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity (tableName = "folder")
public class Folder implements Serializable {

    @PrimaryKey(autoGenerate = true)
    long folder_id = 0;

    @ColumnInfo (name = "folder_title")
    String tagTitle = "";
    @ColumnInfo (name = "count")
    int itemCount = 0;

    public long getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(long folder_id) {
        this.folder_id = folder_id;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }
}
