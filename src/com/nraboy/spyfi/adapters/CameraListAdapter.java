package com.nraboy.spyfi;

/*
 * Spyfi
 * Created by Nic Raboy
 * www.nraboy.com
 */

import com.nraboy.spyfi.objects.*;
import java.util.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.util.*;

public class CameraListAdapter extends BaseAdapter {

    private static ArrayList<Camera> cameraList;
    private LayoutInflater mInflater;
    private Context context;

    public CameraListAdapter(Context context, ArrayList<Camera> results) {
        this.cameraList = results;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return this.cameraList.size();
    }

    public Object getItem(int position) {
        return this.cameraList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvTitle;
        TextView tvHost;
        try {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listrow_camera, null);
            }
            tvTitle = (TextView) convertView.findViewById(R.id.title);
            tvHost = (TextView) convertView.findViewById(R.id.host);
            tvTitle.setText(this.cameraList.get(position).getTitle());
            tvHost.setText(this.cameraList.get(position).getHost());
        } catch (Exception e) {
            Log.d(this.context.getResources().getString(R.string.app_name), e.getMessage(), e);
        }
        return convertView;
    }

}