package ww.smartexpress.driver.ui.award.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.data.model.api.response.Award;
import ww.smartexpress.driver.databinding.ItemAwardBinding;

public class AwardAdapter extends RecyclerView.Adapter<AwardAdapter.AwardViewHolder>{
    private List<Award> awards;
    private OnItemClickListener onItemClickListener;

    public AwardAdapter(List<Award> awards) {
        this.awards = awards;
    }

    @NonNull
    @Override
    public AwardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAwardBinding binding = ItemAwardBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AwardViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AwardViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return awards == null ? 0 : awards.size();
    }

    public void addItems(List<Award> awards) {
        this.awards = awards;
        notifyDataSetChanged();
    }

    public void clearItems() {
        awards.clear();
    }

    public class AwardViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        private ItemAwardBinding mBinding;
        private Award award;
        private OnItemClickListener onItemClickListener;

        public AwardViewHolder(@NonNull ItemAwardBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.award = awards.get(position);
            mBinding.setVariable(BR.ivm, award);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            this.onItemClickListener.itemClick(award);
        }
    }

    public interface OnItemClickListener{
        void itemClick(Award award);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
