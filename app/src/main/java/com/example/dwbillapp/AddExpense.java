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

public class AddExpense extends AppCompatActivity {

    private EditText expense;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        //Toolbar

        Toolbar toolbar=findViewById(R.id.toolbarExpense);
        setSupportActionBar(toolbar);

        //Logika

        Intent getFromSpecificGroup=getIntent();
        String gid=getFromSpecificGroup.getStringExtra("gid");
        String uid=getFromSpecificGroup.getStringExtra("uid");
        String name=getFromSpecificGroup.getStringExtra("name");
        expense=findViewById(R.id.etExpense);
        submit=findViewById(R.id.btnSubmitExpense);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();

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
            Intent intent1=new Intent(AddExpense.this, MainActivity.class);
            startActivity(intent1);
        }else if(id==R.id.settings){
            Intent intent2=new Intent(AddExpense.this, Settings.class);
            intent2.putExtra("uid", uid);
            startActivity(intent2);
        }else if(id==R.id.home){
            Intent intent3=new Intent(AddExpense.this, MainMenu.class);
            intent3.putExtra("uid", uid);
            startActivity(intent3);
        }
        return true;
    }
    //Logika

    private void addExpense(){
        String paidFromET=expense.getText().toString().trim();
        Intent intent=getIntent();
        String gid=intent.getStringExtra("gid");
        String uid=intent.getStringExtra("uid");
        String name=intent.getStringExtra("name");
        String currency=intent.getStringExtra("currency");
        System.out.println(currency);
        if (!paidFromET.isEmpty()) {
            double paid = Double.parseDouble(paidFromET);
            FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Member member=snapshot.getValue(Member.class);
                    double paidFromDB=member.getOutcome();
                    double newPaid=paid+paidFromDB;
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uid).child("outcome").setValue(newPaid);
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap : snapshot.getChildren()){
                                Member member=snap.getValue(Member.class);
                                String uidFromDB=member.getUid();
                                FirebaseDatabase.getInstance().getReference().child("Groups").child(gid).child("Members").child(uidFromDB).child("settled").setValue(0);
                            }
                            Intent goBack=new Intent(AddExpense.this, SpecificGroup.class);
                            goBack.putExtra("uid", uid);
                            goBack.putExtra("gid", gid);
                            goBack.putExtra("name", name);
                            goBack.putExtra("currency", currency);
                            System.out.println(currency);
                            finish();
                            startActivity(goBack);
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
        }else{
            expense.setError("Enter the expense");
        }
    }
}