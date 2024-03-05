package com.example.newnotesapp.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newnotesapp.Models.VersionNote;
import com.example.newnotesapp.R;
import com.example.newnotesapp.VersionClickListener;

import java.util.List;

public class VersionAdapter extends RecyclerView.Adapter<VersionViewHolder> {
    VersionClickListener onVersionClick;
    List<VersionNote> versionNotes;
    Context context;

    public VersionAdapter(Context context, List<VersionNote> versionNotes,
                          VersionClickListener onVersionClick) {
        this.context = context;
        this.versionNotes = versionNotes;
        this.onVersionClick = onVersionClick;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    @NonNull
    @Override
    public VersionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VersionViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.version_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VersionViewHolder holder, int position) {
        holder.versionTitleView.setText(versionNotes.get(position).getVersionTitle());
        holder.versionDateView.setText(versionNotes.get(position).getVersionDate());
        holder.versionNoteView.setText(versionNotes.get(position).getVersionNote());
        holder.arrowType.setImageResource(R.drawable.ic_arrow_right);
        holder.arrowType.setBackgroundColor(0);

        holder.versionNoteView.setVisibility(View.GONE);

        /*holder.versionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(versionNotes.get(holder.getAdapterPosition()).isNoteShown()) {
                    holder.versionNoteView.setVisibility(View.GONE);
                    holder.arrowType.setImageResource(R.drawable.ic_arrow_right);
                }
                else {
                    holder.versionNoteView.setVisibility(View.VISIBLE);
                    holder.arrowType.setImageResource(R.drawable.ic_arrow_bot);
                }

                versionNotes.get(holder.getAdapterPosition())
                        .setNoteShown(versionNotes.get(holder.getAdapterPosition()).isNoteShown());
            }
        });*/

        holder.arrowType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                VersionNote versionNote = versionNotes.get(position);

                setVisibilityNote(holder, versionNote.isNoteShown());

                versionNote.setNoteShown(!versionNote.isNoteShown());
            }
        });

        holder.versionItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getPosition();
                onVersionClick.onLongClick(versionNotes.get(position), holder.versionItem,
                        position);
                return true;
            }
        });
    }

    private void setVisibilityNote(@NonNull VersionViewHolder holder, boolean isShown) {

        if (isShown) {
            holder.versionNoteView.setVisibility(View.GONE);
            holder.arrowType.setImageResource(R.drawable.ic_arrow_right);
        }
        else {
            holder.versionNoteView.setVisibility(View.VISIBLE);
            holder.arrowType.setImageResource(R.drawable.ic_arrow_bot);
        }
    }

    /*private void applyAnimation(View view, @AnimRes int animationResId) {
        Animation animation = AnimationUtils.loadAnimation(context, animationResId);
        view.startAnimation(animation);
    }*/

    @Override
    public int getItemCount() {
        return versionNotes.size();
    }
}

class VersionViewHolder extends RecyclerView.ViewHolder {
    CardView versionItem;
    ImageButton arrowType;
    TextView versionTitleView, versionNoteView, versionDateView;

    public VersionViewHolder(@NonNull View itemView) {
        super(itemView);

        versionItem = itemView.findViewById(R.id.versionCardView);

        versionTitleView = itemView.findViewById(R.id.versionTitle);
        versionDateView = itemView.findViewById(R.id.versionDate);
        versionNoteView = itemView.findViewById(R.id.versionNote);
        arrowType = itemView.findViewById(R.id.showVersionNoteButton);
    }
}
