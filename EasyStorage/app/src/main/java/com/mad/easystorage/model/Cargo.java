package com.mad.easystorage.model;

import java.io.Serializable;

/**
 * Cargo object contains 5 attributes
 */
public class Cargo implements Serializable {
    private String mName;
    private double mPrice;
    private int mAmount;
    private String mDescription;
    private String mBarcode;

    /**
     * Mandatory no parameter constructor
     * essential for Firebase database
     */
    public Cargo() {
    }

    /**
     * Cargo constructor with the following 4 perameters
     *
     * @param Name
     * @param Price
     * @param Amount
     * @param Description
     */
    public Cargo(String Name, double Price, int Amount, String Description) {
        this.mName = Name;
        this.mAmount = Amount;
        this.mDescription = Description;
        this.mPrice = Price;
    }

    /**
     * Cargo constructor with the folllowing 5 perameters
     *
     * @param Name
     * @param Price
     * @param Amount
     * @param Description
     * @param Barcode
     */
    public Cargo(String Name, double Price, int Amount, String Description, String Barcode) {
        this.mName = Name;
        this.mAmount = Amount;
        this.mDescription = Description;
        this.mPrice = Price;
        this.mBarcode = Barcode;
    }

    /**
     * getter method for mName
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * setter method for Name
     *
     * @param mName
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * getter method for mAmount
     *
     * @return
     */
    public int getAmount() {
        return mAmount;
    }

    /**
     * setter method for mAmount
     *
     * @param mAmount
     */
    public void setAmount(int mAmount) {
        this.mAmount = mAmount;
    }

    /**
     * getter method for mDescription
     *
     * @return
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * setter method for mDescription
     *
     * @param mDescription
     */
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    /**
     * getter method for mPrice
     *
     * @return
     */
    public double getPrice() {
        return mPrice;
    }

    /**
     * setter method for mPrice
     *
     * @param mPrice
     */
    public void setPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    /**
     * getter method for mBarcode
     *
     * @return
     */
    public String getBarcode() {
        return mBarcode;
    }

    /**
     * setter method for mBarcode
     *
     * @param mBarcode
     */
    public void setBarcode(String mBarcode) {
        this.mBarcode = mBarcode;
    }

    /**
     * toString method to print out the cargo.
     *
     * @return
     */
    public String toString() {
        return mName + mPrice + mAmount + mDescription + mBarcode;
    }

    /**
     * method for cargo stock in
     *
     * @param amount
     */
    public void warehousing(int amount) {
        this.mAmount += amount;
    }

    /**
     * method for cargo stock out
     *
     * @param amount
     * @return true if successful
     */
    public boolean delivery(int amount) {
        if ((this.mAmount - amount) >= 0) {
            this.mAmount -= amount;
            return true;
        }
        return false;
    }

    /**
     * method for get the total value of one cargo
     *
     * @return
     */
    public double getTotalValue() {
        return this.getPrice() * this.getAmount();
    }


}
