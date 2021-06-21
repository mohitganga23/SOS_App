package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sosapp.Adapter.ContactListAdapter;
import com.example.sosapp.Model.Contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SecurityNetworkActivity extends AppCompatActivity {

    public final String TAG = "tag";
    public static int PICK_CONTACT = 101;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    int total_contacts = 0;
    private ContactListAdapter contactListAdapter;
    List<Contacts> contactsList;
    List<String> id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_network);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Your Network");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView contact_recycler_view = findViewById(R.id.contact_recycler_view);
        TextView count = findViewById(R.id.count);
        count.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();

        contactsList = new ArrayList<>();
        id = new ArrayList<>();

        contactListAdapter = new ContactListAdapter(contactsList,id);

        contact_recycler_view.setHasFixedSize(true);
        contact_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        contact_recycler_view.setAdapter(contactListAdapter);

        firebaseFirestore.collection("Users/" + user_id + "/Contact_List").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    count.setVisibility(View.GONE);
                    for (DocumentChange documentChange: value.getDocumentChanges()){
                        Contacts contacts = documentChange.getDocument().toObject(Contacts.class);
                        String current_id = documentChange.getDocument().getId();
                        contactsList.add(contacts);
                        id.add(current_id);
                        contactListAdapter.notifyDataSetChanged();
                    }
                } else {
                    count.setVisibility(View.VISIBLE);
                }
            }
        });

        firebaseFirestore.collection("Users/" + user_id + "/Contact_List").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (!value.isEmpty()) {
                        total_contacts = value.size();
                    }
                }
            }
        });

        Button add_contact = findViewById(R.id.add_contact);
        add_contact.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                if (total_contacts < 5) {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(i, PICK_CONTACT);
                } else {
                    Toast.makeText(SecurityNetworkActivity.this,
                            "Limit Reached!\nCannot add more contacts", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult()");
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                CursorLoader loader = new CursorLoader(this, contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                assert c != null;
                if (c.moveToFirst()) {
                    String phone_number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    addContact(name, phone_number);
                }
            }
        }
    }

    private void addContact(String name, String phone_number) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("contact_num", phone_number);

        firebaseFirestore.collection("Users/" + user_id + "/Contact_List").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SecurityNetworkActivity.this, "Contact added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}