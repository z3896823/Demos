package org.zyb.recyclerviewtest;

/**
 * Created by Administrator on 2017/2/12.
 */

public class Fruit {
    int imageId;
    String fruitName;

    public Fruit(String name,int imageId){
        this.fruitName = name;
        this.imageId = imageId;
    }

    public void setImageId(int imageId){
        this.imageId = imageId;
    }

    public void setFruitName(String fruitName){
        this.fruitName = fruitName;
    }

    public int getImageId(){
        return imageId;
    }

    public String getFruitName(){
        return fruitName;
    }
}
