package com.example.ljh.launcher_test1;

/**
 * Created by 知晓 on 2016/11/16.
 */

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示Apps的adapter
 */
public class AppsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater infalter;
    private List<ResolveInfo> apps;
    public AppsAdapter(Context context, List<ResolveInfo> apps) {
        super();
        this.context = context;
        this.apps = apps;

        infalter = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ResolveInfo info = apps.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = infalter.inflate(R.layout.apps_item, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_appsIcon);
            viewHolder.text = (TextView) convertView.findViewById(R.id.tev_appsIcon);
            convertView.setTag(viewHolder);
            convertView.setLayoutParams(new GridView.LayoutParams(250, 300));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageDrawable(info.activityInfo.loadIcon(context.getPackageManager()));
        viewHolder.text.setText(info.activityInfo.loadLabel(context.getPackageManager()));
        return convertView;
    }

    public final int getCount() {
        return apps.size();
    }

    public final Object getItem(int position) {
        return apps.get(position);
    }

    public final long getItemId(int position) {
        return position;
    }

    public List<ResolveInfo> getApps() {
        return apps;
    }

    /**
     * 移动item的位置
     * @param start 开始item的位置
     * @param end 需要换到的位置
     */
    protected void moveItem(int start, int end) {
        List<ResolveInfo> tempList = new ArrayList<>();
        tempList.clear();
        for(ResolveInfo r : apps) {
            tempList.add(r);
        }
        ResolveInfo startMirror = tempList.get(start);

        if(start < end) {
            for(int i = start; i < end; i++) {
                tempList.remove(i);
                tempList.add(i, (ResolveInfo) getItem(i + 1));
            }
        }
        else {
            for(int i = start; i > end; i--) {
                tempList.remove(i);
                tempList.add(i, (ResolveInfo) getItem(i - 1));
            }
        }
        tempList.remove(end);
        tempList.add(end, startMirror);

        apps.clear();
        apps.addAll(tempList);
        notifyDataSetChanged();
    }

    /**
     *  交换两个item的位置
     * @param start 交换的位置
     * @param end 需要交换的位置
     */
    protected void exchangeItem(int start, int end){
        List<ResolveInfo> tempList = new ArrayList<>();
        tempList.clear();
        tempList.addAll(apps);
        if(start < end) {
            ResolveInfo endMirror = tempList.get(end);
            tempList.remove(end);
            tempList.add(end, tempList.get(start));
            tempList.remove(start);
            tempList.add(start, endMirror);
        }
        else {
            ResolveInfo startMirror = tempList.get(start);
            tempList.remove(start);
            tempList.add(start, tempList.get(end));
            tempList.remove(end);
            tempList.add(end, startMirror);
        }
        apps.clear();
        apps.addAll(tempList);
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView icon;
        TextView text;
    }

}
