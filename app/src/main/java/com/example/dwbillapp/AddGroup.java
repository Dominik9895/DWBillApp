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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddGroup extends AppCompatActivity {

    private EditText groupName;
    private EditText currency;
    private Button createGroup;
    DatabaseReference databaseGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarAddGroup);
        setSupportActionBar(toolbar);

        //Logic

        databaseGroups= FirebaseDatabase.getInstance().getReference("Groups");
        groupName=findViewById(R.id.etGroupName);
        currency=findViewById(R.id.etCurrency);
        createGroup=findViewById(R.id.btnCreateGroup);
        Intent goToAddGroup=getIntent();
        String uid=goToAddGroup.getStringExtra("uid");
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!valCurrency()||!valGroupName()){
                    return;
                }else{
                    create();
                }

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
            Intent intent1=new Intent(AddGroup.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(AddGroup.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(AddGroup.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logika

    private boolean valGroupName(){
        String val=groupName.getText().toString();
        if(val.isEmpty()){
            groupName.setError("Enter group name");
            return false;
        }else{
            return true;
        }
    }

    private boolean valCurrency(){
        String val=currency.getText().toString();
        if(val.isEmpty()){
            currency.setError("Enter currency");
            return false;
        }else{
            return true;
        }
    }



    private void create() {
        Intent intent=getIntent();
        String uid=intent.getStringExtra("uid");
        ArrayList<Member> Members=new ArrayList<>();
        String userEnteredGroupName=groupName.getText().toString().trim();
        String userEnteredCurrency=currency.getText().toString().trim();
        String gid=databaseGroups.push().getKey();
        Group group=new Group(gid, userEnteredGroupName, userEnteredCurrency, Members);
        databaseGroups.child(gid).setValue(group);
        Member member = new Member(uid, 0d, 0);
        databaseGroups.child(gid).child("Members").child(uid).setValue(member);
        Toast.makeText(this, "Group added", Toast.LENGTH_LONG).show();
        Intent intent2=new Intent(AddGroup.this, MainMenu.class);
        intent2.putExtra("uid", uid);
        startActivity(intent2);
    }
}















