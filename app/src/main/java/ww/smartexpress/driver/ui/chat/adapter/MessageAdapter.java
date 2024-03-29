package ww.smartexpress.driver.ui.chat.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.data.model.api.response.MessageChat;
import ww.smartexpress.driver.databinding.ItemMessageBinding;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<MessageChat> messages;
    private OnItemClickListener onItemClickListener;
    public Long currentUserId;

    public MessageChat lassMessage = new MessageChat();

    public MessageAdapter() {
    }
    public void setMessages(List<MessageChat> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MessageViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    public void addItems(List<MessageChat> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void clearItems() {
        messages.clear();
    }
    public void addMessage(MessageChat messageChat){
        if(messages!= null && messages.size()!=0){
            messages.add(messageChat);
            notifyItemInserted(messages.size());
        }else {
            messages = new ArrayList<>();
            messages.add(messageChat);
            notifyDataSetChanged();
        }

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        private ItemMessageBinding mBinding;
        private MessageChat message;
        private OnItemClickListener onItemClickListener;

        public MessageViewHolder(@NonNull ItemMessageBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.message = messages.get(position);
            if(position - 1>=0){
                lassMessage = messages.get(position-1);
            }
            mBinding.setVariable(BR.ivm, message);
            mBinding.setUserId(currentUserId);
            mBinding.setLastMessage(lassMessage);
//            Log.d("TAG", "onBind: "+currentUserId);
//            Log.d("TAG", "onBind1: "+message.getSender());
//            Log.d("TAG", "onBind1: "+lassMessage.getSender());
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            this.onItemClickListener.itemClick(message);
        }
    }

    public interface OnItemClickListener{
        void itemClick(MessageChat message);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
