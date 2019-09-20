package com.example.fleago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        intent1 = getIntent();
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        TextView textView5 = (TextView) findViewById(R.id.textView5);
        LinearLayout linearlayout1 = (LinearLayout)findViewById(R.id.linearlayout1);


        textView.setText(intent1.getStringExtra("name"));
        textView2.setText(intent1.getStringExtra("district"));
        textView3.setText(intent1.getStringExtra("introduction"));
       // textView4.setText(intent1.getStringExtra("event_type"));

//        String array[]=intent1.getExtras().getStringArray("array");
//        String add_array="";
//        for(int i=0;i<array.length;i++){
//            add_array+=array[i]+", ";
//        }
//        textView4.setText(add_array);

        List<String> event_type=(List<String>)intent1.getSerializableExtra("event_type");
        linearlayout1.removeAllViews();
        for(int i=0;i<event_type.size();i++){
            TextView textView01 = new TextView(getApplicationContext());
            textView01.setText(event_type.get(i));  //배열리스트 이용
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            linearlayout1.addView(textView01, params);  //linearLayout01 위에 생성

        }

        textView5.setText(intent1.getStringExtra("location"));
        textView4.setText(intent1.getStringExtra("page_url"));


    }
}
