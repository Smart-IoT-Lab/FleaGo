package adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import com.example.fleago.Main2Activity;
import com.example.fleago.Market;
import com.example.fleago.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.fleago.ARActivity.REQUEST_LOCATION_PERMISSIONS_CODE;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private ArrayList<Market> list;

    //firebaseStorage 인스턴스 생성
    //하나의 Storage와 연동되어 있는 경우, getInstance()의 파라미터는 공백으로 두어도 됨
    //하나의 앱이 두개 이상의 Storage와 연동이 되어있 경우, 원하는 저장소의 스킴을 입력
    //getInstance()의 파라미터는 firebase console에서 확인 가능('gs:// ... ')
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //생성된 FirebaseStorage를 참조하는 storage 생성
    StorageReference storageRef = storage.getReference();
    //Storage 내부의 images 폴더 안의 image.jpg 파일명을 가리키는 참조 생성
    StorageReference pathReference = storageRef.child("/FLEAGO.png");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private String urii;

    private TextView tv_distance;
    private Location currentLocation;
    private LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 100;//1000 * 60 * 1; // 1 minute

    public ListViewAdapter(Context mContext, ArrayList list) {
        this.mContext = mContext;
        this.list = list;
    }

    LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d("TEST", "onLocationChanged() : " + location);

            currentLocation.setLatitude(location.getLatitude());
            currentLocation.setLongitude(location.getLongitude());
            currentLocation.setAltitude(location.getAltitude());
//            updateLatestLocation();
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

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        // 리스트의 아이템 표현하는데 item_view.xml 사용
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view, null);

//       SwipeLayout swipeLayout = view.findViewById(getSwipeLayoutResourceId(position));
        // 더블클릭
//        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
//            @Override
//            public void onDoubleClick(SwipeLayout layout, boolean surface) {
//                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
//            }
//        });

        // 상세정보(돋보기) 클릭 시
        view.findViewById(R.id.magnifier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(mContext, "click magnifier", Toast.LENGTH_SHORT).show();

                Intent intent1 = new Intent(view.getContext(), Main2Activity.class);
                intent1.putExtra("name", list.get(position).getName());
                intent1.putExtra("district", list.get(position).getDistrict());
                intent1.putExtra("event_type", list.get(position).getEvent_type());
                intent1.putExtra("location", list.get(position).getLocation());
                intent1.putExtra("introduction", list.get(position).getIntroduction());
                intent1.putExtra("page_url", list.get(position).getPage_url());
                mContext.startActivity(intent1);
            }
        });

        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {
        // 그 position의 item 에다가 데이터 채우기

        // 이미지 출력
        ImageView image = (ImageView) convertView.findViewById(R.id.marketImage);
        setImage(image);

        // 제목 출력
        ((TextView) convertView.findViewById(R.id.text_data)).setText(list.get(position).getName());

        // 카테고리 출력
        ArrayList<String> events = list.get(position).getEvent_type();
        String eventsToString = "";
        for (String s : events) {
            eventsToString = eventsToString.concat("#" + s + " ");
        }
        ((TextView) convertView.findViewById(R.id.tv_eventType)).setText(eventsToString);

        // TODO 운영시간(OpeningHour). 현재 no data
        // ((TextView) convertView.findViewById(R.id.tv_openingHour)).setText(list.get(position).getOpeningHour());

        // 거리 출력
        tv_distance = convertView.findViewById(R.id.tv_distance);
        requestLocationPermission();

        // Demo gps  127.05700576, 37.54162688 마르쉐 채소시장@성수
        Location target = new Location("Demo GPS");
        target.setLongitude(127.05700576); // 경도
        target.setLatitude(37.54162688);   // 위도
        double distance;

        distance = currentLocation.distanceTo(target);
        tv_distance.setText(String.format("%,d", (int)distance) + "m"); // 소수점 버림
        Log.d("TEST", "distance : " + distance);

        // TODO Market class의 수정 필요. 위도, 경도를 가져올 수 있어야 함.
//        if(list.get(position).hasGps()) {
//            Location target = new Location("Firebase");
//            target.setLongitude(list.get(position).getLongitude()); // 경도
//            target.setLatitude(list.get(position).getLatitude());   // 위도

//            distance = currentLocation.distanceTo(target);
//            tv_distance.setText(String.format("%,d", (int)distance) + "m"); // 소수점 버림
//            Log.d("TEST", "distance : " + distance);
//        } else {
//            // GPS 정보가 없는 경우
//            tv_distance.setText("no data");
//        }

        Log.d("TEST", "distance : " + distance);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setImage(final ImageView image) {
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urii = uri.toString();
//                Log.d("uri", urii);
                Picasso.with(
                        mContext).
                        load(urii).
                        fit().
                        centerInside().
                        into(image);
            }
        });
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((Activity)mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
    }

    private void initLocationService() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try {
            this.locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

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

//    void sortToDistance() {
//        // distance가 낮은 순으로 정렬
//        Collections.sort(list, new Comparator<Market>() {
//            @Override
//            public int compare(Market m1, Market m2) {
//                if (m1.getDistance() < m2.getDistance()) {
//                    return -1;
//                } else if (m1.getDistance() > m2.getDistance()) {
//                    return 1;
//                }
//                return 0;
//            }
//        });
//    }

//    private void updateLatestLocation() {
//        Log.d("TEST", "current location in updateLatestLocation() : " + currentLocation);
//        double distance;
//        if (currentLocation != null) {
//            // Demo gps 37.552398, 126.862294600991
//            Location locationB = new Location("hard coding");
//            locationB.setLatitude(37.552398);
//            locationB.setLongitude(126.862294600991);
//
//            distance = currentLocation.distanceTo(locationB);
//            tv_distance.setText(String.format("%,d", (int)distance) + "m");
//            Log.d("TEST", "distance : " + distance);
//        }
//    }
}
