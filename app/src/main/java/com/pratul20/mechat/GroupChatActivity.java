package com.pratul20.mechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.pratul20.mechat.Adapters.ChatAdapter;
import com.pratul20.mechat.Models.MessagesModel;
import com.pratul20.mechat.databinding.ActivityGroupChatBinding;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.userName.setText("Friends Group");

        final ChatAdapter adapter = new ChatAdapter(messagesModels, this);
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModels.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                            messagesModels.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.send.setOnClickListener(view -> {
            final String message = binding.etMessage.getText().toString();
            final MessagesModel model = new MessagesModel(senderId, message);
            model.setTimestamp(new Date().getTime());

            binding.etMessage.setText("");

            database.getReference().child("Group Chat")
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
        });
    }
}