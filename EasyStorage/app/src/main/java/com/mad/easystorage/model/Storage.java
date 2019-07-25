package com.mad.easystorage.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Storage object contains a list with cargos
 */
public class Storage implements Serializable {
    private List<Cargo> mCargoList = new ArrayList<Cargo>();

    /**
     * Mandatory no parameter constructor
     * essential for Firebase database
     */
    public Storage() {
    }

    /**
     * getter method for mCargoList
     *
     * @return
     */
    public List<Cargo> getCargoList() {
        return mCargoList;
    }

    /**
     * A method to check if the storage has no cargo in it
     *
     * @return
     */
    public boolean isEmpty() {
        return mCargoList.size() == 0;
    }

    /**
     * setter method for mCargoList
     *
     * @param cargoList
     */
    public void setCargoList(List<Cargo> cargoList) {
        this.mCargoList = cargoList;
    }

    /**
     * Add a new cargo to the list
     *
     * @param cargo
     */
    public void add(Cargo cargo) {
        mCargoList.add(cargo);
    }

    /**
     * remove the target cargo from mCargoList
     *
     * @param target parse in target cargo
     * @return true if successful
     */
    public boolean remove(Cargo target) {
        for (Cargo cargo : mCargoList) {
            if (cargo.getName().equals(target.getName())) {
                return mCargoList.remove(cargo);
            }
        }
        return false;
    }

    /**
     * get how many type of cargo in this storage
     *
     * @return the size of mCargoList
     */
    public int getCargoTypes() {
        return getCargoList().size();
    }

    /**
     * get total amount of all cargos
     *
     * @return
     */
    public int getAmountAll() {
        int amountAll = 0;
        for (Cargo cargo : getCargoList()) {
            amountAll += cargo.getAmount();
        }
        return amountAll;
    }

    /**
     * get total value of all cargos
     *
     * @return
     */
    public double getTotalValue() {
        double totalValue = 0;
        for (Cargo cargo : getCargoList()) {
            totalValue += cargo.getTotalValue();
        }
        return totalValue;
    }

    /**
     * get average price of each cargo in the storage
     *
     * @return
     */
    public double getAveragePrice() {
        return getTotalValue() / getAmountAll();
    }

    /**
     * toString method to print out the storage
     *
     * @return
     */
    public String toString() {
        String result = "";
        for (Cargo cargo : mCargoList) {
            result = result + cargo.toString() + " ";
        }
        return result;
    }
}
