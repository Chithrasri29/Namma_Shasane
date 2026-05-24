package com.nammashasane.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nammashasane.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingVH> {

    private final String[] titles;
    private final String[] descriptions;
    private final String[] emojis;

    public OnboardingAdapter(String[] titles, String[] descriptions, String[] emojis) {
        this.titles = titles;
        this.descriptions = descriptions;
        this.emojis = emojis;
    }

    @NonNull
    @Override
    public OnboardingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_page, parent, false);
        return new OnboardingVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingVH holder, int position) {
        holder.tvIllustration.setText(emojis[position]);
        holder.tvTitle.setText(titles[position]);
        holder.tvDesc.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class OnboardingVH extends RecyclerView.ViewHolder {
        TextView tvIllustration, tvTitle, tvDesc;

        OnboardingVH(@NonNull View itemView) {
            super(itemView);
            tvIllustration = itemView.findViewById(R.id.tv_illustration);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
        }
    }
}
