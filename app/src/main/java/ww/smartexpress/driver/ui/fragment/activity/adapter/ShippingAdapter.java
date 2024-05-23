package ww.smartexpress.driver.ui.fragment.activity.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.data.model.api.response.Size;
import ww.smartexpress.driver.databinding.ItemShippingBinding;

public class ShippingAdapter extends RecyclerView.Adapter<ShippingAdapter.ShippingViewHolder> {

    @Getter
    @Setter
    private List<CurrentBooking> bookingList = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    @Getter
    @Setter
    private int positionSelected;

    public ShippingAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ShippingAdapter.ShippingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShippingBinding binding = ItemShippingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ShippingViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingAdapter.ShippingViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return bookingList !=null?bookingList.toArray().length : 0;
    }

    public void updateItem(int position, CurrentBooking currentBooking) {
        bookingList.set(position, currentBooking);
        notifyItemChanged(position);
    }

    public void addItem(CurrentBooking currentBooking) {
        bookingList.add(0,currentBooking);
        notifyItemInserted(0);
    }

    // Method to remove an item
    public void removeItem(int position) {
        if (position >= 0 && position < bookingList.size()) {
            bookingList.remove(position);
            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, bookingList.size());
        }
    }

    public class ShippingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemShippingBinding mBinding;
        private OnItemClickListener onItemClickListener;
        private int position;
        final int durationInSeconds = 30;
        final int updateInterval = 1000;
        private Runnable runnable;
        private Handler handler = new Handler();

        public ShippingViewHolder(@NonNull ItemShippingBinding mBinding, OnItemClickListener onItemClickListener) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            this.onItemClickListener = onItemClickListener;
            mBinding.getRoot().setOnClickListener(this);
            Long a = ((MVVMApplication) context.getApplicationContext()).getDeleteBookingId();
        }

        void onBind(int position){
            this.position = position;
            mBinding.setBooking(bookingList.get(position));
            if(bookingList.get(position)!= null && bookingList.get(position).getService()!=null && bookingList.get(position).getService().getSize()!= null){
                mBinding.setSize(getSize(bookingList.get(position).getService().getSize()));
            }

            mBinding.sendMessage.setOnClickListener(view -> {
                this.onItemClickListener.navigate_chat(position);
            });

            mBinding.callDriver.setOnClickListener(view -> {
                this.onItemClickListener.navigate_call(position);
            });

            if(bookingList.get(position).getState()!= null &&bookingList.get(position).getState() == Constants.BOOKING_STATE_BOOKING){
                mBinding.btnAccept.setOnClickListener(view -> {
                    Log.d("TAG", "onBind: ");
                    handler.removeCallbacks(runnable);
                    this.onItemClickListener.accept_booking(position);
                });
            } else if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_DRIVER_ACCEPT){
                mBinding.btnAccept.setOnClickListener(view -> {
                    this.onItemClickListener.pickup_booking(position);

                });
            }

            if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_PICKUP_SUCCESS){
                mBinding.btnConfirm.setOnClickListener(view -> {
                    this.onItemClickListener.done_booking(position);
                });
            } else {
                mBinding.btnConfirm.setOnClickListener(view -> {
                    this.onItemClickListener.delete_booking(position);
                });
            }

            mBinding.btnCancel.setOnClickListener(view -> {
                if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_BOOKING){
                    handler.removeCallbacks(runnable);
                }
                this.onItemClickListener.reject_booking(position);
            });

            if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_BOOKING){
                Log.d("TAG", "onBind: sao m chạy");
                startCountdown(position);
            }else {
                handler.removeCallbacks(runnable);
            }

            mBinding.executePendingBindings();
        }

        private void startCountdown(int position) {
            runnable = new Runnable() {
                private int remainingTime = ((MVVMApplication) context.getApplicationContext()).getCountDownTime().get(bookingList.get(position).getId());
                @Override
                public void run() {
                    if (remainingTime >= 0) {
                        mBinding.progressText.setText(String.valueOf(remainingTime));
                        mBinding.progressBar.setProgress((durationInSeconds - remainingTime) * 100 / durationInSeconds);
                        remainingTime--;
                        ((MVVMApplication) context.getApplicationContext()).getCountDownTime().put(bookingList.get(position).getId(), remainingTime);
                        handler.postDelayed(runnable, updateInterval);
                    } else {
                        onItemClickListener.countdown_end(position);
                        mBinding.progressText.setText(String.valueOf(durationInSeconds));
                        mBinding.progressBar.setProgress(0);
                    }
                }
            };
            handler.postDelayed(runnable, updateInterval);
        }

        @Override
        public void onClick(View view) {
            if(bookingList.get(position).getState() == Constants.BOOKING_STATE_BOOKING){
                handler.removeCallbacks(runnable);
            }
            this.onItemClickListener.itemClick(position);

        }
    }

    public interface OnItemClickListener{
        void itemClick(int position);
        void accept_booking(int position);
        void pickup_booking(int position);
        void reject_booking(int position);
        void done_booking(int position);
        void navigate_chat(int position);
        void navigate_call(int position);
        void delete_booking(int position);
        void countdown_end(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public String getSize(String sizeJson){
        Size size = ApiModelUtils.fromJson( sizeJson,Size.class);
        return size.formatSize();
    }
}
