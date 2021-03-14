package com.example.dwbillapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText confirm;
    private EditText email;
    private Button signIn;
    private TextView empty;
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        databaseUsers= FirebaseDatabase.getInstance().getReference("Users");

        username=(EditText)findViewById(R.id.etUsername2);
        password=(EditText)findViewById(R.id.etPassword2);
        confirm=(EditText)findViewById(R.id.etConfirm);
        email=(EditText)findViewById(R.id.etEmail);
        signIn=(Button)findViewById(R.id.btnSignIn2);
        empty=(TextView)findViewById(R.id.tvEmpty);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    private void addUser(){
        String name=username.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String cPass=confirm.getText().toString().trim();
        String address=email.getText().toString().trim();


        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag=false;
                boolean flagEmail=false;
                for(DataSnapshot snap : snapshot.getChildren()){
                    User user=snap.getValue(User.class);
                    String usernameFromDB=user.getUsername();
                    String emailFromDB=user.getEmail();
                    if(usernameFromDB.equals(name)){
                        flag=true;
                    }
                    if(emailFromDB.equals(address)){
                        flagEmail=true;
                    }
                }





                if(flag){
                    username.setError("Username already exists");
                }else if(flagEmail){
                    email.setError("Email address is already used");
                } else{
                    if(name.isEmpty()){
                        username.setError("Enter username");
                    }else if (pass.isEmpty()){
                        password.setError("Enter password");
                    }else if(cPass.isEmpty()){
                        confirm.setError("Confirm password");
                    }else if(address.isEmpty()){
                        email.setError("Enter email address");
                    }else if(!(pass.equals(cPass))){
                        confirm.setError("Enter correct password");
                    }else{
                        String id = databaseUsers.push().getKey();
                        User user = new User(id, name, pass, address);
                        databaseUsers.child(id).setValue(user);
                        Intent intent=new Intent(SignIn.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}