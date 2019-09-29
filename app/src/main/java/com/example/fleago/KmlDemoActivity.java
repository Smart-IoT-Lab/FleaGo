package com.example.fleago;

import android.util.Log;

import com.example.fleago.model.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import androidx.annotation.NonNull;

public class KmlDemoActivity extends BaseDemoActivity {

    private GoogleMap mMap;
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
        float e = (float) (Math.pow(2, 13 - zoom_lv) * 0.04);
        float e2 = (float) (Math.pow(2, 13 - zoom_lv) * 0.03);
        float sq_left = (float) (126.785582 + e2);
        float sq_right = (float) (127.183786 - e2);
        float sq_up = (float) (37.701397 - e);
        float sq_down = (float) (37.428402 + e);
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
    public void startDemo() {
        try {
            mMap = getMap();
            //set map zoomIn/Out limitation
            mMap.setMinZoomPreference(10);
            mMap.setMaxZoomPreference(13);
            retrieveFileFromResource();
            //retrieveFileFromUrl();
            addItems(ref);


            mClusterManager = new ClusterManager<>(this, mMap);
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
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
                    mMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster_center, 11));
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
                    mMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(item_center, 12));
                    return true;
                }
            });

            seoul_bounds = new LatLngBounds(new LatLng(sq_h_center - 0.00001, sq_w_center - 0.00001), new LatLng(sq_h_center + 0.00001, sq_w_center + 0.00001));
            mMap.setLatLngBoundsForCameraTarget(seoul_bounds);

            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {

                    float zoom_lv = mMap.getCameraPosition().zoom;
                    Log.e("Zoom_level: ", String.valueOf(zoom_lv));
                    if (zoom_lv <= 13) {
                        seoul_bounds = get_bounds(zoom_lv);
                        mMap.setLatLngBoundsForCameraTarget(seoul_bounds);
                        Log.e("Zoom_e: ", String.valueOf(seoul_bounds.toString()));
                    }
                }
            });

            mMap.setLatLngBoundsForCameraTarget(seoul_bounds);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul_center, 10));


        } catch (Exception e) {
            Log.e("Exception caught", e.toString());
        }
    }


    private void addItems(DatabaseReference rf) {
        rf.child("금토일 공예상점*6").child("gps").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dblat = dataSnapshot.getValue(float.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        rf.child("금토일 공예상점*6").child("gps").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dblng = dataSnapshot.getValue(float.class);
                Log.d("Test13", String.valueOf(dblat)+", "+ String.valueOf(dblng));
                marker = new LatLng(dblat, dblng);

                double lat = marker.latitude;
                double lng = marker.longitude;
                String title = "marker_";
                //offset for example
                for (int i = 0; i < 15; i++) {
                    double offset = 1 / 600d;
                    lat = (float) (lat - offset);
                    lng = (float) (lng + offset * (-1 ^ (i + 1)));
                    MyItem offsetItem = new MyItem(lat, lng, title);
                    mClusterManager.addItem(offsetItem);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void retrieveFileFromResource() {
        try {
            KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.taker_fusiontable_seoul, getApplicationContext());
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

    /*
    private void retrieveFileFromUrl() {
        new DownloadKmlFile(getString(R.string.kml_url)).execute();
    }
     */
    private void moveCameraToKml(KmlLayer kmlLayer) {
        //Retrieve the first container in the KML layer
        KmlContainer container = kmlLayer.getContainers().iterator().next();
        //Retrieve a nested container within the first container
        container = container.getContainers().iterator().next();
        //Retrieve the first placemark in the nested container
        KmlPlacemark placemark = container.getPlacemarks().iterator().next();
        //Retrieve a polygon object in a placemark
        KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();
        //Create LatLngBounds of the outer coordinates of the polygon
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polygon.getOuterBoundaryCoordinates()) {
            builder.include(latLng);
        }

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, 1));
    }

    /*
    private class DownloadKmlFile extends AsyncTask<String, Void, byte[]> {
        private final String mUrl;

        public DownloadKmlFile(String url) {
            mUrl = url;
        }

        protected byte[] doInBackground(String... params) {
            try {
                InputStream is =  new URL(mUrl).openStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(byte[] byteArr) {
            try {
                KmlLayer kmlLayer = new KmlLayer(mMap, new ByteArrayInputStream(byteArr),
                        getApplicationContext());
                kmlLayer.addLayerToMap();
                kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        Toast.makeText(KmlDemoActivity.this,
                                "Feature clicked: " + feature.getId(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                moveCameraToKml(kmlLayer);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
     */


}
