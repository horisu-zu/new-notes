package com.example.newnotesapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes", foreignKeys =
    @ForeignKey(entity = Folder.class,
        parentColumns = "folder_id",
        childColumns = "folder_id",
        onDelete = ForeignKey.RESTRICT))
public class Notes implements Serializable {

    @PrimaryKey (autoGenerate = true)
    int ID = 0;

    @ColumnInfo (name = "title")
    String title = "";

    @ColumnInfo (name = "notes")
    String notes = "";

    @ColumnInfo (name = "date")
    String date = "";

    @ColumnInfo (name = "color")
    int color = 0;

    @ColumnInfo (name = "pin")
    boolean pin = false;

    @ColumnInfo (name = "isColorSet")
    boolean isColorSet = false;

    @ColumnInfo (name = "isArchived")
    boolean isArchived = false;

    @ColumnInfo (name = "tag")
    String tag = "";

    @ColumnInfo (name = "folder_id")
    int folder_id = 0;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isColorSet() {
        return isColorSet;
    }

    public void setColorSet(boolean colorSet) {
        isColorSet = colorSet;
    }

    public int getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }
}


