package com.example.fleago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.util.Attributes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import adapter.ListViewAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarketListView";

    private SlidingUpPanelLayout mLayout;
    private ListViewAdapter mAdapter;
    private ArrayList<Market> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Market");

        list = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if(list.add(d.getValue(Market.class))) {
                        // 리스트에 market이 추가되었을 때,
                        // Log.d("TEST firebase list add", "return true");
                    }
                }

                /******************* Sliding up List View *******************/
                mLayout = findViewById(R.id.sliding_layout);

                final ListView lv = (ListView) findViewById(R.id.marketList);
                Log.d("TEST market list size", "value : " + list.size());

                mAdapter = new ListViewAdapter(MainActivity.this, list);
                lv.setAdapter(mAdapter);
                mAdapter.setMode(Attributes.Mode.Single);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    // 클릭 시 돋보기 스와이프 되는 view
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(view.getContext(), "lv..setOnItemClick", Toast.LENGTH_SHORT).show();
                        ((com.daimajia.swipe.SwipeLayout) (lv.getChildAt(position - lv.getFirstVisiblePosition()))).open(true);

                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                });

                // 바깥 눌렀을 때, 반응
                mLayout.setFadeOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }

                });

                /******************* Sliding up List View END *******************/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

