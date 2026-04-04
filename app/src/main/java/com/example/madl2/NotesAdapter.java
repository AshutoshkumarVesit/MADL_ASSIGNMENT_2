package com.example.madl2;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private final List<Note> notes = new ArrayList<>();

    public void submitList(List<Note> noteList) {
        notes.clear();
        notes.addAll(noteList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvTitle.setText(note.getTitle());
        holder.tvDescription.setText(note.getDescription());
        holder.tvPriority.setText("Priority: " + note.getPriority());
        holder.tvDate.setText(note.getDate());

        String imagePath = note.getImagePath();
        if (!TextUtils.isEmpty(imagePath)) {
            try {
                holder.ivNoteImage.setVisibility(View.VISIBLE);
                holder.ivNoteImage.setImageURI(Uri.parse(imagePath));
            } catch (Exception exception) {
                holder.ivNoteImage.setVisibility(View.GONE);
                holder.ivNoteImage.setImageDrawable(null);
            }
        } else {
            holder.ivNoteImage.setVisibility(View.GONE);
            holder.ivNoteImage.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvPriority;
        private final TextView tvDate;
        private final ImageView ivNoteImage;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivNoteImage = itemView.findViewById(R.id.ivNoteImage);
        }
    }
}
