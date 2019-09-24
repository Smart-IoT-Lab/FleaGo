package com.example.fleago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

<<<<<<< HEAD
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.daimajia.swipe.util.Attributes;
=======

>>>>>>> c7a772a0c93ea43ad87dacf6fb88d29a803538e7
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import adapter.ListViewAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarketListView";

    private SlidingUpPanelLayout mLayout;
    private ListViewAdapter mAdapter;
    private ArrayList list;
    TextView tv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TEST
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button) findViewById(R.id.button)).setText("하이");
            }
        });

        /******************* Sliding up List View *******************/

        //어따놔야할까~!?!?!


        final ListView lv = (ListView) findViewById(R.id.marketList);
        // 클릭 시 스와이프 > 돋보기 등장. TODO 돋보기 버튼 클릭시 인텐트 변하도록 수정 필요
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //Intent intent1=new Intent(getApplicationContext(), Market.class);
//                Intent intent1=new Intent(MainActivity.this, Main2Activity.class);
//                intent1.putExtra("name", list.get(position).getName());
//                intent1.putExtra("district", list.get(position).getDistrict());
//                intent1.putExtra("event_type", list.get(position).getEvent_type());
//                intent1.putExtra("location", list.get(position).getLocation());
//                intent1.putExtra("introduction", list.get(position).getIntroduction());
//                intent1.putExtra("page_url", list.get(position).getPage_url());
//                startActivity(intent1);
//            }
//       });

        mAdapter = new ListViewAdapter(this, list);
        lv.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 클릭 시 돋보기 스와이프 되는 view
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "lv..setOnItemClick", Toast.LENGTH_SHORT).show();
                ((com.daimajia.swipe.SwipeLayout) (lv.getChildAt(position - lv.getFirstVisiblePosition()))).open(true);
            }
        });


//        final ArrayAdapter<Market> arrayAdapter = new ArrayAdapter<Market>(
//                this,
//                android.R.layout.simple_list_item_1,
//                list );
//        lv.setAdapter(arrayAdapter);


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

