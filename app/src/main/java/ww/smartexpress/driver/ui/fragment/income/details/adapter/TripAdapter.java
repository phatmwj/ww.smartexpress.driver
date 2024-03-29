package ww.smartexpress.driver.ui.fragment.income.details.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.Trip;
import ww.smartexpress.driver.databinding.ItemTripBinding;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder>{
    private List<Booking> bookings;
    private OnItemClickListener onItemClickListener;

    public TripAdapter() {
    }
    public void setBookings(List<Booking> bookings){
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripBinding binding = ItemTripBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new TripViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
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

    public class TripViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        private ItemTripBinding mBinding;
        private Booking booking;
        private OnItemClickListener onItemClickListener;

        public TripViewHolder(@NonNull ItemTripBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.booking = bookings.get(position);
            mBinding.setVariable(BR.ivm, booking);
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
