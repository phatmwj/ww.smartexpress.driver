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
import ww.smartexpress.driver.databinding.ItemShippingEmptyBinding;

public class ShippingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Getter
    @Setter
    private List<CurrentBooking> bookingList = new ArrayList<>();

    private static final int VT_SHIPPING = 1;
    private static final int VT_EMPTY = 0;
    private Context context;
    private OnItemClickListener onItemClickListener;

    @Getter
    @Setter
    private int positionSelected;

    @Getter
    @Setter
    private Map<Long, Integer> mapIdPos = new HashMap<>();

    private MVVMApplication application;

    public ShippingAdapter(Context context){
        this.context = context;
        application = (MVVMApplication) context.getApplicationContext();
    }

    public void addMapIdPos(){
        for (int i=0;i<bookingList.toArray().length;i++) {
            mapIdPos.put(bookingList.get(i).getId(), i );
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VT_SHIPPING){
            ItemShippingBinding binding = ItemShippingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new ShippingViewHolder(binding, onItemClickListener);
        }else {
            ItemShippingEmptyBinding binding = ItemShippingEmptyBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new EmptyViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ShippingViewHolder){
            ((ShippingViewHolder) holder).onBind(position);
        } else if(holder instanceof EmptyViewHolder){
            ((EmptyViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList !=null && bookingList.toArray().length !=0 ?bookingList.toArray().length : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return bookingList !=null && bookingList.toArray().length !=0 ? VT_SHIPPING : VT_EMPTY;
    }

    public void updateItem(Long bookingId, CurrentBooking currentBooking) {
        int pos = mapIdPos.get(bookingId);
        bookingList.set(pos, currentBooking);
        notifyItemChanged(pos);
    }

    public void addItem(CurrentBooking currentBooking) {
        bookingList.add(0,currentBooking);
        if(bookingList.toArray().length ==1){
            notifyDataSetChanged();
        }
        notifyItemInserted(0);
//        notifyItemRangeInserted(0, bookingList.toArray().length);
        addMapIdPos();
    }

    // Method to remove an item
    public void removeItem(Long bookingId) {
        int pos = mapIdPos.get(bookingId);
        bookingList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeRemoved(pos,bookingList.toArray().length);
        addMapIdPos();
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder{
        private ItemShippingEmptyBinding mBinding;
        public EmptyViewHolder(@NonNull ItemShippingEmptyBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void onBind(int position){
            mBinding.executePendingBindings();
        }

    }

    public class ShippingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemShippingBinding mBinding;
        private OnItemClickListener onItemClickListener;
        private int position;
        private Long bookingId;
        final int durationInSeconds = 30;
        final int updateInterval = 1000;
        private Runnable runnable;
        private Handler handler = new Handler();

        public ShippingViewHolder(@NonNull ItemShippingBinding mBinding, OnItemClickListener onItemClickListener) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            this.onItemClickListener = onItemClickListener;
            mBinding.getRoot().setOnClickListener(this);
        }

        void onBind(int position){
            this.position = position;
            bookingId = bookingList.get(position).getId();
            mBinding.setBooking(bookingList.get(position));
            if(bookingList.get(position)!= null && bookingList.get(position).getService()!=null && bookingList.get(position).getService().getSize()!= null){
                mBinding.setSize(getSize(bookingList.get(position).getService().getSize()));
            }

            mBinding.sendMessage.setOnClickListener(view -> {
                this.onItemClickListener.navigate_chat(position, bookingId);
            });

            mBinding.callDriver.setOnClickListener(view -> {
                this.onItemClickListener.navigate_call(position, bookingId);
            });

            if(bookingList.get(position).getState()!= null &&bookingList.get(position).getState() == Constants.BOOKING_STATE_BOOKING){
                mBinding.btnAccept.setOnClickListener(view -> {
                    Log.d("TAG", "onBind: ");
                    handler.removeCallbacks(runnable);
                    this.onItemClickListener.accept_booking(position, bookingId);
                });
            } else if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_DRIVER_ACCEPT){
                mBinding.btnAccept.setOnClickListener(view -> {
                    this.onItemClickListener.pickup_booking(position, bookingId);

                });
            }

            if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_PICKUP_SUCCESS){
                mBinding.btnConfirm.setOnClickListener(view -> {
                    this.onItemClickListener.done_booking(position, bookingId);
                });
            } else {
                mBinding.btnConfirm.setOnClickListener(view -> {
                    this.onItemClickListener.delete_booking(position, bookingId);
                });
            }

            mBinding.btnCancel.setOnClickListener(view -> {
                if(bookingList.get(position).getState()!= null && bookingList.get(position).getState() == Constants.BOOKING_STATE_BOOKING){
                    handler.removeCallbacks(runnable);
                }
                this.onItemClickListener.reject_booking(position, bookingId);
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
                private int remainingTime = application.getCountDownTime().get(bookingList.get(position).getId());
                @Override
                public void run() {
                    if (remainingTime >= 0) {
                        mBinding.progressText.setText(String.valueOf(remainingTime));
                        mBinding.progressBar.setProgress((durationInSeconds - remainingTime) * 100 / durationInSeconds);
                        remainingTime--;
                        application.getCountDownTime().put(bookingId, remainingTime);
                        handler.postDelayed(runnable, updateInterval);
                    } else {
                        onItemClickListener.countdown_end(position, bookingId);
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
            this.onItemClickListener.itemClick(position, bookingId);

        }
    }

    public interface OnItemClickListener{
        void itemClick(int position, Long bookingId);
        void accept_booking(int position, Long bookingId);
        void pickup_booking(int position, Long bookingId);
        void reject_booking(int position, Long bookingId);
        void done_booking(int position, Long bookingId);
        void navigate_chat(int position, Long bookingId);
        void navigate_call(int position, Long bookingId);
        void delete_booking(int position, Long bookingId);
        void countdown_end(int position, Long bookingId);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public String getSize(String sizeJson){
        Size size = ApiModelUtils.fromJson( sizeJson,Size.class);
        return size.formatSize();
    }
}
