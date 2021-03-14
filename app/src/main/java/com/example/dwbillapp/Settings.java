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

public class Settings extends AppCompatActivity {

    private Button changeUsername;
    private Button changePassword;
    private Button changeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);



        //Logika

        Intent intent2=getIntent();
        String uid=intent2.getStringExtra("uid");

        changeUsername=findViewById(R.id.btnChangeUsername);
        changePassword=findViewById(R.id.btnChangePassword);
        changeEmail=findViewById(R.id.btnChangeEmail);

        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(Settings.this, ChangeUsername.class);
                intent1.putExtra("uid", uid);
                startActivity(intent1);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(Settings.this, ChangePassword.class);
                intent2.putExtra("uid", uid);
                startActivity(intent2);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(Settings.this, ChangeEmail.class);
                intent3.putExtra("uid", uid);
                startActivity(intent3);
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
            Intent intent1=new Intent(Settings.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(Settings.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(Settings.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
}