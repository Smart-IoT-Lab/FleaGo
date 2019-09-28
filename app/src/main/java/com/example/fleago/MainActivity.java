package com.example.fleago;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import java.util.Collections;
import java.util.Comparator;

import adapter.ListViewAdapter;

import static com.example.fleago.ARActivity.REQUEST_LOCATION_PERMISSIONS_CODE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarketListView";

    private SlidingUpPanelLayout mLayout;
    private ListViewAdapter mAdapter;
    private ArrayList<Market> list;

    private Location currentLocation;
    private LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 100;//1000 * 60 * 1; // 1 minute
    private static final int MAX_DISTANCE = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Market");
        // 임시 test. 거리순 정렬 보려고 gps 칼럼이 있는 10월로 테스트
//        final DatabaseReference myRef = database.getReference("10월");

        list = new ArrayList<>();

        requestLocationPermission();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Firebase 데이터 load
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    int distance;
                    Market tmp = d.getValue(Market.class);

                    // 각 Market 까지의 거리를 설정
                    if (tmp.hasGps()) {
                        Location target = new Location("Firebase");
                        ArrayList<String> gps = tmp.getGps();
                        target.setLongitude(Double.parseDouble(gps.get(0)));    // 경도
                        target.setLatitude(Double.parseDouble(gps.get(1)));     // 위도

                        distance = (int) currentLocation.distanceTo(target);    // 소수점 버림
                        tmp.setDistance(distance);
                    } else {
                        // GPS 정보가 없는 경우, 가까운 순으로 정렬을 위해 최대값으로 설정.
                        distance = MAX_DISTANCE;
                        tmp.setDistance(distance);
                    }

                    // TODO 현재 날짜를 기준으로 2주동안의 시장만 추가하기.

                    list.add(tmp);
                }

                // 거리순 정렬
                sortToDistance();

                // Sliding Up List View
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d("TEST", "onLocationChanged() : " + location);

            currentLocation.setLatitude(location.getLatitude());
            currentLocation.setLongitude(location.getLongitude());
            currentLocation.setAltitude(location.getAltitude());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("TEST", "onStatusChanged() : " + status);
        }

        public void onProviderEnabled(String provider) {
            Log.d("TEST", "onProviderEnabled() : " + provider);
        }

        public void onProviderDisabled(String provider) {
            Log.d("TEST", "onProviderDisabled() : " + provider);
        }
    };

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
    }

    private void initLocationService() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                Log.d("TEST provider", "network");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        gpsLocationListener);
                if (locationManager != null)   {
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    updateLatestLocation();
                }
            }

            if (isGPSEnabled)  {
                Log.d("TEST provider", "gps");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        gpsLocationListener);

                if (locationManager != null)  {
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    updateLatestLocation();
                }
            }

            Log.d("TEST initService() end", "current location : " + currentLocation);

        } catch (Exception ex)  {
            Log.e("initLocationService()", ex.getMessage());
        }
    }

    void sortToDistance() {
        // distance가 낮은 순으로 정렬
        Collections.sort(list, new Comparator<Market>() {
            @Override
            public int compare(Market m1, Market m2) {
                if (m1.getDistance() < m2.getDistance()) {
                    return -1;
                } else if (m1.getDistance() > m2.getDistance()) {
                    return 1;
                }
                return 0;
            }
        });
    }

}

