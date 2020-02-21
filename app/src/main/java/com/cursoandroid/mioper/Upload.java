package com.cursoandroid.mioper;

public class Upload {

    private String mName;
    private String mImageUrl;

    public Upload(){

    }

    public Upload(String name, String imageUrl){

        if(name.trim().equals("")){
            name = "Nenhum nome";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getNome(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getmImageUrl(){
        return mImageUrl;
    }

    public void setmImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }


}
