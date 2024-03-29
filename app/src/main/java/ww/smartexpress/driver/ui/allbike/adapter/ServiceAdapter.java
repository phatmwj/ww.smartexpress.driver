package ww.smartexpress.driver.ui.allbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.data.model.api.response.DriverServiceResponse;
import ww.smartexpress.driver.data.model.api.response.ServiceResponse;
import ww.smartexpress.driver.databinding.ItemServiceBinding;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>{
    private List<DriverServiceResponse> services;
    private OnItemClickListener onItemClickListener;

    public ServiceAdapter(List<DriverServiceResponse> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceBinding binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ServiceViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.onBind(position);
        holder.mBinding.switchState.setOnCheckedChangeListener((compoundButton, b) -> {
            int newState;
            if(b){
                newState =1;
            }
            else {
                newState = 0;
            }
            if(onItemClickListener != null){
                onItemClickListener.changeStateService(services.get(position), newState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services == null ? 0 : services.size();
    }

    public void addItems(List<DriverServiceResponse> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void clearItems() {
        services.clear();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ItemServiceBinding mBinding;
        private DriverServiceResponse serviceResponse;
        private OnItemClickListener onItemClickListener;

        public ServiceViewHolder(@NonNull ItemServiceBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.serviceResponse = services.get(position);
            mBinding.setVariable(BR.ivm, serviceResponse);
            mBinding.setVariable(BR.i, services.get(services.size()-1).getId());
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            this.onItemClickListener.itemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void itemClick(int position);
        void changeStateService(DriverServiceResponse serviceResponse, int newState);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
