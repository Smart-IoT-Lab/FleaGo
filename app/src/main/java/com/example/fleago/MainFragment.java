package com.example.fleago;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fleago.model.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private ClusterManager<MyItem> mClusterManager;

    float dblat= (float) 37.543333;
    float dblng= (float) 126.981111;
    LatLng marker = new LatLng(dblat, dblng);
    LatLngBounds seoul_bounds;

    final float sq_w_center = (float) ((126.785582 + 127.183786) / 2);
    final float sq_h_center = (float) ((37.701397 + 37.428402) / 2);

    ArrayList<Markets> marketList = new ArrayList<Markets>();

    protected int getLayoutId() {
        return R.layout.kml_demo;
    }

    //DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ChildEventListener mChildEventlistener;
    ChildEventListener mChildEventlistener2;
    DatabaseReference ref = database.getReference("9월");
    DatabaseReference ref2 = database.getReference("10월");

    public LatLngBounds get_bounds(float zoom_lv) {
        float e = (float) (Math.pow(2, 13 - zoom_lv) * 0.03);
        float e2 = (float) (Math.pow(2, 13 - zoom_lv) * 0.03);
        float sq_left = (float) (126.785582 + e2);
        float sq_right = (float) (127.183786 - e2);
        float sq_up = (float) (37.741397 - e);      //+0.04
        float sq_down = (float) (37.388402 + e);    //-0.04
        if (sq_left > sq_right && sq_down > sq_up) {
            sq_left = sq_w_center;
            sq_right = sq_w_center;
            sq_up = sq_h_center;
            sq_down = sq_h_center;
            Log.d("Type1","all");
            return new LatLngBounds(new LatLng(sq_h_center - 0.00001, sq_w_center - 0.00001), new LatLng(sq_h_center + 0.00001, sq_w_center + 0.00001));
        } else {
            if(sq_down > sq_up){
                sq_up = sq_h_center;
                sq_down = sq_h_center;
                Log.d("Type2","height");
                return new LatLngBounds(new LatLng(sq_h_center - 0.00001, sq_left), new LatLng(sq_h_center + 0.00001, sq_right));
            }
            Log.d("Type3","none");
            return new LatLngBounds(new LatLng(sq_down, sq_left), new LatLng(sq_up, sq_right));
        }
    }
    private void retrieveFileFromResource(GoogleMap mMap) {
        try {
            KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.taker_fusiontable_seoul, getActivity().getApplicationContext());
            kmlLayer.addLayerToMap();

            Log.d("fdda", kmlLayer.getContainers().toString());

            //moveCameraToKml(kmlLayer);

            /*
            LatLng seoul_center = new LatLng(37.543545, 126.981061);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul_center, 10));
             */
            // initialize LatLng to seoul's center & move camera to Seoul
            /*
            LatLngBounds seoul_center = new LatLngBounds(
                    new LatLng(33, 121),
                    new LatLng(41, 131)
            );
            */
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void loadFirebase(final DatabaseReference ref){

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Markets markets = snapshot.getValue(Markets.class);
                    Log.d("asdfasdf",markets.getGps().get(0)+","+markets.getGps().get(1));
                    marketList.add(markets);
                    Log.d("asdfasdf1",marketList.get(marketList.size()-1).getGps().get(0)+","+marketList.get(marketList.size()-1).getGps().get(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

            public void onLocationChanged(Location location, GoogleMap mMap)    {

            }
        });

    }




    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    //여기서 뷰가 만들어집니다!!!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap gMap) {

                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMap.setMinZoomPreference(10);
                gMap.setMaxZoomPreference(13);

                gMap.clear(); //clear old markers

                retrieveFileFromResource(gMap);

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.543545, 126.981061))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10, null);
                //Firebase 연동으로 지도 위에 마커 추가하기
                addMarkersToMap(gMap);
                addMarkers2ToMap(gMap);


                /*
                gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.443545, 126.981061))
                        .title("경기돈데요")
                        .snippet("서울인줄 알았죠?"));
                */
                mClusterManager = new ClusterManager<>(getContext(), gMap);
                gMap.setOnCameraIdleListener(mClusterManager);
                gMap.setOnMarkerClickListener(mClusterManager);
                //addItems();
                LatLng seoul_center = new LatLng(37.543545, 126.981061);//   +0.038ed          +0.03                      -0.04           -0.03

                //ClusterManager의 item들인 마커들+클러스터 클릭리스너
                mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>()   {
                    @Override
                    public boolean onClusterClick(Cluster<MyItem> cluster) {
                        LatLng position = cluster.getPosition();
                        LatLng cluster_center = new LatLng(position.latitude, position.longitude);
                        Log.d("cluster","clicked");
                        Log.d("cluster_position_lat", String.valueOf(position.latitude));
                        Log.d("cluster_position_lng", String.valueOf(position.longitude));
                        seoul_bounds = get_bounds(12);
                        gMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster_center, 12));
                        return true;
                    }
                });
                mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        LatLng position = item.getPosition();
                        LatLng item_center = new LatLng(position.latitude, position.longitude);
                        Log.d("cluster item","clicked");
                        Log.d("item_position_lat", String.valueOf(position.latitude));
                        Log.d("item_position_lng", String.valueOf(position.longitude));
                        seoul_bounds = get_bounds(13);
                        gMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(item_center, 13));
                        return true;
                    }
                });
                seoul_bounds = new LatLngBounds(new LatLng(sq_h_center - 0.00001, sq_w_center - 0.00001), new LatLng(sq_h_center + 0.00001, sq_w_center + 0.00001));
                gMap.setLatLngBoundsForCameraTarget(seoul_bounds);

                gMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {

                        float zoom_lv = gMap.getCameraPosition().zoom;
                        Log.e("Zoom_level: ", String.valueOf(zoom_lv));
                        if (zoom_lv <= 13) {
                            seoul_bounds = get_bounds(zoom_lv);
                            gMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                            Log.e("Zoom_e: ", String.valueOf(seoul_bounds.toString()));
                        }
                    }
                });

                gMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul_center, 10));

                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                    @Override
                    public boolean onMarkerClick(Marker marker){
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12), 2, null);
                        return false;
                    }
                });
            }
        });
        return rootView;
    }

    private void addMarkersToMap(final GoogleMap gMap) {
        mChildEventlistener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Markets marker = dataSnapshot.getValue(Markets.class);
                if(!marker.getGps().get(0).equals("N")) {

                    String name = marker.getName();
                    String latitude = marker.getGps().get(0);
                    String longitude = marker.getGps().get(1);
                    double convert_lat = Double.parseDouble(latitude);
                    double convert_lng = Double.parseDouble(longitude);
                    LatLng location = new LatLng(convert_lng, convert_lat);
                    gMap.addMarker(new MarkerOptions().position(location).title(name));
                    Log.d("FB_marker_ADD-Location", String.valueOf(location));
                    Log.d("FB_marker_ADD-name", name);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void addMarkers2ToMap(final GoogleMap gMap) {
        mChildEventlistener2 = ref2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Markets marker = dataSnapshot.getValue(Markets.class);
                String name = marker.getName();
                String latitude = marker.getGps().get(0);
                String longitude = marker.getGps().get(1);
                double convert_lat = Double.parseDouble(latitude);
                double convert_lng = Double.parseDouble(longitude);
                LatLng location = new LatLng(convert_lng, convert_lat);
                gMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
                Log.d("FB_marker_ADD-Location", String.valueOf(location));
                Log.d("FB_marker_ADD-name", name);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return true;
    }
}