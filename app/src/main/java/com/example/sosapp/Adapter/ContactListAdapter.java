package com.example.sosapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sosapp.Model.Contacts;
import com.example.sosapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    public Context context;
    public List<Contacts> contactsList;
    public List<String> contact_id;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public ContactListAdapter(List<Contacts> contactsList, List<String> contact_id) {
        this.contactsList = contactsList;
        this.contact_id = contact_id;
    }

    @NonNull
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card_view, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String current_contact_id = contact_id.get(position);
        String current_name = contactsList.get(position).getName();
        String current_number = contactsList.get(position).getContact_num();
        holder.setContact(current_name, current_number);

        String user_id = firebaseAuth.getCurrentUser().getUid();
        holder.delete_contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete contact?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("Users/" + user_id + "/Contact_List")
                                .document(current_contact_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                contactsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, contactsList.size());
                                notifyDataSetChanged();
                                holder.itemView.setVisibility(View.GONE);
                                Toast.makeText(context, "Contact Removed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contactsList.size() != 0) {
            return contactsList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final ImageButton delete_contact_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            delete_contact_btn = mView.findViewById(R.id.delete_contact_btn);
        }

        public void setContact(String current_name, String current_number) {
            TextView name = mView.findViewById(R.id.contact_name);
            name.setText(current_name);
            TextView contact_num = mView.findViewById(R.id.contact_number);
            contact_num.setText(current_number);
        }
    }
}
