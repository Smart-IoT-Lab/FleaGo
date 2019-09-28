package com.example.fleago;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.MarkerOptions;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    private ClusterManager<MyItem> mClusterManager;

    float dblat= (float) 37.543333;
    float dblng= (float) 126.981111;
    LatLng marker = new LatLng(dblat, dblng);
    LatLngBounds seoul_bounds;

    final float sq_w_center = (float) ((126.785582 + 127.183786) / 2);
    final float sq_h_center = (float) ((37.701397 + 37.428402) / 2);


    protected int getLayoutId() {
        return R.layout.kml_demo;
    }

    //DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("10월");


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
            KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.taker_fusiontable_seoul, this.getContext());
            kmlLayer.addLayerToMap();

            Log.d("dda", kmlLayer.getContainers().toString());

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
            public void onMapReady(final GoogleMap gmMap) {

                gmMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gmMap.setMinZoomPreference(10);
                gmMap.setMaxZoomPreference(13);

                retrieveFileFromResource(gmMap);

                gmMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.543545, 126.981061))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                gmMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10, null);

                gmMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.543333,126.981111))
                        .title("서울 중앙인데요")
                        .snippet("I'm center marker"));

                gmMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.443545, 126.981061))
                        .title("경기돈데요")
                        .snippet("서울인줄 알았죠?"));

                mClusterManager = new ClusterManager<>(getContext(), gmMap);
                gmMap.setOnCameraIdleListener(mClusterManager);
                gmMap.setOnMarkerClickListener(mClusterManager);
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
                        seoul_bounds = get_bounds(11);
                        gmMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                        gmMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster_center, 11));
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
                        seoul_bounds = get_bounds(12);
                        gmMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                        gmMap.animateCamera(CameraUpdateFactory.newLatLngZoom(item_center, 12));
                        return true;
                    }
                });

                seoul_bounds = new LatLngBounds(new LatLng(sq_h_center - 0.00001, sq_w_center - 0.00001), new LatLng(sq_h_center + 0.00001, sq_w_center + 0.00001));
                gmMap.setLatLngBoundsForCameraTarget(seoul_bounds);

                gmMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {

                        float zoom_lv = gmMap.getCameraPosition().zoom;
                        Log.e("Zoom_level: ", String.valueOf(zoom_lv));
                        if (zoom_lv <= 13) {
                            seoul_bounds = get_bounds(zoom_lv);
                            gmMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                            Log.e("Zoom_e: ", String.valueOf(seoul_bounds.toString()));
                        }
                    }
                });

                gmMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                gmMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul_center, 10));
            }
        });


        return rootView;
    }



    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}