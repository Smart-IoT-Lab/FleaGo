package com.example.fleago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView tv ;
    ListView listview;
    //ArrayAdapter adapter;
    ArrayList<Market> list=new ArrayList<Market>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<Market> adapter= new ArrayAdapter<Market>(this, android.R.layout.simple_dropdown_item_1line,list);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent1=new Intent(getApplicationContext(), Market.class);
                Intent intent1=new Intent(MainActivity.this, Main2Activity.class);
                intent1.putExtra("name", list.get(position).getName());
                intent1.putExtra("district", list.get(position).getDistrict());
                intent1.putExtra("event_type", list.get(position).getEvent_type());
                intent1.putExtra("location", list.get(position).getLocation());
                intent1.putExtra("introduction", list.get(position).getIntroduction());
                intent1.putExtra("page_url", list.get(position).getPage_url());
                startActivity(intent1);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Market");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Market.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(Market.class));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        setmarketlist(myRef);
    }

    void setmarketlist(final DatabaseReference ref) {
        tv = findViewById(R.id.tv);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String market = "";

                ArrayList<Market> Marketlist = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Marketlist.add(singleSnapshot.getValue(Market.class));
                    market = market.concat(singleSnapshot.getValue(Market.class).toString() + "\n");
                }

                tv.setText(market);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

