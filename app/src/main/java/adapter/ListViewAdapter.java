package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import com.example.fleago.Main2Activity;
import com.example.fleago.Market;
import com.example.fleago.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private ArrayList<Market> list;

    public ListViewAdapter(Context mContext, ArrayList list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        // 리스트의 아이템 표현하는데 item_view.xml 사용
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view, null);
        SwipeLayout swipeLayout = view.findViewById(getSwipeLayoutResourceId(position));


        // 더블클릭
//        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
//            @Override
//            public void onDoubleClick(SwipeLayout layout, boolean surface) {
//                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
//            }
//        });

        // 상세정보(돋보기) 클릭 시
        view.findViewById(R.id.magnifier).setOnClickListener(new View.OnClickListener() {
            public void setIntent(View view){
                Intent intent1=new Intent(view.getContext(), Main2Activity.class);
                intent1.putExtra("name", list.get(position).getName());
                intent1.putExtra("district", list.get(position).getDistrict());
                intent1.putExtra("event_type", list.get(position).getEvent_type());
                intent1.putExtra("location", list.get(position).getLocation());
                intent1.putExtra("introduction", list.get(position).getIntroduction());
                intent1.putExtra("page_url", list.get(position).getPage_url());
                mContext.startActivity(intent1);
            }
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click magnifier", Toast.LENGTH_SHORT).show();
                setIntent(view);
            }
        });

        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {
        // 그 item 에다가 데이터 채우기
        TextView t = (TextView)convertView.findViewById(R.id.position);
        t.setText((position + 1) + ".");

//        setmarketlist(myRef);
//        Log.d("list",list.toString());
        if(!(list.isEmpty()))
            ((TextView) convertView.findViewById(R.id.text_data)).setText(list.get(position).getName());
        else
            ((TextView) convertView.findViewById(R.id.text_data)).setText("데이터 없음");
    }





    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
