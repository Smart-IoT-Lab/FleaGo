package com.example.fleago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarketListView";

    private SlidingUpPanelLayout mLayout;

    TextView tv ;
    ArrayList<Market> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /******************* Sliding up List View *******************/

        ListView lv = (ListView) findViewById(R.id.marketList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

        final ArrayAdapter<Market> arrayAdapter = new ArrayAdapter<Market>(
                this,
                android.R.layout.simple_list_item_1,
                list );
        lv.setAdapter(arrayAdapter);

        mLayout = findViewById(R.id.sliding_layout);
//        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                Log.i(TAG, "onPanelStateChanged " + newState);
//            }
//        });

        // 바깥 눌렀을 때, 반응
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        /******************* Sliding up List View END *******************/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Market");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Market.class));
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(Market.class));
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

//        setmarketlist(myRef);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

//    void setmarketlist(final DatabaseReference ref) {
//        tv = findViewById(R.id.tv);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String market = "";
//
//                ArrayList<Market> Marketlist = new ArrayList<>();
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                    Marketlist.add(singleSnapshot.getValue(Market.class));
//                    market = market.concat(singleSnapshot.getValue(Market.class).toString() + "\n");
//                }
//
//                tv.setText(market);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

}

