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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button logIn;
    private Button signIn;
    private TextView wrong;
    private String uid;

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
                isUser();
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

    private void isUser() {
        String userEnteredUsername=username.getText().toString().trim();
        String userEnteredPassword=password.getText().toString().trim();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    User user=snap.getValue(User.class);
                    String tempUsername=user.getUsername();
                    if(tempUsername.equals(userEnteredUsername)){
                        uid=user.getUid();
                    }
                }

                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User tempUser = snapshot.getValue(User.class);
                            String passwordFromDB = tempUser.getPassword();
                            if (passwordFromDB.equals(userEnteredPassword)) {
                                Intent intentLogIn = new Intent(MainActivity.this, MainMenu.class);
                                intentLogIn.putExtra("uid", uid);
                                startActivity(intentLogIn);
                            } else {
                                password.setError("Wrong username or password");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private String getUid(String username) {
//        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot snap : snapshot.getChildren()){
//                    User user=snap.getValue(User.class);
//                    String possibility=user.getUsername();
//                    if(possibility.equals(username)){
//                        result=user.getUid();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return result;
//    }


}