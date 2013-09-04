package com.nraboy.spyfi;

import java.util.ArrayList;
import android.widget.*;
import android.view.*;
import android.content.Context;

public class CameraListAdapter extends BaseAdapter {

    private static ArrayList<Camera> cameraList;
    private LayoutInflater mInflater;
    private Context context;

    public CameraListAdapter(Context context, ArrayList<Camera> results) {
        cameraList = results;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return cameraList.size();
    }

    public Object getItem(int position) {
        return cameraList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.camerarow, null);
                holder = new ViewHolder();
                holder.txtLabel = (TextView) convertView.findViewById(R.id.camera_label);
                holder.txtHost = (TextView) convertView.findViewById(R.id.camera_host);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtLabel.setText(cameraList.get(position).getLabel());
            holder.txtHost.setText(cameraList.get(position).getHost());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView txtLabel;
        TextView txtHost;
    }
}
