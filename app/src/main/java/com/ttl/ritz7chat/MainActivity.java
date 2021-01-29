package com.ttl.ritz7chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference  databaseReference;
    boolean doubleBackToExitPressedOnce = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.main_page_toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ritz7Chat");
        viewPager =(ViewPager) findViewById(R.id.main_tabs_pager);
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);
        tabLayout =(TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser ==  null){
            sendToLoginActivity();
        }else {
            veifyUserExistance();

        }
    }

    private void veifyUserExistance() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.child("UsersChat").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }else{
                    sendToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_option){
            mAuth.signOut();
            sendToLoginActivity();
        }
        if(item.getItemId() == R.id.main_settings_option){
            sendToSettingActivity();
        }
        if(item.getItemId() == R.id.main_create_group_option){
            requesNewGroup();
        }
        if(item.getItemId() == R.id.main_find_user_option){

        }
        return true;
    }

    private void requesNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupNameText = new EditText(MainActivity.this);
        groupNameText.setHint("e.g. Developer Group");
        builder.setView(groupNameText);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameText.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please Enter group Name...", Toast.LENGTH_SHORT).show();
                }else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createNewGroup(String groupName) {
        databaseReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName+ "group created successfully..", Toast.LENGTH_SHORT).show();
                }     
            }
        });
    }

    private void sendToSettingActivity() {
        Intent intentSetting = new Intent(MainActivity.this,settingsActivity.class);
        intentSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentSetting);
        finish();
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    
}