package com.ss.android.drag.demochannel;

public class ChannelEntity {

    private long id;
    private String name;
    private int spansize;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpansize(int size){
        this.spansize=size;
    }
    public int getSpansize(){
        return spansize;
    }
}
