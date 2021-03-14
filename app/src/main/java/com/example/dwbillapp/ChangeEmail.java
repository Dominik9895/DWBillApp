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

public class ChangeEmail extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarEmail);
        setSupportActionBar(toolbar);

        //Logic

        email=findViewById(R.id.etNewEmail);
        password=findViewById(R.id.etPassInEmail);
        submit=findViewById(R.id.btnSubmitNewEmail);
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
            Intent intent1=new Intent(ChangeEmail.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(ChangeEmail.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(ChangeEmail.this, MainMenu.class);
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

    private boolean valEmail(){
        String val=email.getText().toString();
        if(val.isEmpty()){
            email.setError("Enter new username");
            return false;
        }else{
            return true;
        }
    }

    private void change(){
        if(!valEmail()||!valPassword()){
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
                    String emailFromUser=email.getText().toString().trim();
                    if(passwordFromDB.equals(passwordFromUser)){
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("email").setValue(emailFromUser);
                        Intent goTo=new Intent(ChangeEmail.this, MainMenu.class);
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