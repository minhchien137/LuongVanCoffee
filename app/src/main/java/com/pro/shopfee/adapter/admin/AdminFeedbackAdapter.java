package com.pro.shopfee.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.model.Feedback;

import java.util.List;

public class AdminFeedbackAdapter extends RecyclerView.Adapter<AdminFeedbackAdapter.AdminFeedbackViewHolder> {

    private final List<Feedback> mListFeedback;

    public AdminFeedbackAdapter(List<Feedback> list) {
        this.mListFeedback = list;
    }

    @NonNull
    @Override
    public AdminFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_feedback, parent, false);
        return new AdminFeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFeedbackViewHolder holder, int position) {
        Feedback feedback = mListFeedback.get(position);
        if (feedback == null) return;
        holder.tvEmail.setText(feedback.getEmail());
        holder.tvFeedback.setText(feedback.getComment());
    }

    @Override
    public int getItemCount() {
        if (mListFeedback != null) {
            return mListFeedback.size();
        }
        return 0;
    }

    public static class AdminFeedbackViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEmail;
        private final TextView tvFeedback;

        public AdminFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvFeedback = itemView.findViewById(R.id.tv_feedback);
        }
    }
}
