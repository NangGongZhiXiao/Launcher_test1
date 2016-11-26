package com.example.ljh.launcher_test1;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by 知晓 on 2016/11/9.
 */
public class AppsFragment extends Fragment {

    private List<ResolveInfo> mApps;
    private MyGridView mGrid;
    private Context context;

    /**
     * 重写点击item的监听
     */
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            ResolveInfo info = mApps.get(position);

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
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(mGrid.getDeletePosition() != -1){
                ResolveInfo info = mApps.get(mGrid.getDeletePosition());

                //该应用的包名
                String pkg = info.activityInfo.packageName;

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + pkg));
                startActivity(intent);
                mGrid.setDeletePosition(-1);
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View newsLayout = inflater.inflate(R.layout.apps, container, false);
        context = newsLayout.getContext();

        loadApps();
        mGrid = (MyGridView) newsLayout.findViewById(R.id.apps_list);
        mGrid.setAdapter(new AppsAdapter(context, mApps));
        mGrid.setOnItemClickListener(listener);
        mGrid.setOnTouchListener(touchListener);

        return newsLayout;
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
     * 加载所有的app
     */
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
    }

}
