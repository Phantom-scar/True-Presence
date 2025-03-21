package com.project.truepresence.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.truepresence.R;
import com.project.truepresence.models.FeatureItem;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {

    private final Context context;
    private final List<FeatureItem> featureList;

    public FeatureAdapter(Context context, List<FeatureItem> featureList) {
        this.context = context;
        this.featureList = featureList;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        FeatureItem feature = featureList.get(position);
        holder.icon.setImageResource(feature.getIconResId());
        holder.title.setText(feature.getTitleResId());

        // ✅ Neon Pulse Animation on Click
        holder.itemView.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.neon_pulse));
            Intent intent = new Intent(context, feature.getActivityClass());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;

        public FeatureViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.featureIcon);
            title = view.findViewById(R.id.featureTitle);
        }
    }
}
