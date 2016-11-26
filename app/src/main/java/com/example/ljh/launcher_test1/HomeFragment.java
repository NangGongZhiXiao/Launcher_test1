package com.example.ljh.launcher_test1;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by 知晓 on 2016/11/9.
 */
public class HomeFragment extends Fragment {

    private List<ResolveInfo> mApps;
    private MyGridView mGrid;
    private Context context;
    private ViewPager viewPager;
    private List<View> listView;
    private static int APP_COUNT = 15;
    private int count=0;

    /**
     * 重写点击item的监听
     */
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            ResolveInfo info = ((AppsAdapter)parent.getAdapter()).getApps().get(position);

            //该应用的包名
            String pkg = info.activityInfo.packageName;
            //应用的主activity类
            String cls = info.activityInfo.name;

            ComponentName componet = new ComponentName(pkg, cls);

            Intent i = new Intent();
            i.setComponent(componet);
            startActivity(i);
        }

    };

    /**
     *  重写触摸事件的监听
     */
    private View.OnTouchListener forbidScroll = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (((MyGridView) v).getDeletePosition() != -1) {
                    ResolveInfo info = ((AppsAdapter) ((MyGridView) v).getAdapter()).getApps().get(((MyGridView) v).getDeletePosition());

                    //该应用的包名
                    String pkg = info.activityInfo.packageName;

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + pkg));
                    startActivity(intent);
                    ((MyGridView) v).setDeletePosition(-1);
                    }
                    break;
            }
            return false;
        }
    };
   /* @Nullable
    private View.OnTouchListener forbidScroll = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View newsLayout = inflater.inflate(R.layout.home, container, false);
        context = newsLayout.getContext();
        loadApps();
        viewPager = new ViewPager(context);
        listView = new ArrayList<>();
        viewpagerGetApps(inflater);

        //添加适配器
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return listView.size();
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(listView.get(position));
                return listView.get(position);
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(listView.get(position));
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        new SlideAnimation().setAnimation(viewPager);
//        mGrid = (GridView) newsLayout.findViewById(R.id.apps_list);
//        mGrid.setAdapter(new AppsAdapter(context));
//        mGrid.setOnItemClickListener(listener);
        return viewPager;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     *  将app加载到每一页上
     */
    private void viewpagerGetApps(LayoutInflater inflater) {
        viewPager = new ViewPager(context);
        listView = new ArrayList<>();
        for(int i = 0; i < mApps.size(); i += APP_COUNT){
            List<ResolveInfo> apps = new ArrayList<>();
            MyGridView view = (MyGridView) inflater.inflate(R.layout.home,null);
            for(int j = 0; j < 15 && (j + i) < mApps.size(); j++){
                apps.add(mApps.get(i + j));
            }
            listView.add(view);
            view.setAdapter(new AppsAdapter(context, apps));
            view.setOnTouchListener(forbidScroll);
            view.setOnItemClickListener(listener);
        }
    }


    /**
     * 加载所有的app
     */
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
    }



}
