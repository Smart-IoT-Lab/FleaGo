package com.example.fleago;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

//import net.daum.mf.map.api.MapPOIItem;
//import net.daum.mf.map.api.MapPoint;
//import net.daum.mf.map.api.MapView;

import net.daum.android.map.MapView;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private Intent intent1;
    private Intent intent2;
    private Intent intent3;
    ViewFlipper v_flipper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        int images[]={R.drawable.blur_img,R.drawable.img1,R.drawable.ic_info};
        v_flipper=findViewById(R.id.v_flipper);

        for(int image:images){
            flipperImages(image);
        }

//        MapView mapView = new MapView(this);
//        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
//        mapViewContainer.addView(mapView);
//        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.541, 126.986);
//        mapView.setMapCenterPoint(mapPoint, true);
//// 중심점 변경
//        //mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.33, 127.3), true);
//// 줌 레벨 변경
//        mapView.setZoomLevel(7, true);
//// 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.541, 126.986), 7, true);
//// 줌 인
//        mapView.zoomIn(true);
//// 줌 아웃
//        mapView.zoomOut(true);
//        MapPOIItem marker = new MapPOIItem();
//
//        marker.setItemName("mar1");
//        marker.setTag(0);
//        marker.setMapPoint(mapPoint);
//        // 기본으로 제공하는 BluePin 마커 모양.
//        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//        mapView.addPOIItem(marker);


        intent1 = getIntent();
        intent2 = getIntent();
        intent3 = getIntent();
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        TextView textView5 = (TextView) findViewById(R.id.textView5);
        LinearLayout linearlayout1 = (LinearLayout)findViewById(R.id.linearlayout1);
        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button)findViewById(R.id.button2);

        textView.setText(intent1.getStringExtra("name"));

        textView3.setText(intent1.getStringExtra("introduction"));

        List<String> event_type=(List<String>)intent1.getSerializableExtra("event_type");
        linearlayout1.removeAllViews();
        for(int i = 0; i < event_type.size(); i++){
            TextView textView01 = new TextView(getApplicationContext());
            textView01.setText(event_type.get(i));  //배열리스트 이용
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            linearlayout1.addView(textView01, params);  //linearLayout01 위에 생성
        }

        textView5.setText(intent1.getStringExtra("location"));
        textView4.setText(intent1.getStringExtra("page_url"));

      //  button2 = findViewById(R.id.button2); /*페이지 전환버튼*/
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ARActivity.class);
                startActivity(intent3);//액티비티 띄우기
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("daummaps://open?page=routeSearch"));
                startActivity(intent2);
            }
        });

    }

    private void flipperImages(int image) {

        ImageView imageView=new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(2000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }


}
