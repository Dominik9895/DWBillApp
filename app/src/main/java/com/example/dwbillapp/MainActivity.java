package com.example.dwbillapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button logIn;
    private Button signIn;
    private TextView wrong;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username=(EditText)findViewById(R.id.etUsername);
        password=(EditText)findViewById(R.id.etPassword);
        logIn=(Button)findViewById(R.id.btnLogIn);
        signIn=(Button)findViewById(R.id.btnSignIn);
        wrong=(TextView)findViewById(R.id.tvWrong);


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!valUsername()||!valPassword()){
                    return;
                }else{
                    isUser();
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(MainActivity.this, SignIn.class);
                startActivity(intent2);
            }
        });
    }

    private boolean valUsername(){
        String val=username.getText().toString();
        if(val.isEmpty()){
            username.setError("Enter username");
            return false;
        }else{
            return true;
        }
    }

    private boolean valPassword(){
        String val=password.getText().toString();
        if(val.isEmpty()){
            password.setError("Enter password");
            return false;
        }else{
            return true;
        }
    }

    public void login(View view){
        if(!valUsername()||!valPassword()){
            return;
        }else{
            isUser();
        }
    }


    private void isUser() {
        String userEnteredUsername=username.getText().toString().trim();
        String userEnteredPassword=password.getText().toString().trim();
        String uid=getUid(userEnteredUsername);
        DatabaseReference usersList=FirebaseDatabase.getInstance().getReference("Users");
        Query check=usersList.orderByChild("uid").equalTo(uid);
        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wrong.setVisibility(View.INVISIBLE);
                if(snapshot.exists()){

                    String passwordFromDB=snapshot.child(uid).child("password").getValue(String.class);
                    if(passwordFromDB.equals(userEnteredPassword)){
                        String usernameFromDB=snapshot.child(uid).child("username").getValue(String.class);
                        String emailFromDB=snapshot.child(uid).child("email").getValue(String.class);

                        Intent intent=new Intent(getApplicationContext(), MainMenu.class);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                    }else{
                        wrong.setVisibility(View.VISIBLE);
                    }
                }else{
                    wrong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String getUid(String username) {
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    User user=snap.getValue(User.class);
                    String possibility=user.getUsername();
                    if(possibility.equals(username)){
                        result=user.getUid();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return result;
    }


}