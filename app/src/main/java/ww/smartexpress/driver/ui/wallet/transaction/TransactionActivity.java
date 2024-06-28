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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
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


//        viewModel.transactions.observe(this,walletTransactions -> {
//            transactionItemList = new ArrayList<>();
//            for (WalletTransaction walletTransaction: walletTransactions) {
//                transactionItemList.add(new TransactionItem(walletTransaction));
//            }
//            if(viewModel.pageNumber.get() == 0){
//
//                mFlexibleAdapter = new FlexibleAdapter(transactionItemList, this);
//
//
//                if(viewModel.totalElement.get()> viewModel.pageSize.get()){
//                    mFlexibleAdapter
//                            .setLoadingMoreAtStartUp(false)
//                            .setEndlessScrollListener(this, new ProgressItem())
//                            .setEndlessScrollThreshold(1)
//                            .setEndlessTargetCount(viewModel.totalElement.get())
//                            .setEndlessPageSize(viewModel.pageSize.get());
//                }
//                EmptyViewHelper.create(mFlexibleAdapter, findViewById(R.id.empty_view), null, this);
//
//                viewBinding.rcTransaction.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//                viewBinding.rcTransaction.setAdapter(mFlexibleAdapter);
//            }else {
//                mFlexibleAdapter.onLoadMoreComplete(transactionItemList);
//            }
//            viewModel.hideLoading();
//        });
//
//        viewModel.getTransaction();

        getMyTransaction();

    }

    private void getMyTransaction(){
        if (viewModel.pageNumber.get() == 0){
            viewModel.showLoading();
        }
        viewModel.compositeDisposable.add(viewModel.getMyTransaction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        viewModel.pageTotal.set(response.getData().getTotalPages());
                        viewModel.totalElement.set(Math.toIntExact(response.getData().getTotalElements()));
                        if (response.getData().getContent()!=null){
                            transactionItemList = new ArrayList<>();
                            for (WalletTransaction walletTransaction: response.getData().getContent()) {
                                transactionItemList.add(new TransactionItem(walletTransaction));
                            }
                            if(viewModel.pageNumber.get() == 0){

                                mFlexibleAdapter = new FlexibleAdapter(transactionItemList, this);


                                if(viewModel.totalElement.get()> viewModel.pageSize.get()){
                                    mFlexibleAdapter
                                            .setLoadingMoreAtStartUp(false)
                                            .setEndlessScrollListener(this, new ProgressItem())
                                            .setEndlessScrollThreshold(1)
                                            .setEndlessTargetCount(viewModel.totalElement.get())
                                            .setEndlessPageSize(viewModel.pageSize.get());
                                }
                                EmptyViewHelper.create(mFlexibleAdapter, findViewById(R.id.empty_view), null, this);

                                viewBinding.rcTransaction.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                                viewBinding.rcTransaction.setAdapter(mFlexibleAdapter);
                            }else {
                                mFlexibleAdapter.onLoadMoreComplete(transactionItemList);
                            }
                        }
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                    }
                    viewModel.hideLoading();
                },error->{
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    viewModel.hideLoading();
                })
        );
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
                getMyTransaction();
            }
        }, 1500);


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
