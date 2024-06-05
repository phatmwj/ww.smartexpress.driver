package ww.smartexpress.driver.ui.bank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ww.smartexpress.driver.BuildConfig;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.BankResponse;
import ww.smartexpress.driver.databinding.ItemBankBinding;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private List<BankResponse> bankResponseList;

    private OnItemClickListener onItemClickListener;
    private Context context;

    public BankAdapter(Context context){
        this.context = context;
    }
    public void setBankResponseList(List<BankResponse> bankResponseList){
        this.bankResponseList = bankResponseList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBankBinding binding = ItemBankBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BankViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return bankResponseList != null ? bankResponseList.toArray().length : 0;
    }

    public class BankViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ItemBankBinding binding;
        private OnItemClickListener onItemClickListener;

        private BankResponse bankResponse;
        public BankViewHolder(@NonNull ItemBankBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onItemClickListener = onItemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBind(int position){
            this.bankResponse = bankResponseList.get(position);
            binding.setIvm(bankResponse);
            Glide.with(context)
                    .load(bankResponse.getLogo())
                    .into(binding.bankLogo);
           binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.itemClick(bankResponse);
        }
    }

    public interface OnItemClickListener{
        void itemClick(BankResponse bankResponse);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}
