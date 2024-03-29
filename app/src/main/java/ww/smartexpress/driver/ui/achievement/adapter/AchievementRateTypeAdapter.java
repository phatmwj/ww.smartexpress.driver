package ww.smartexpress.driver.ui.achievement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.data.model.api.response.RateType;
import ww.smartexpress.driver.data.model.api.response.TripHistory;
import ww.smartexpress.driver.databinding.ItemRatingBinding;

public class AchievementRateTypeAdapter extends RecyclerView.Adapter<AchievementRateTypeAdapter.AchievementRateTypeViewHolder>{
    private List<RateType> rateTypes;
    private OnItemClickListener onItemClickListener;

    public AchievementRateTypeAdapter(List<RateType> rateTypes) {
        this.rateTypes = rateTypes;
    }

    @NonNull
    @Override
    public AchievementRateTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRatingBinding binding = ItemRatingBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AchievementRateTypeViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementRateTypeViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return rateTypes == null ? 0 : rateTypes.size();
    }

    public void addItems(List<RateType> rateTypes) {
        this.rateTypes = rateTypes;
        notifyDataSetChanged();
    }

    public void clearItems() {
        rateTypes.clear();
    }

    public class AchievementRateTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ItemRatingBinding mBinding;
        private RateType rateType;
        private OnItemClickListener onItemClickListener;

        public AchievementRateTypeViewHolder(@NonNull ItemRatingBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.rateType = rateTypes.get(position);
            mBinding.setVariable(BR.ivm, rateType);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            this.onItemClickListener.itemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void itemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
