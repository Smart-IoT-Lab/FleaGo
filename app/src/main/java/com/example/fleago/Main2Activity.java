package com.example.fleago;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.w3c.dom.Text;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private Intent intent1;
    private Intent intent2;
    private Intent intent3;
    ViewFlipper v_flipper;

//    long now = System.currentTimeMillis();
//    Date date= new Date(now);
//    SimpleDateFormat sdf= new SimpleDateFormat("M월");
//    String formatDate = sdf.format(date);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        int images[]={R.drawable.blur_img,R.drawable.img1,R.drawable.ic_info};
        v_flipper=findViewById(R.id.v_flipper);

        for(int image:images){
            flipperImages(image);
        }

        intent1 = getIntent();
        intent2 = getIntent();
        intent3 = getIntent();


        List<String> gps=(List<String>)intent1.getSerializableExtra("gps");

        double gps1 = Double.parseDouble(gps.get(0));
        double gps2 = Double.parseDouble(gps.get(1));

        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(gps2, gps1);//중심점
        mapView.setMapCenterPoint(mapPoint, true);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gps2,gps1), true);//중심점변경
        mapView.setZoomLevel(7, true);//줌레벨
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(gps2,gps1), 6, true);//중심점, 줌레벨
        mapView.zoomIn(true);//줌인
        mapView.zoomOut(true);//줌아웃
        //마커
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("market");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);


        TextView textView = (TextView) findViewById(R.id.textView);//name
        TextView textView3 = (TextView) findViewById(R.id.textView3);//discription
        TextView textView4 = (TextView) findViewById(R.id.textView4);//url
        TextView textView5 = (TextView) findViewById(R.id.textView5);//start_location
        LinearLayout linearlayout1 = (LinearLayout)findViewById(R.id.linearlayout1);//event_type
        TextView textView6 = (TextView) findViewById(R.id.textView6);//start_date
//        TextView textView7 = (TextView) findViewById(R.id.textView7);//day
//        TextView textView8 = (TextView) findViewById(R.id.textView8);//end_Date
//        TextView textView9 = (TextView) findViewById(R.id.textView9);//end_time
//        TextView textView10 = (TextView) findViewById(R.id.textView10);//month
        TextView textView11 = (TextView) findViewById(R.id.textView11);//start_time
        TextView textView12 = (TextView) findViewById(R.id.textView12);//월 test

        Button button = (Button) findViewById(R.id.button);
        com.google.android.material.floatingactionbutton.FloatingActionButton button2 = (com.google.android.material.floatingactionbutton.FloatingActionButton)findViewById(R.id.button2);

        textView.setText(intent1.getStringExtra("name"));
        textView3.setText(intent1.getStringExtra("discription"));
        textView5.setText(intent1.getStringExtra("start_location"));
        textView6.setText(intent1.getStringExtra("start_date"));

        textView11.setText(intent1.getStringExtra("start_time"));
      //  textView12.setText(formatDate);

/*event_type 출력
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
*/

        //textView4.setText(intent1.getStringExtra("page_url"));

      //  button2 = findViewById(R.id.button2); /*페이지 전환버튼*/
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ARActivity.class);
                startActivity(intent);//액티비티 띄우기
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

        String location= textView5.getText().toString();


        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("위치복사", location);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplication(), "위치가 복사되었습니다.",Toast.LENGTH_LONG).show();


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

    // Search bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu) ;

        return true ;
    }
    // Search bar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_serach :
                // TODO : process the click event for action_search item.
                return true ;
            // ...
            // ...
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }



}
