package com.cursoandroid.mioper;

public class CustomItem {

    private String sppinerItemName;
    private int spinnerItemImage;

    public CustomItem(String sppinerItemName, int spinnerItemImage){
        this.sppinerItemName = sppinerItemName;
        this.spinnerItemImage = spinnerItemImage;
    }

    public String getSppinerItemName(){
        return sppinerItemName;
    }

    public int getSpinnerItemImage(){
        return spinnerItemImage;
    }
}
