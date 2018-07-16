package com.derickoduor.legrand.Model;

/**
 * Created by Derick Oduor on 4/8/2018.
 */

public class Product {
    private int id;
    private String name,description,tag,gender,picture;
    private double price;
    private int quantity;

    public Product(int id, String name, String description, String tag, String gender, String picture, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.gender = gender;
        this.picture = picture;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(int id, String name, String picture, double price) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
    }

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
