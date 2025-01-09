package com.mynotes.controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class DemoController {

    @GetMapping("/add-name")
    public String addName() {
        try {
            // Get a reference to the Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Reference to the 'names' collection
            DatabaseReference namesRef = databaseReference.child("names");

            // Generate a unique key for the new name entry
            String nameKey = namesRef.push().getKey();

            // Check if the nameKey is null
            if (nameKey == null) {
                return "Failed to generate unique key.";
            }

            // Use a Map to structure the data
            Map<String, Object> nameData = new HashMap<>();
            nameData.put("name", "Abjeet");

            // Set the value using the Map (to handle data in a structured way)
            namesRef.child(nameKey).setValueAsync(nameData);

            return "Name 'Abjeet' added successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add name: " + e.getMessage();
        }
    }
}




