package com.mynotes.services.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    private final DatabaseReference database;

    public FirebaseService() {
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    public void saveData(String key, Object value) {
        database.child("your-node").child(key).setValueAsync(value);
    }

    public DatabaseReference getData(String key) {
        return database.child("your-node").child(key);
    }
}
