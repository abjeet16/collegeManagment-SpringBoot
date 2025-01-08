package com.mynotes.config.firbase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {
        // Dynamically get the project path
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + File.separator +"my-notes"+File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "firebase" + File.separator + "collegemanagmentsystem.json";

        System.out.println("Firebase config file path: " + filePath);

        FileInputStream serviceAccount = new FileInputStream(new File(filePath));

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://dinamictimetable-default-rtdb.firebaseio.com") // Replace with your actual database URL
                .build();

        FirebaseApp.initializeApp(options);
    }
}



