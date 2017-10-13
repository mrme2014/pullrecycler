package com.mrs.mrs.swiprecyclerview.bean;

/**
 * Created by mrs on 2017/5/18.
 */

public class FileEntiy {
    public long progress;
    public String path;

    public FileEntiy(String pic) {
        this.path = pic;
        this.progress = 0;
    }
}
