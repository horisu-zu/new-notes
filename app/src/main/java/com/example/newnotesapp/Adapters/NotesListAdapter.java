package com.example.newnotesapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newnotesapp.Models.Notes;
import com.example.newnotesapp.NotesClickListener;
import com.example.newnotesapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder>{

    Context context;
    List<Notes> notesList;
    List<Notes> originalNotesList;
    NotesClickListener onClickListener;

    public NotesListAdapter(Context context, List<Notes> notesList,
                            NotesClickListener onClickListener) {
        this.context = context;
        this.notesList = notesList;
        this.originalNotesList = new ArrayList<>(notesList);
        this.onClickListener = onClickListener;
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);

        Random random = new Random();

        int randomColor = random.nextInt(colorCode.size());

        return colorCode.get(randomColor);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list,
                parent, false);

        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.titleView.setText(notesList.get(position).getTitle());
        holder.notesView.setText(notesList.get(position).getNotes());
        holder.dateView.setText(notesList.get(position).getDate());
        holder.tagView.setText(notesList.get(position).getTag());

        if(notesList.get(position).isPin()) {
            holder.pin_tag.setImageResource(R.drawable.png_pin);
        }
        else {
            holder.pin_tag.setImageResource(0);
        }

        int colorCode = getRandomColor();
        holder.notes_container.setCardBackgroundColor(holder.itemView.getResources()
                .getColor(colorCode));

        holder.notes_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(notesList.get(holder.getAdapterPosition()));
            }
        });

        holder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onClickListener.onLongClick(notesList.get(holder.getAdapterPosition()),
                        holder.notes_container);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Notes> filteredList) {
        notesList = filteredList;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {

    CardView notes_container;
    TextView titleView, notesView, dateView, tagView;
    ImageView pin_tag;
    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        notes_container = itemView.findViewById(R.id.notes_container);

        titleView = itemView.findViewById(R.id.titleView);
        notesView = itemView.findViewById(R.id.notesView);
        dateView = itemView.findViewById(R.id.dateView);
        tagView = itemView.findViewById(R.id.notesTagView);

        pin_tag = itemView.findViewById(R.id.pin_tag);
    }
}
