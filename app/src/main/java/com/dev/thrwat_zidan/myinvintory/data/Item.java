package com.dev.thrwat_zidan.myinvintory.data;

public class Item {

    private int mItem;
    private int mItemQuantity;

    public Item() {

    }

    public Item(int mItem, int mItemQuantity) {
        this.mItem = mItem;
        this.mItemQuantity = mItemQuantity;
    }

    public int getmItem() {
        return mItem;
    }

    public void setmItem(int mItem) {
        this.mItem = mItem;
    }

    public int getmItemQuantity() {
        return mItemQuantity;
    }

    public void setmItemQuantity(int mItemQuantity) {
        this.mItemQuantity = mItemQuantity;
    }

}
