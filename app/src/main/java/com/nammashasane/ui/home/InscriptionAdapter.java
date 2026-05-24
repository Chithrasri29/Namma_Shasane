package com.nammashasane.ui.home;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.nammashasane.R;
import com.nammashasane.data.model.Inscription;

import java.util.ArrayList;
import java.util.List;

public class InscriptionAdapter extends RecyclerView.Adapter<InscriptionAdapter.InscriptionVH> {

    private List<Inscription> inscriptions = new ArrayList<>();
    private final OnInscriptionClickListener listener;

    public interface OnInscriptionClickListener {
        void onInscriptionClick(Inscription inscription);
    }

    public InscriptionAdapter(OnInscriptionClickListener listener) {
        this.listener = listener;
    }

    public void setInscriptions(List<Inscription> list) {
        this.inscriptions = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InscriptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inscription_card, parent, false);
        return new InscriptionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InscriptionVH holder, int position) {
        Inscription inscription = inscriptions.get(position);
        holder.bind(inscription, listener);
    }

    @Override
    public int getItemCount() {
        return inscriptions.size();
    }

    static class InscriptionVH extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvName, tvLocation, tvDate;

        InscriptionVH(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        void bind(Inscription inscription, OnInscriptionClickListener listener) {
            String name = inscription.getName();
            tvName.setText((name != null && !name.isEmpty()) ? name : "Unnamed Shasane");

            String location = inscription.getLocationName();
            tvLocation.setText("📍 " + ((location != null && !location.isEmpty()) ? location : "Unknown location"));

            if (inscription.getCreatedAt() != null) {
                long millis = inscription.getCreatedAt().toDate().getTime();
                CharSequence relTime = DateUtils.getRelativeTimeSpanString(millis);
                tvDate.setText(relTime);
            }

            if (inscription.getPhotoUrl() != null && !inscription.getPhotoUrl().isEmpty()) {
                Glide.with(ivThumbnail.getContext())
                        .load(inscription.getPhotoUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .placeholder(R.drawable.ic_photo_placeholder)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_photo_placeholder);
            }

            itemView.setOnClickListener(v -> listener.onInscriptionClick(inscription));
        }
    }
}
