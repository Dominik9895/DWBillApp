package com.example.dwbillapp;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {
    private ListView listView;
    private ImageButton addGroup;
    ArrayList<String> strings;
    ArrayList<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //Logika

        Intent intent=getIntent();
        String uid=intent.getStringExtra("uid");

        listView=findViewById(R.id.lvGroups);
        addGroup=findViewById(R.id.btnAddGroup);

        strings = new ArrayList<>();
        groups=new ArrayList<>();
        showGroups();
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddGroup=new Intent(MainMenu.this, AddGroup.class);
                goToAddGroup.putExtra("uid", uid);
                startActivity(goToAddGroup);
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
            Intent intent1=new Intent(MainMenu.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(MainMenu.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(MainMenu.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logika

    private boolean showGroups() {
        Intent intent=getIntent();
        String uid=intent.getStringExtra("uid");
        FirebaseDatabase.getInstance().getReference().child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Group group=snap.getValue(Group.class);
                    String name=group.getName();
                    String gid=group.getGid();
                    String currency=group.getCurrency();



                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap : snapshot.getChildren()){
                                Member member=snap.getValue(Member.class);
                                String uidFromMember=member.getUid();
                                if(uidFromMember.equals(uid)){
                                    strings.add(name);
                                    groups.add(group);

                                }
                            }
                            ArrayAdapter adapter=new ArrayAdapter(MainMenu.this, android.R.layout.simple_list_item_1, strings);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String test=groups.get(position).getName();
                                    Intent goToSpecificGroup=new Intent(MainMenu.this, SpecificGroup.class);
                                    goToSpecificGroup.putExtra("uid", uid);
                                    goToSpecificGroup.putExtra("gid", groups.get(position).getGid());
                                    goToSpecificGroup.putExtra("name", groups.get(position).getName());
                                    goToSpecificGroup.putExtra("currency", groups.get(position).getCurrency());
                                    startActivity(goToSpecificGroup);
                                }
                            });
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println(error.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }


}