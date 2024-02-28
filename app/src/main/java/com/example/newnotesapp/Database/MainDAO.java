package com.example.newnotesapp.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.newnotesapp.Models.Folder;
import com.example.newnotesapp.Models.Notes;

import java.util.List;

@Dao
public interface MainDAO {
    @Insert (onConflict = REPLACE)
    void insert(Notes notes);

    @Insert (onConflict = REPLACE)
    void insertFolder(Folder folder);

    @Query("SELECT * FROM folder WHERE folder_id=1")
    Folder getFolder();

    @Query("SELECT * FROM notes WHERE isArchived=0 ORDER BY ID DESC")
    List<Notes> getAll();
    @Query("SELECT * FROM notes WHERE isArchived=0 AND folder_id = :folder_id ORDER BY ID DESC")
    List<Notes> getAllByFolderId(long folder_id);

    @Query("SELECT * FROM notes WHERE isArchived=1 ORDER BY ID DESC")
    List<Notes> getAllArchived();

    @Query("SELECT * FROM folder WHERE folder_id != 1 ORDER BY folder_id DESC")
    List<Folder> getAllFolders();

    @Query("UPDATE notes SET title = :title, notes = :notes, tag = :tag WHERE ID = :id")
    void update(int id, String title, String tag, String notes);

    @Query("UPDATE folder SET folder_title = :tagTitle, count = :itemsCount WHERE folder_id = :id")
    void updateFolder(long id, String tagTitle, int itemsCount);
    @Query("UPDATE notes SET folder_id = :newFolderId WHERE folder_id = :oldFolderId")
    void moveNotesToFolder(long oldFolderId, long newFolderId);

    @Delete
    void delete(Notes notes);

    @Delete
    void deleteFolder(Folder folder);

    @Query("UPDATE notes SET isArchived = :isArchived WHERE ID = :id")
    void archive(int id, boolean isArchived);

    @Query("UPDATE notes SET folder_id =:folder_id WHERE ID = :id")
    void addToFolder(int id, long folder_id);

    @Query("SELECT COUNT(*) FROM notes WHERE folder_id=:folder_id AND isArchived = 0")
    int countNotesInFolder(long folder_id);

    @Query("UPDATE notes SET pin = :pin WHERE ID = :id")
    void pin(int id, boolean pin);

    @Query("SELECT * FROM notes WHERE isArchived=0 ORDER BY ID DESC")
    List<Notes> getAllSortedByDate();

    @Query("SELECT * FROM notes WHERE isArchived=0 ORDER BY title ASC")
    List<Notes> getAllSortedByName();

    //Reverse
    @Query("SELECT * FROM notes WHERE isArchived=0 ORDER BY ID ASC")
    List<Notes> getAllSortedByDateReverse();

    @Query("SELECT * FROM notes WHERE isArchived=0 ORDER BY title DESC")
    List<Notes> getAllSortedByNameReverse();

    //Archived
    @Query("SELECT * FROM notes WHERE isArchived=1 ORDER BY ID DESC")
    List<Notes> getArchivedSortedByDate();

    @Query("SELECT * FROM notes WHERE isArchived=1 ORDER BY title ASC")
    List<Notes> getArchivedSortedByName();

    @Query("SELECT * FROM notes WHERE isArchived=1 ORDER BY ID ASC")
    List<Notes> getArchivedSortedByDateReverse();

    @Query("SELECT * FROM notes WHERE isArchived=1 ORDER BY title DESC")
    List<Notes> getArchivedSortedByNameReverse();

    //Folder
    @Query("SELECT * FROM notes WHERE isArchived=0 AND folder_id = :folder_id AND folder_id != 1" +
            " ORDER BY ID DESC")
    List<Notes> getAllSortedByDateInFolder(long folder_id);

    @Query("SELECT * FROM notes WHERE isArchived=0 AND folder_id = :folder_id AND folder_id != 1" +
            " ORDER BY title ASC")
    List<Notes> getAllSortedByNameInFolder(long folder_id);

    //Reverse
    @Query("SELECT * FROM notes WHERE isArchived=0 AND folder_id = :folder_id AND folder_id != 1" +
            " ORDER BY ID ASC")
    List<Notes> getAllSortedByDateReverseInFolder(long folder_id);

    @Query("SELECT * FROM notes WHERE isArchived=0 AND folder_id = :folder_id AND folder_id != 1" +
            " ORDER BY title DESC")
    List<Notes> getAllSortedByNameReverseInFolder(long folder_id);
}

