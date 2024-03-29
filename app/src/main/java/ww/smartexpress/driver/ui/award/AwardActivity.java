package ww.smartexpress.driver.ui.award;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.Award;
import ww.smartexpress.driver.databinding.ActivityAwardBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.award.adapter.AwardAdapter;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class AwardActivity extends BaseActivity<ActivityAwardBinding, AwardViewModel> {

    AwardAdapter awardAdapter;
    
    @Override
    public int getLayoutId() {
        return R.layout.activity_award;
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
        loadAwards();
        loadSpinner();
    }

    public void loadAwards(){
        List<Award> awards = new ArrayList<>();
        awards.add(new Award("1"));
        awards.add(new Award("2"));
        awards.add(new Award("1"));
        awards.add(new Award("2"));
        awards.add(new Award("1"));
        awards.add(new Award("2"));
        awards.add(new Award("1"));
        awards.add(new Award("2"));
        awards.add(new Award("1"));
        awards.add(new Award("2"));
        awards.add(new Award("1"));
        awards.add(new Award("2"));


        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);
        viewBinding.rcAward.setLayoutManager(layout);
        viewBinding.rcAward.setItemAnimator(new DefaultItemAnimator());
        awardAdapter = new AwardAdapter(awards);
        viewBinding.rcAward.setAdapter(awardAdapter);
        awardAdapter.setOnItemClickListener(award -> {

        });
    }

    private void loadSpinner(){
        String[] list = {"Tất cả các tháng","Tháng 12", "Tháng 11", "Tháng 10", "Tháng 9", "Tháng 8",
                        "Tháng 7","Tháng 6", "Tháng 5","Tháng 4", "Tháng 3", "Tháng 2","Tháng 1"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.item_spinner, R.id.text, list);
        viewBinding.spinner.setAdapter(adapter);
        viewBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
