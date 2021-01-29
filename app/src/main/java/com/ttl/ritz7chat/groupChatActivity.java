package com.ttl.ritz7chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class groupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText userMsgText;
    private TextView textViewDisplayMsg;
    private ImageButton btnSendMsg;
    private ScrollView scrollView;
    String getGroupName, currUserId, currUsername, currDate, currTime;
    DatabaseReference databaseReference, groupRef,groupMsgRefKey;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getGroupName = getIntent().getExtras().get("GroupName").toString();

        auth = FirebaseAuth.getInstance();
        currUserId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(getGroupName);
        initializeFeilds();
        getUserInfo();

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMsgToDatabase();
                userMsgText.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    displayMsg(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    displayMsg(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayMsg(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMsg = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
            textViewDisplayMsg.append(chatName + ":\n" + chatMsg + "\n" + chatTime + "    " + chatDate + "\n\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void saveMsgToDatabase() {

        String message = userMsgText.getText().toString();
        String msgKey = groupRef.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Empty message", Toast.LENGTH_SHORT).show();
        }else {

            Calendar cForDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
            currDate = dateFormat.format(cForDate.getTime());

            Calendar cForTime = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            currTime = timeFormat.format(cForTime.getTime());

            HashMap<String,Object> groupMsgKey = new HashMap<>();
            groupRef.updateChildren(groupMsgKey);

            groupMsgRefKey = groupRef.child(msgKey);
            HashMap<String,Object> msgInfoMap = new HashMap<>();

            msgInfoMap.put("name", currUsername);
            msgInfoMap.put("message", message);
            msgInfoMap.put("date", currDate);
            msgInfoMap.put("time", currTime);
            groupMsgRefKey.updateChildren(msgInfoMap);

        }
    }

    private void getUserInfo() {

        databaseReference.child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currUsername = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFeilds() {

        toolbar = findViewById(R.id.groupChatLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getGroupName);
        userMsgText = findViewById(R.id.input_groupMsg);
        btnSendMsg = findViewById(R.id.send_msg);
        scrollView = findViewById(R.id.groupChatScrollview);
        textViewDisplayMsg = findViewById(R.id.grpChatMsgDisplay);


    }
}