package com.example.jan.restauto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jan on 3/12/2018.
 */

public class OrderObject implements Parcelable {
    //id of user
    int userID = 0;
    //floor represents the floor user is working on
    int floor = 0;
    //table represents the table the user is serving
    int table = 0;
    /*
    HashMap is used for storing menu items for the order
    key is the menu item name (string)
    value is the item quantity (int)
     */
    HashMap<String, Integer> orderMap;

    /*
    constructor for object, only needs floor and table
    hashmap is initialized as blank
     */
    public OrderObject(int userID, int floor, int table) {
        this.userID = userID;
        this.floor = floor;
        this.table = table;
        orderMap = new HashMap<>();
    }

    //setter function to add userId
    public void addUser(int id){
        this.userID= id;
    }

    //setter function to add menu item and quantity to order
    public void addOrder(String item, int num) {
        orderMap.put(item, num);
    }

    /*
    update function for updating values of
    already inserted menu items
     */
    public void updateOrder(String item, int num) {
        orderMap.put(item, num);
    }

    //remove function for removing a menu item
    public void removeOrder(String item) {
        orderMap.remove(item);
    }

    //for parceable
    @Override
    public int describeContents() {
        return 0;
    }

    //for parceable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userID);
        dest.writeInt(floor);
        dest.writeInt(table);
        dest.writeMap(orderMap);
    }

    //for parceable
    public static final Parcelable.Creator<OrderObject> CREATOR = new Parcelable.Creator<OrderObject>() {
        public OrderObject createFromParcel(Parcel pc) {
            return new OrderObject(pc);
        }
        public OrderObject[] newArray(int size) {
            return new OrderObject[size];
        }
    };

    //for parceable
    public OrderObject(Parcel in){
        userID     =  in.readInt();
        floor      =  in.readInt();
        table      =  in.readInt();
        orderMap   =  new HashMap<String, Integer>();
        in.readMap(orderMap, OrderObject.class.getClassLoader());
    }
}
