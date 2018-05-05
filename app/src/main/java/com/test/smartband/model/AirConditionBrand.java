package com.test.smartband.model;

/**
 * 空调品牌的实体类
 */

public class AirConditionBrand {

    private boolean isSelect;
    private String name;

    public AirConditionBrand(){
    }

    public AirConditionBrand(boolean isSelect, String name) {
        this.isSelect = isSelect;
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
