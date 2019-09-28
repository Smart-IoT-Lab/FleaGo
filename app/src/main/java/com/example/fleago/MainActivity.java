package com.example.fleago;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.Collections;
import java.util.Comparator;

import adapter.ListViewAdapter;

import static com.example.fleago.ARActivity.REQUEST_LOCATION_PERMISSIONS_CODE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarketListView";

    private SlidingUpPanelLayout mLayout;
    private ListViewAdapter mAdapter;
    private ArrayList<Markets> list;

    long now = System.currentTimeMillis();
    Date date= new Date(now);
    SimpleDateFormat sdf= new SimpleDateFormat("M");
    String formatDate = sdf.format(date);
    int month = Integer.parseInt(formatDate)+1;
    String nMonth=String.valueOf(month);

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

        // TODO 맨 처음에 GPS 허가 받는데, 받지 않으면 currentLocation이 null이므로 에러가 떠서 종료됨.
        requestLocationPermission();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(formatDate + "월");
        final DatabaseReference myRef2 = database.getReference(nMonth + "월");

        // 임시 test. 거리순 정렬 보려고 gps 칼럼이 있는 10월로 테스트
//        final DatabaseReference myRef = database.getReference("10월");

        list = new ArrayList<>();

        ValueEventListener evl = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Firebase 데이터 load
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    int distance;
                    Markets tmp = d.getValue(Markets.class);

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
                        ((SwipeLayout) (lv.getChildAt(position - lv.getFirstVisiblePosition()))).open(true);

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
        };

        // 현재 달 Firebase reference
        myRef.addValueEventListener(evl);
        // 다음 달 Firebase reference
        myRef2.addValueEventListener(evl);
    }
    // Menu method
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu) ;

        // Searchview 길이 max로
        SearchView searchView = (SearchView)menu.findItem(R.id.action_serach).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Hint
        searchView.setQueryHint("ex) 뚝섬 ");

        //리스너 구현
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    // Search bar method
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_serach :
                Log.d("search bar","qqqqqqqqqqq");
                return true ;
            // ...
            // ...
            default :
                return super.onOptionsItemSelected(item) ;
        }
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
                ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
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
        Collections.sort(list, new Comparator<Markets>() {
            @Override
            public int compare(Markets m1, Markets m2) {
                if (m1.getDistance() < m2.getDistance()) {
                    return -1;
                } else if (m1.getDistance() > m2.getDistance()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    // 권한 허가 요청에 대한 응답 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    // 권한 요청이 거부되었으므로 프로그램 종료

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}

