package com.ttl.ritz7chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {
    CircleImageView imageProfile;
    EditText usernameEdt, statusEdt, userTaskedt;
    Button btnUpdate;
    String getCurrUserID;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        InitializeSettingFeilds();
        mAuth = FirebaseAuth.getInstance();
        getCurrUserID =  mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
//        usernameEdt.setVisibility(View.INVISIBLE);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {

        databaseReference.child("UsersChat").child(getCurrUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))){

                    String getUsername = snapshot.child("name").getValue().toString();
                    String getTask = snapshot.child("task").getValue().toString();
                    String getStatus = snapshot.child("status").getValue().toString();
                    String getImage = snapshot.child("image").getValue().toString();

                    usernameEdt.setText(getUsername);
                    statusEdt.setText(getStatus);
                    userTaskedt.setText(getTask);


                }else if((snapshot.exists()) && (snapshot.hasChild("name"))){
                    String getUsername = snapshot.child("name").getValue().toString();
                    String getTask = snapshot.child("task").getValue().toString();
                    String getStatus = snapshot.child("status").getValue().toString();
//                    String getImage = snapshot.child("image").getValue().toString();

                    usernameEdt.setText(getUsername);
                    statusEdt.setText(getStatus);
                    userTaskedt.setText(getTask);
                }else {
                    usernameEdt.setVisibility(View.VISIBLE);
                    Toast.makeText(settingsActivity.this, "Please Update Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateSettings() {
        String setUsername = usernameEdt.getText().toString();
        String setStatus = statusEdt.getText().toString();
        String setTask = userTaskedt.getText().toString();

        if (TextUtils.isEmpty(setUsername)){
            usernameEdt.setError("Enter Valid Username");
            usernameEdt.requestFocus();
            return;
        }else if (TextUtils.isEmpty(setStatus)){
            statusEdt.setError("Enter Valid Status");
            statusEdt.requestFocus();
            return;
        }else if (TextUtils.isEmpty(setTask)){
            userTaskedt.setError("Enter Valid Status");
            userTaskedt.requestFocus();
            return;
        }

        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("uid",getCurrUserID);
        profileMap.put("name",setUsername);
        profileMap.put("status",setStatus);
        profileMap.put("task",setTask);

        databaseReference.child("UsersChat").child(getCurrUserID).setValue(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            sendToMainActivity();
                            Toast.makeText(settingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        }else {
                            String msg = task.getException().toString();
                            Toast.makeText(settingsActivity.this, "Error" +msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void InitializeSettingFeilds() {
        userTaskedt = findViewById(R.id.set_usertask);
        usernameEdt = findViewById(R.id.set_username);
        statusEdt = findViewById(R.id.set_status);
        imageProfile = findViewById(R.id.profile_image);
        btnUpdate = findViewById(R.id.btnUpdate);
    }
    private void sendToMainActivity() {
        Intent intent = new Intent(settingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(settingsActivity.this,MainActivity.class));
    }
}