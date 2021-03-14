package com.example.dwbillapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Change;

public class ChangeUsername extends AppCompatActivity {
    private EditText newName;
    private EditText password;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarName);
        setSupportActionBar(toolbar);

        //Logic

        newName=findViewById(R.id.etNewName);
        password=findViewById(R.id.etPassInName);
        submit=findViewById(R.id.btnSubmitNewUsername);
        Intent intent10=getIntent();
        String uid=intent10.getStringExtra("uid");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });
    }

    //Toolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=getIntent();
        String uid=intent.getStringExtra("uid");
        int id=item.getItemId();
        if(id==R.id.logOut){
            Intent intent1=new Intent(ChangeUsername.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(ChangeUsername.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(ChangeUsername.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logic
    private boolean valPassword(){
        String val=password.getText().toString();
        if(val.isEmpty()){
            password.setError("Enter password");
            return false;
        }else{
            return true;
        }
    }

    private boolean valName(){
        String val=newName.getText().toString();
        if(val.isEmpty()){
            newName.setError("Enter new username");
            return false;
        }else{
            return true;
        }
    }

    private void change(){
        if(!valName()||!valPassword()){
            return;
        }else{
            Intent intent1=getIntent();
            String uid=intent1.getStringExtra("uid");
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    String passwordFromDB=user.getPassword().toString().trim();
                    String passwordFromUser=password.getText().toString().trim();
                    String usernameFromUser=newName.getText().toString().trim();
                    if(passwordFromDB.equals(passwordFromUser)){
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("username").setValue(usernameFromUser);
                        Intent goTo=new Intent(ChangeUsername.this, MainMenu.class);
                        goTo.putExtra("uid", uid);
                        startActivity(goTo);
                    }else if(!passwordFromDB.equals(passwordFromUser)){
                        password.setError("Wrong password");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}