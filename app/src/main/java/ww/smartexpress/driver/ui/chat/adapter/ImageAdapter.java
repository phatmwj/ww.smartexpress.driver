package ww.smartexpress.driver.ui.chat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ww.smartexpress.driver.databinding.ItemImageBinding;
import ww.smartexpress.driver.utils.ScreenUtils;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Uri> uris;
    private OnItemClickListener onItemClickListener;

    @Getter
    private Integer imageSelected;

    @Getter
    private Integer imageUnSelected;
    private Context context;

    public ImageAdapter(List<Uri> uris, Context context){
        this.uris = uris;
        this.context = context;
    }

    public void setImageSelected(Integer i){
        if(imageSelected != null){
            notifyItemChanged(imageSelected);
        }
        this.imageSelected =i;
        if(i != null){
            notifyItemChanged(i);
        }
    }

    public void setImageUnSelected(Integer i){
        this.imageUnSelected =i;
        notifyItemChanged(i);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ImageViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth(context)/3;
        holder.itemView.setLayoutParams(layoutParams);
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return uris != null ? uris.size() : 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ItemImageBinding mBinding;
        private Uri uri;
        private int pos;
        private OnItemClickListener onItemClickListener;
        public ImageViewHolder(@NonNull ItemImageBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        void onBind(int position){
            pos = position;
            mBinding.setPos(position);
            mBinding.setPosSelected(imageSelected);
            uri = uris.get(position);
            Glide.with(context)
                    .load(uri)
                    .into(mBinding.imageView);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.itemClick(uri, pos);
        }
    }

    public interface OnItemClickListener{
        void itemClick(Uri uri, Integer pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
