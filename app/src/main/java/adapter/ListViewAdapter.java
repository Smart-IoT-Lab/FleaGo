package adapter;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;


import com.example.fleago.Main2Activity;
import com.example.fleago.Market;
import com.example.fleago.Markets;
import com.example.fleago.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListViewAdapter extends BaseSwipeAdapter implements Filterable {

    private Context mContext;

    private ArrayList<Markets> list;
    private ArrayList<Markets> list2;


    //firebaseStorage 인스턴스 생성
    //하나의 Storage와 연동되어 있는 경우, getInstance()의 파라미터는 공백으로 두어도 됨
    //하나의 앱이 두개 이상의 Storage와 연동이 되어있 경우, 원하는 저장소의 스킴을 입력
    //getInstance()의 파라미터는 firebase console에서 확인 가능('gs:// ... ')
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://fleago-8b03c.appspot.com");
    //생성된 FirebaseStorage를 참조하는 storage 생성
    StorageReference storageRef1 = storage.getReference("image/9월/");
    StorageReference storageRef2 = storage.getReference("image/10월/");
    StorageReference pathReference;
    //Storage 내부의 images 폴더 안의 image.jpg 파일명을 가리키는 참조 생성
    //StorageReference pathReference = storageRef.child("/FLEAGO.png");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private String urii;

    public ListViewAdapter(Context mContext, ArrayList list) {
        this.mContext = mContext;
        this.list = list;
        list2 = new ArrayList<Markets>();
        list2.addAll(list);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {

//        SwipeLayout swipeLayout = view.findViewById(getSwipeLayoutResourceId(position));
        // 더블클릭
//        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
//            @Override
//            public void onDoubleClick(SwipeLayout layout, boolean surface) {
//                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
//            }
//        });

        // 리스트의 아이템 표현하는데 item_view.xml 사용
        return LayoutInflater.from(mContext).inflate(R.layout.item_view, null);

    }

    @Override
    public void fillValues(final int position, View convertView) {
        // 그 position의 item 에다가 데이터 채우기

        // 이미지 출력
        ImageView image = (ImageView) convertView.findViewById(R.id.marketImage);
        setImage(image, position);

        // 제목 출력
        ((TextView) convertView.findViewById(R.id.text_data)).setText(list.get(position).getName());

        // 카테고리 출력
        String eventsToString = "";
        ArrayList<String> events = list.get(position).getEvent_type();
        if (events == null || events.size() == 0) {
            // exception: 카테고리 없음
        } else {
            for (String s : events) {
                eventsToString = eventsToString.concat("#" + s + " ");
            }
        }
        ((TextView) convertView.findViewById(R.id.tv_eventType)).setText(eventsToString);

        // 운영시간(OpeningHour) 출력
        String start = list.get(position).getStart_time();
        String end = list.get(position).getEnd_time();
        if (list.get(position).hasTime()) {
            ((TextView) convertView.findViewById(R.id.tv_openingHour)).setText(start + " ~ " + end);


        } else {
            ((TextView) convertView.findViewById(R.id.tv_openingHour)).setText("no data");
        }

        // 거리 출력
        int distance = list.get(position).getDistance();
        TextView tv_distance = convertView.findViewById(R.id.tv_distance);
        if (distance == Integer.MAX_VALUE || distance < 0) {
            tv_distance.setText("no data");
        } else {
            if (distance/1000.0 > 1.0)
                tv_distance.setText(String.format("%.2f ", distance/1000.0) + "km");
            else
                tv_distance.setText(String.format("%d ", distance) + "m");
        }

        // 상세정보(돋보기) 클릭 시
        convertView.findViewById(R.id.magnifier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(mContext, "click magnifier", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(view.getContext(), Main2Activity.class);
//                intent1.putExtra("name", list.get(position).getName());
//                intent1.putExtra("district", list.get(position).getDistrict());
//                intent1.putExtra("event_type", list.get(position).getEvent_type());
//                intent1.putExtra("location", list.get(position).getLocation());
//                intent1.putExtra("introduction", list.get(position).getIntroduction());
//                intent1.putExtra("page_url", list.get(position).getPage_url());
                intent1.putExtra("day", list.get(position).getDay());
                intent1.putExtra("discription", list.get(position).getDiscription());
                intent1.putExtra("end_date", list.get(position).getEnd_date());
                intent1.putExtra("end_time", list.get(position).getEnd_time());
                intent1.putExtra("gps", list.get(position).getGps());
                intent1.putExtra("month", list.get(position).getMonth());
                intent1.putExtra("name", list.get(position).getName());
                intent1.putExtra("start_date", list.get(position).getStart_date());
                intent1.putExtra("start_location", list.get(position).getStart_location());
                intent1.putExtra("start_time", list.get(position).getStart_time());
                intent1.putExtra("week", list.get(position).getWeek());
                mContext.startActivity(intent1);

                Log.d("TEST position", String.valueOf(position));
            }
        });

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

    public void setImage(final ImageView image, final int position) {
        pathReference = storageRef1.child(list2.get(position).getName() + "/" + list2.get(position).getName() + "_1.jpg");
        //pathReference = storageRef1.child("(건대입구역)/(건대입구역)_1.jpg");
        if (pathReference == null)
            storageRef2.child(list2.get(position).getName() + "/" + list2.get(position).getName() + "_1.jpg");


        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(
                        mContext).
                        load(uri).
                        fit().
                        centerInside().
                        into(image);
            }
    });

}

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter () {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Markets> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(list2);
            } else {
                for (Markets market: list2) {
                    if (market.getName().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(market);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends Markets>) filterResults.values);
            notifyDataSetChanged();
        }
    };

}
