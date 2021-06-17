package com.example.sosapp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ContactID {
    @Exclude
    public String ContactID;

    public <T extends ContactID> T withID(@NonNull final String id) {
        this.ContactID = id;
        return (T) this;
    }
}
