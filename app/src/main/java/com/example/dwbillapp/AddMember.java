package com.example.dwbillapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMember extends AppCompatActivity {

    private EditText member;
    private Button submit;
    private String searchedUID="";
    private String newUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarMember);
        setSupportActionBar(toolbar);

        //Logika

        Intent getFromSpecificGroup=getIntent();
        String gid=getFromSpecificGroup.getStringExtra("gid");
        String uid=getFromSpecificGroup.getStringExtra("uid");
        String name=getFromSpecificGroup.getStringExtra("name");
        String currency=getFromSpecificGroup.getStringExtra("currency");
        member=findViewById(R.id.etMember);
        submit=findViewById(R.id.btnSubmitMember);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();

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
            Intent intent1=new Intent(AddMember.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(AddMember.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(AddMember.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logika

    private void addMember() {
        String usernameFromET = member.getText().toString().trim();
        Intent intent = getIntent();
        String gid = intent.getStringExtra("gid");
        String uid = intent.getStringExtra("uid");
        String name = intent.getStringExtra("name");
        String currency=intent.getStringExtra("currency");
        if (!usernameFromET.isEmpty()) {
            String username = usernameFromET;
            FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User user = snap.getValue(User.class);
                        String usernameFromDB = user.getUsername();
                        String uidFromDB = user.getUid();
                        if (usernameFromDB.equals(usernameFromET)) {
                            searchedUID = uidFromDB;
                        }
                    }
                    if (searchedUID.isEmpty()) {
                        member.setError("User doesn't exist");
                    }else if(usernameFromET.isEmpty()){
                        member.setError("Enter username");
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean flag = false;
                                for (DataSnapshot snap2 : snapshot.getChildren()) {
                                    Member memb = snap2.getValue(Member.class);
                                    String uidFromDB = memb.getUid();
                                    if (uidFromDB.equals(searchedUID)) {
                                        flag = true;
                                    }
                                    else{
                                        newUID=searchedUID;
                                    }
                                }
                                if (flag) {
                                    member.setError("User is already in group");
                                } else {
                                    Member newMember=new Member(newUID, 0d, 0);
                                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(newUID).setValue(newMember);
                                    Intent intent2=new Intent(AddMember.this, SpecificGroup.class);
                                    intent2.putExtra("uid", uid);
                                    intent2.putExtra("gid", gid);
                                    intent2.putExtra("name", name);
                                    intent2.putExtra("currency", currency);
                                    finish();
                                    startActivity(intent2);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}