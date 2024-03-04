package com.example.newnotesapp.Models;

import java.io.Serializable;

public class VersionNote implements Serializable {
    private String versionTitle;
    private String versionDate;
    private String versionNote;
    private boolean isNoteShown = false;

    public VersionNote() {}

    public VersionNote(String versionTitle, String versionDate, String versionNote) {
        this.versionTitle = versionTitle;
        this.versionDate = versionDate;
        this.versionNote = versionNote;
    }

    public String getVersionTitle() {
        return versionTitle;
    }

    public boolean isNoteShown() {
        return isNoteShown;
    }

    public void setNoteShown(boolean noteShown) {
        isNoteShown = noteShown;
    }
    public void setVersionTitle(String versionTitle) {
        this.versionTitle = versionTitle;
    }

    public String getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(String versionDate) {
        this.versionDate = versionDate;
    }

    public String getVersionNote() {
        return versionNote;
    }

    public void setVersionNote(String versionNote) {
        this.versionNote = versionNote;
    }
}
