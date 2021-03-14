package com.example.dwbillapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SpecificGroup extends AppCompatActivity {

    private Button settleUp;
    private Button addExpense;
    private Button addMember;
    private TextView name;
    private TextView message;
    private ListView listView;
    ArrayList<String> members;
    DatabaseReference databaseUsers;
    private double sum=0d;
    private double average=0d;
    private double paid;
    private String mess="";
    private String username;
    private double losba;
    private double subsum;
    private int countSettles;
    private int countMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_group);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarSpecific);
        setSupportActionBar(toolbar);

        //Logika

        Intent goToSpecificGroup=getIntent();
        String gid=goToSpecificGroup.getStringExtra("gid");
        String uid=goToSpecificGroup.getStringExtra("uid");
        String specificName=goToSpecificGroup.getStringExtra("name");
        String currency=goToSpecificGroup.getStringExtra("currency");
        settleUp=findViewById(R.id.btnSettleUp);
        addMember=findViewById(R.id.btnAddMember);
        addExpense=findViewById(R.id.btnAddExpense);
        name=findViewById(R.id.tvSpecificName);
        name.setText(specificName.toUpperCase());
        message=findViewById(R.id.tvMessage);
        listView=findViewById(R.id.lvSpecific);
        members=new ArrayList<>();
        databaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        checkSettle();
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMemberInt=new Intent(SpecificGroup.this, AddMember.class);
                addMemberInt.putExtra("uid", uid);
                addMemberInt.putExtra("gid", gid);
                addMemberInt.putExtra("name", specificName);
                addMemberInt.putExtra("currency", currency);
                startActivity(addMemberInt);
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addExpenseInt=new Intent(SpecificGroup.this, AddExpense.class);
                addExpenseInt.putExtra("uid", uid);
                addExpenseInt.putExtra("gid", gid);
                addExpenseInt.putExtra("name", specificName);
                addExpenseInt.putExtra("currency", currency);
                startActivity(addExpenseInt);
            }
        });
        settleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uid).child("settled").setValue(1);
                Toast.makeText(SpecificGroup.this, "You are settled", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
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
            Intent intent1=new Intent(SpecificGroup.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(SpecificGroup.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(SpecificGroup.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logika

    private void checkSettle(){
        Intent goToSpecificGroup=getIntent();
        String gid=goToSpecificGroup.getStringExtra("gid");
        String uid=goToSpecificGroup.getStringExtra("uid");
        FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Member member=snap.getValue(Member.class);
                    int settled=member.getSettled();
                    if(settled==1){
                        countSettles++;
                    }

                }
                FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap2 : snapshot.getChildren()){
                            countMembers++;
                        }
                        if(countMembers==countSettles && !(countMembers ==0)){
                            message.setText("You are all settled");
                            FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot snap3 : snapshot.getChildren()){
                                        Member member=snap3.getValue(Member.class);
                                        String uidFromDB=member.getUid();
                                        FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uidFromDB).child("outcome").setValue(0);
                                    }

                                   showMembers();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });

                        }else{
                            showMembers();
                            count();
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

    private void showMembers(){
        members.clear();
        Intent intent2=getIntent();
        String gid=intent2.getStringExtra("gid");
        FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Member member=snap.getValue(Member.class);
                    String uidFromGroup=member.getUid();
                    double paid=member.getOutcome();
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot3) {
                            Group group=snapshot3.getValue(Group.class);
                            String currency=group.getCurrency();
                            FirebaseDatabase.getInstance().getReference().child("Users").child(uidFromGroup).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    User user=snapshot2.getValue(User.class);
                                    String nameFromGroup=user.getUsername();
                                    members.add(nameFromGroup + "   " + paid + " " + currency);
                                    ArrayAdapter adapter=new ArrayAdapter(SpecificGroup.this, android.R.layout.simple_list_item_1, members);
                                    listView.setAdapter(adapter);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void count(){
        Intent intent3=getIntent();
        String gid=intent3.getStringExtra("gid");
        String uid=intent3.getStringExtra("uid");
        String currency=intent3.getStringExtra("currency");

        ArrayList<Member> membersToPay=new ArrayList<>();
        ArrayList<Member> debtors=new ArrayList<>();
        ArrayList<Member> waiting=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Member member=snap.getValue(Member.class);
                    double outcome=member.getOutcome();
                    sum+=outcome;
                    membersToPay.add(member);
                }
                average=sum/membersToPay.size();
                double test=average;
                //tworzenie list
                for(Member member : membersToPay){
                    if(member.getOutcome()<average){
                        debtors.add(member);
                    }else if(member.getOutcome()>average){
                        waiting.add(member);
                    }
                }
                //szukanie uid na listach
                boolean flagDebtors=false;
                boolean flagWaiting=false;
                for(Member member : debtors){
                    if(member.getUid().equals(uid)){
                        flagDebtors=true;
                    }
                }
                for(Member member : waiting){
                    if(member.getUid().equals(uid)){
                        flagWaiting=true;
                    }
                }





                if(!flagDebtors&&!flagWaiting){
                    message.setText("You are settled up");
                //dłużnik
                }else if(flagDebtors&&!flagWaiting){
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Member member=snapshot.getValue(Member.class);
                            paid=member.getOutcome();
                            double absol=Math.abs(paid-average);
                            mess="You owe: \n";
                            //Pobieram czekających; ich nazwy i losba
                            for(Member member2 : waiting){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(member2.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user=snapshot.getValue(User.class);
                                        username=user.getUsername();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                losba=Math.abs(average-member2.getOutcome());
                                subsum+=losba;
                            }
                            //liczę dług i wyświetlam wiadomosc
                            for(Member member3 : waiting){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(member3.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user=snapshot.getValue(User.class);
                                        username=user.getUsername();
                                        losba=Math.abs(average-member3.getOutcome());
                                        double x=losba/subsum*absol;
                                        DecimalFormat df=new DecimalFormat("#.##");
                                        mess+=username + " " + df.format(x) + " " + currency + "\n";
                                        message.setText(mess);
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
            //czekający
            else if(!flagDebtors&&flagWaiting){
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Member member=snapshot.getValue(Member.class);
                            paid=member.getOutcome();
                            double absol=Math.abs(paid-average);
                            //Pobieram dłużników; ich nazwy
                            for(Member member2 : debtors){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(member2.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user=snapshot.getValue(User.class);
                                        username=user.getUsername();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                losba=Math.abs(average-member2.getOutcome());
                                subsum+=losba;
                            }
                            //liczę dług i wyświetlam wiadomosc
                            for(Member member3 : debtors){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(member3.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user=snapshot.getValue(User.class);
                                        username=user.getUsername();
                                        losba=Math.abs(average-member3.getOutcome());
                                        double a=losba/subsum*absol;
                                        DecimalFormat df=new DecimalFormat("#.##");
                                        mess+=username + " owes you " + df.format(a) + " " + currency + "\n";
                                        message.setText(mess);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
