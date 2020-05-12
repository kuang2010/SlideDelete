package com.kuang2010.slidedelete;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mLv_activity;
    private List<String> datas = new ArrayList<>();
    public HashMap<Integer,Boolean> showDelete = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLv_activity = findViewById(R.id.lv_activity);

        for (int i=0;i<20;i++){

            datas.add("andy"+i);

            showDelete.put(i,false);
        }

        mLv_activity.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHold viewHold = null;
            if (convertView == null){
                convertView = View.inflate(MainActivity.this,R.layout.item_list,null);
                viewHold = new ViewHold();
                convertView.setTag(viewHold);

                viewHold.mSlideDeleteView = convertView.findViewById(R.id.sdv_item);
                viewHold.tv_name = convertView.findViewById(R.id.tv_name);
                viewHold.tv_delete = convertView.findViewById(R.id.tv_delete);

            }else {
                viewHold = (ViewHold) convertView.getTag();
            }

            String s = datas.get(position);

            viewHold.tv_name.setText(s);

            final boolean show = showDelete.get(position);
            if (show){
                viewHold.mSlideDeleteView.showDeleteChild();
            }else {
                viewHold.mSlideDeleteView.hideDeleteVChild();
            }

            viewHold.mSlideDeleteView.setOnDragViewlistener(new SlideDeleteView.OnDragViewlistener() {
                @Override
                public void onDrag(SlideDeleteView view) {
                    Log.d("tagtag","position:"+position);
                    for (int i=0;i<datas.size();i++){
                        if (position == i){
                            showDelete.put(i,true);
                        }else {
                            showDelete.put(i,false);
                        }
                    }
                    notifyDataSetChanged();

                }
            });

            return convertView;
        }
    }

    class ViewHold{
        SlideDeleteView mSlideDeleteView;
        TextView tv_name;
        TextView tv_delete;
    }
}
