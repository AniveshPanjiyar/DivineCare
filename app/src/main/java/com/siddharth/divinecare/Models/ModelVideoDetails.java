package com.siddharth.divinecare.Models;

/**
 * Created by Ansh on 29/10/2017 12:26 AM.
 */

public class ModelVideoDetails {
    String videoName;
    String videoDesc;
    String url;
    String videoId;


public void setVideoName(String VideoName){
    this.videoName =VideoName;
}

public String getVideoName(){
    return videoName;
}

public void setVideoDesc(String VideoDesc){
    this.videoDesc =VideoDesc;
}

public String getVideoDesc(){
    return videoDesc;
}

public void setUrl(String url){
    this.url = url;
}

public String getUrl(){
    return url;
}

public void setVideoId(String VideoId){
    this.videoId =VideoId;
}
public String getVideoId(){
    return videoId;
}

}
