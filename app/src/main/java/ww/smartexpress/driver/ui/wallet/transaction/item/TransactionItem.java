package ww.smartexpress.driver.ui.wallet.transaction.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.WalletTransaction;
import ww.smartexpress.driver.utils.DateUtils;
import ww.smartexpress.driver.utils.NumberUtils;

public class TransactionItem extends AbstractFlexibleItem<TransactionItem.TransactionViewHolder> {

    private WalletTransaction walletTransaction;
    public TransactionItem(WalletTransaction walletTransaction) {
        this.walletTransaction = walletTransaction;
    }


    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
       return R.layout.item_transaction;
    }

    @Override
    public TransactionViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new TransactionViewHolder(view,adapter);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, TransactionViewHolder holder, int position, List<Object> payloads) {
//        holder.onBind();
        Context context = holder.itemView.getContext();
        holder.createDate.setText(DateUtils.dateFormat(walletTransaction.getCreatedDate()));
        switch (walletTransaction.getKind()){
            case 0:
                holder.money.setText("+"+NumberUtils.formatCurrency((double) walletTransaction.getMoney()));
                holder.money.setTextColor(context.getResources().getColor(R.color.green_light_app));
                holder.desc.setText("Nạp tiền vào ví");
                break;
            case 1:
                holder.money.setText("-"+NumberUtils.formatCurrency((double) walletTransaction.getMoney()));
                holder.money.setTextColor(context.getResources().getColor(R.color.red_color));
                holder.desc.setText("Rút tiền từ ví");
                break;
            case 2:
                break;
            case 3:
                holder.money.setText("+"+NumberUtils.formatCurrency((double) walletTransaction.getMoney()));
                holder.money.setTextColor(context.getResources().getColor(R.color.green_light_app));
                holder.desc.setText("Nhận tiền");
                break;
            case 4:
                holder.money.setText("-"+NumberUtils.formatCurrency((double) walletTransaction.getMoney()));
                holder.money.setTextColor(context.getResources().getColor(R.color.red_color));
                holder.desc.setText("Chi tiền");
                break;
            case 5:
                holder.money.setText("+"+NumberUtils.formatCurrency((double) walletTransaction.getMoney()));
                holder.money.setTextColor(context.getResources().getColor(R.color.green_light_app));
                holder.desc.setText("Nhận thêm tiền");
                break;
            default:
                break;
        }

        switch (walletTransaction.getState()) {
            case 0:
                holder.state.setText("Đang xử lí");
                holder.state.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case 1:
                holder.state.setText("Thành công");
                holder.state.setTextColor(context.getResources().getColor(R.color.green_light_app));
                break;
            case 2:
                holder.state.setText("Thất bại");
                holder.state.setTextColor(context.getResources().getColor(R.color.red_color));
                break;
            default:
                break;
        }
    }


    @Override
    public int getSpanSize(int spanCount, int position) {
        return super.getSpanSize(spanCount, position);
    }

    public class TransactionViewHolder extends FlexibleViewHolder{

        @BindView(R.id.createDate)
        TextView createDate;
        @BindView(R.id.desc)
        TextView desc;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.state)
        TextView state;
        public TransactionViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }

}
