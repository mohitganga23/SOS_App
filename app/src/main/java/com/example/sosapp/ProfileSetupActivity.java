package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileSetupActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile Setup");

        TextInputLayout input_name = findViewById(R.id.name_text_field);
        TextInputLayout input_email = findViewById(R.id.email_text_field);
        TextInputLayout input_contact_num = findViewById(R.id.contact_num_text_field);

        Button next = findViewById(R.id.btn_continue);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            user_id = firebaseAuth.getCurrentUser().getUid();
            input_email.getEditText().setText(firebaseAuth.getCurrentUser().getEmail());
        }

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String current_name = task.getResult().getString("name");
                        String current_email = task.getResult().getString("email");
                        String current_contact_num = task.getResult().getString("contact_num");

                        input_name.getEditText().setText(current_name);
                        input_email.getEditText().setText(current_email);
                        input_contact_num.getEditText().setText(current_contact_num);
                    }
                } else {
                    Toast.makeText(ProfileSetupActivity.this, "Firestore Retrieve Error : " +
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input_name.getEditText().getText().toString().trim();
                String email = input_email.getEditText().getText().toString().trim();
                String con_num = input_contact_num.getEditText().getText().toString().trim();

                if (name.isEmpty()){
                    input_name.setError("Please provide a valid name");
                    input_name.requestFocus();
                } else if (email.isEmpty()){
                    input_email.setError("Please provide a valid email");
                    input_email.requestFocus();
                } else if (con_num.isEmpty()){
                    input_contact_num.setError("Please provide a valid contact number");
                    input_contact_num.requestFocus();
                } else{
                    storeFirestore(name,email,con_num);
                }
            }
        });
    }

    private void storeFirestore(String name, String email, String contact_num) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("contact_num", contact_num);
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(ProfileSetupActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(ProfileSetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}