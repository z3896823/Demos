package org.zyb.litepaltest;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/2/13.
 */

public class Book extends DataSupport {

    int id;
    String title,author;
    double price;

    public Book(String title,String author, double price) {
        this.author = author;
        this.price = price;
        this.title = title;
    }

    public Book(){

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
