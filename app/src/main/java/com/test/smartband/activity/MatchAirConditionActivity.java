package com.test.smartband.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.adapter.AirConditionAdapter;
import com.test.smartband.model.AirConditionBrand;
import com.test.smartband.tools.CacheUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择空调品牌
 */
public class MatchAirConditionActivity extends AppCompatActivity {

    private RelativeLayout settingRelativeLayout;
    private String[] brandNames = new String[]{"美的", "格力", "海尔", "海信", "奥克斯", "大金", "松下"};
    private int currentSelectId = 0;
    private List<AirConditionBrand> mBrands = new ArrayList<AirConditionBrand>();
    private TextView brandTextView;
    private ImageView back;
    private AlertDialog mDialog;
    private AirConditionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_aircondition);
        back = (ImageView) findViewById(R.id.iv_back);
        brandTextView = (TextView) findViewById(R.id.tv_setting);
        settingRelativeLayout = (RelativeLayout) findViewById(R.id.re_setting);
        settingRelativeLayout.setOnClickListener(new MatchAirConListener());
        back.setOnClickListener(new MatchAirConListener());
        currentSelectId = CacheUtils.getInt(this, CacheUtils.AIR_CONDITION_BRAND, 0);
        brandTextView.setText(brandNames[currentSelectId ]);
    }


    public class MatchAirConListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_setting:
                    initBrands();
                    View view = LayoutInflater.from(MatchAirConditionActivity.this)
                            .inflate(R.layout.dialog_air_condition_brands, null);
                    mAdapter = new AirConditionAdapter(MatchAirConditionActivity.this,
                            R.layout.list_item_brand, mBrands);
                    mAdapter.notify();
                    ListView listView = (ListView) view.findViewById(R.id.list_view);
                    listView.setFooterDividersEnabled(false);
                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new ItemOnClick());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MatchAirConditionActivity.this);
                    builder.setView(view);
                    mDialog = builder.create();
                    mDialog.show();
                    break;
                case R.id.iv_back:
                    finish();
                    break;
            }
        }
    }

    class ItemOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CacheUtils.putInt(MatchAirConditionActivity.this, CacheUtils.AIR_CONDITION_BRAND, position);//保存空调品牌
            mBrands.get(currentSelectId).setSelect(false);
            AirConditionBrand brand = mBrands.get(position);
            brand.setSelect(true);//设置点击那个被选中
            brandTextView.setText(brand.getName());//显示在textview上面
            currentSelectId = position;
            mAdapter.notifyDataSetChanged();//更新列表数据显示
            mDialog.dismiss();
        }
    }

    private void initBrands() {
        mBrands.clear();
        for (int i = 0; i < brandNames.length; i++) {
            AirConditionBrand brand = new AirConditionBrand();
            brand.setName(brandNames[i]);
            if (currentSelectId == i) {
                brand.setSelect(true);
            } else {
                brand.setSelect(false);
            }
            mBrands.add(brand);
        }

    }


}
