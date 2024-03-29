package ww.smartexpress.driver.ui.history.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.TripHistory;
import ww.smartexpress.driver.databinding.ItemTripHistoryBinding;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.TripHistoryViewHolder>{
    private List<Booking> bookings;
    private OnItemClickListener onItemClickListener;

    public TripHistoryAdapter() {
    }
    public void setBookings(List<Booking> bookings){
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripHistoryBinding binding = ItemTripHistoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new TripHistoryViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TripHistoryViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return bookings == null ? 0 : bookings.size();
    }

    public void addItems(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    public void clearItems() {
        bookings.clear();
    }

    public class TripHistoryViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        private ItemTripHistoryBinding mBinding;
        private Booking booking;
        private OnItemClickListener onItemClickListener;

        public TripHistoryViewHolder(@NonNull ItemTripHistoryBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.booking = bookings.get(position);
            mBinding.setVariable(BR.ivm,booking);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            this.onItemClickListener.itemClick(booking);
        }
    }

    public interface OnItemClickListener{
        void itemClick(Booking booking);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
