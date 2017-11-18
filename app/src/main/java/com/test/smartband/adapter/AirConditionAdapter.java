package com.test.smartband.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.model.AirConditionBrand;

import java.util.List;

/**
 * 空调品牌的适配器
 */

public class AirConditionAdapter extends ArrayAdapter<AirConditionBrand> {

    private int resourceId;

    public AirConditionAdapter(Context context, int resource, List<AirConditionBrand> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AirConditionBrand brand = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.brand_radioButton);
        TextView brandName = (TextView) view.findViewById(R.id.brand_textView);
        radioButton.setChecked(brand.isSelect());
        brandName.setText(brand.getName());
        return view;
    }
}
