package com.example.inventorymapper;

import android.util.Log;

import com.example.inventorymapper.ui.model.Household;
import com.example.inventorymapper.ui.model.Item;
import com.example.inventorymapper.ui.model.Location;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public final class Database {

    // Static DatabaseReference, shared across all usages
    private static final DatabaseReference mDatabase = MyApplication.getDatabaseReference();


    // Private constructor to prevent instantiation
    private Database() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static Household addHousehold(Household household) {
        DatabaseReference householdRef = mDatabase.child("households").push();
        household.setId(householdRef.getKey());
        householdRef.setValue(household);
        return household;
    }

    public static Location addLocation(Location location, String houseId) {
        DatabaseReference locationRef = mDatabase.child("households")
                .child(houseId)
                .child("location")
                .child("sublocations")
                .push();
        location.setId(locationRef.getKey());
        locationRef.setValue(location);

        return location;
    }

    public static DatabaseReference getHouseholds() {
        return mDatabase.child("households");
    }

    public static DatabaseReference getLocationOfHousehold(String householdId) {
        return mDatabase.child("households")
                .child(householdId)
                .child("location");
    }

    public static void addItem(String itemName, String itemDescription, String photoUri, String householdId) {
        Item item = new Item(itemName, itemDescription, photoUri, householdId);
        DatabaseReference itemRef = mDatabase
                .child("households")
                .child(householdId)
                .child("location")
                .child("items");
        itemRef.get().addOnCompleteListener(snapshot -> {
            List<Item> list = (List<Item>) snapshot.getResult().getValue();
            if(list == null) {
                list = new ArrayList<Item>();
            }
            list.add(item);
            itemRef.setValue(list);
        });
    }

    public static void deleteItem(Item item) {
        Storage.deleteImage(item.getPhotoUri());

        // Delete the item from Database
        DatabaseReference itemRef = mDatabase
                .child("households")
                .child(item.getHouseholdId())
                .child("location")
                .child("items");

        Log.d("Database", "Deleting item: " + item.getName() + " from " + item.getHouseholdId());

        itemRef.orderByChild("id").equalTo(item.getId()).get().addOnCompleteListener(snapshot -> {
            if (snapshot.isSuccessful() && snapshot.getResult().hasChildren()) {
                for (DataSnapshot child : snapshot.getResult().getChildren()) {
                    child.getRef().removeValue();
                }
            }
        });
    }

    public static DatabaseReference getAllHouseholds() {
        return mDatabase.child("households");
    }
}
