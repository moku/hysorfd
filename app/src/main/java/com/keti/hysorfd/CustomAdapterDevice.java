package com.keti.hysorfd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapterDevice extends RecyclerView.Adapter<CustomAdapterDevice.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<DeviceInfo> mDeviceList;
    private String mGatewayId;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTextView;
        private final TextView batteryTextView;
        private final TextView temperatureTextView;
        private final TextView rssiTextView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Element " + getAdapterPosition() + " clicked.");
                }
            });
            idTextView = (TextView) v.findViewById(R.id.device_id);
            batteryTextView = (TextView) v.findViewById(R.id.device_battery);
            temperatureTextView = (TextView) v.findViewById(R.id.device_temp);
            rssiTextView = (TextView) v.findViewById(R.id.device_rssi);
        }

        public TextView getIdTextView() {
            return idTextView;
        }
        public TextView getBatteryTextView() {
            return batteryTextView;
        }
        public TextView getTemperatureTextView() {
            return temperatureTextView;
        }
        public TextView getRssiTextView() {
            return rssiTextView;
        }
    }
    /**
     * Initialize the dataset of the Adapter.
     *
     */
    public CustomAdapterDevice(String gatewayId, ArrayList<DeviceInfo> deviceList) {
        mDeviceList = deviceList;
        mGatewayId = gatewayId;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_device, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
       System.out.println("Device " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        DeviceInfo info = mDeviceList.get(position);
        viewHolder.getIdTextView().setText(info.mId);
        viewHolder.getBatteryTextView().setText(info.mBattery);
        viewHolder.getTemperatureTextView().setText(info.mTemperature);
        viewHolder.getRssiTextView().setText(info.mRssi);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }
}
