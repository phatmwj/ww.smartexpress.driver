package ww.smartexpress.driver.ui.wallet.transaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.EmptyViewHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.WalletTransaction;
import ww.smartexpress.driver.databinding.ActivityTransactionBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.wallet.transaction.item.ProgressItem;
import ww.smartexpress.driver.ui.wallet.transaction.item.TransactionItem;

public class TransactionActivity extends BaseActivity<ActivityTransactionBinding, TransactionViewModel>
        implements FlexibleAdapter.OnItemClickListener, FlexibleAdapter.EndlessScrollListener, EmptyViewHelper.OnEmptyViewListener{

    FlexibleAdapter mFlexibleAdapter;

    List<TransactionItem> transactionItemList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        viewModel.transactions.observe(this,walletTransactions -> {
            transactionItemList = new ArrayList<>();
            for (WalletTransaction walletTransaction: walletTransactions) {
                transactionItemList.add(new TransactionItem(walletTransaction));
            }
            if(viewModel.pageNumber.get() == 0){

                mFlexibleAdapter = new FlexibleAdapter(transactionItemList, this);


                viewBinding.rcTransaction.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                viewBinding.rcTransaction.setAdapter(mFlexibleAdapter);

                if(viewModel.totalElement.get()> viewModel.pageSize.get()){
                    mFlexibleAdapter
                            .setLoadingMoreAtStartUp(false)
                            .setEndlessScrollListener(this, new ProgressItem())
                            .setEndlessScrollThreshold(1)
                            .setEndlessTargetCount(viewModel.totalElement.get())
                            .setEndlessPageSize(viewModel.pageSize.get());
                }
                EmptyViewHelper.create(mFlexibleAdapter, findViewById(R.id.empty_view), null, this);

            }else {
                mFlexibleAdapter.onLoadMoreComplete(transactionItemList);
            }
        });

    }

    @Override
    public boolean onItemClick(View view, int position) {
        return false;
    }

    @Override
    public void noMoreLoad(int newItemsSize) {
    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {

        new Handler().postDelayed(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {

                viewModel.pageNumber.set(viewModel.pageNumber.get()+1);
                viewModel.getTransaction();
            }
        }, 3000);


    }

    @Override
    public void onUpdateEmptyDataView(int size) {
        Log.d("TAG", "onUpdateEmptyDataView: "+mFlexibleAdapter.getItemCount());
        if(size>0){
            viewBinding.layoutEmpty.emptyView.setVisibility(View.GONE);
        }else {
            viewBinding.layoutEmpty.emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateEmptyFilterView(int size) {

    }
}
