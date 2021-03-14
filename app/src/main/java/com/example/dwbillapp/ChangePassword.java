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

public class ChangePassword extends AppCompatActivity {

    private EditText oldPassword;
    private EditText newPassword;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarPassword);
        setSupportActionBar(toolbar);

        //Logic

        newPassword=findViewById(R.id.etNewPassword);
        oldPassword=findViewById(R.id.etPassInPass);
        submit=findViewById(R.id.btnSubmitNewPassword);
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
            Intent intent1=new Intent(ChangePassword.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(ChangePassword.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(ChangePassword.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logic
    private boolean valOldPass(){
        String val=oldPassword.getText().toString();
        if(val.isEmpty()){
            oldPassword.setError("Enter old password");
            return false;
        }else{
            return true;
        }
    }

    private boolean valNewPass(){
        String val=newPassword.getText().toString();
        if(val.isEmpty()){
            newPassword.setError("Enter new password");
            return false;
        }else{
            return true;
        }
    }

    private void change(){
        if(!valOldPass()||!valNewPass()){
            return;
        }else{
            Intent intent1=getIntent();
            String uid=intent1.getStringExtra("uid");
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    String passwordFromDB=user.getPassword().toString().trim();
                    String passwordFromUser=oldPassword.getText().toString().trim();
                    String newPassFromUser=newPassword.getText().toString().trim();
                    if(passwordFromDB.equals(passwordFromUser)){
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("password").setValue(newPassFromUser);
                        Intent goTo=new Intent(ChangePassword.this, MainMenu.class);
                        goTo.putExtra("uid", uid);
                        startActivity(goTo);
                    }else if(!passwordFromDB.equals(passwordFromUser)){
                        oldPassword.setError("Wrong password");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}