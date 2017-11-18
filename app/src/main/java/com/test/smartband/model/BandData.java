package com.test.smartband.model;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 手环返回的数据实体
 */
public class BandData implements Serializable{

    private int id;
    private float body_temp;//体温
    private int battery;//电量
    private int step;//步数
    private float humidity;//湿度
    private float room_temp;//室温
    private int aircondition_temp;//空调温度
    private String pmv_level;//舒适度等级
    private float calorie;//卡路里
    private boolean isCharge;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getBodyTemp() {
        return body_temp;
    }

    public void setBodyTemp(float body_temp) {
        this.body_temp = body_temp;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getRoomTemp() {
        return room_temp;
    }

    public void setRoomTemp(float room_temp) {
        this.room_temp = room_temp;
    }

    public int getAirconditionTemp() {
        return aircondition_temp;
    }

    public void setAirconditionTemp(int aircondition_temp) {
        this.aircondition_temp = aircondition_temp;
    }

    public String getPmvLevel() {
        return pmv_level;
    }

    public void setPmvLevel(String pmv_level) {
        this.pmv_level = pmv_level;
    }

    public String getCalorie() {
        //保持两位小数
        return new DecimalFormat("##0.00").format(calorie);
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public boolean isCharge() {
        return isCharge;
    }

    public void setCharge(boolean charge) {
        isCharge = charge;
    }
}
