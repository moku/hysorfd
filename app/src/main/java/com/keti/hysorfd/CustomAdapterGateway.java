package com.keti.hysorfd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomAdapterGateway extends RecyclerView.Adapter<CustomAdapterGateway.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<String> mGatewayList;
    private HashMap<String,ArrayList<DeviceInfo>> mDeviceList = new HashMap<String,ArrayList<DeviceInfo>>();
    private Context mContext;
    private RequestQueue mQueue;
    private HashMap<String,CustomAdapterDevice> mCustomAdapterDevices = new HashMap<String, CustomAdapterDevice>();

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final RecyclerView recyclerView;
        private final ImageButton imgButton;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.gateway_id);
            recyclerView = (RecyclerView) v.findViewById(R.id.device_list);
            imgButton = (ImageButton) v.findViewById(R.id.show_device_list_button);
        }

        public TextView getTextView() {
            return textView;
        }

        public RecyclerView getRecyclerView(){
            return recyclerView;
        }

        public ImageButton getImageButton() {return imgButton;}
    }
    /**
     * Initialize the dataset of the Adapter.
     *
     */
    public CustomAdapterGateway(ArrayList<String> gatewayList, Context context, RequestQueue queue) {
        mGatewayList = gatewayList;
        mQueue = queue;
        mContext = context;

        System.out.println(mGatewayList.size());
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_gateway, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
       System.out.println("Gateway " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        String gatewayId = mGatewayList.get(position);
        viewHolder.getTextView().setText(gatewayId);

        mDeviceList.put(gatewayId, new ArrayList<DeviceInfo>());

        viewHolder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest deviceListRequest = new StringRequest(Request.Method.GET, "http://203.253.128.161:7579/Mobius/hysorfd/gateways/" + gatewayId + "?fu=1&ty=3",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONObject(response).getJSONArray("m2m:uril");
                                    for(int i=0; i<jsonArray.length(); i++) {
                                        String item = jsonArray.getString(i);
                                        String[] str_list = item.split("/");
                                        String device_id = str_list[4];

                                        StringRequest deviceInfoRequest = new StringRequest(Request.Method.GET, "http://203.253.128.161:7579/Mobius/hysorfd/gateways/" + gatewayId + "/" + device_id + "/la",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonArray = new JSONObject(response).getJSONObject("m2m:cin").getJSONObject("con");
                                                            String battery = jsonArray.getString("battery");
                                                            String temperature = jsonArray.getString("temp");
                                                            String rssi = jsonArray.getString("rssi");
                                                            mDeviceList.get(gatewayId).add(new DeviceInfo(device_id, battery, temperature, rssi));
                                                            mCustomAdapterDevices.get(gatewayId).notifyDataSetChanged();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                System.out.println(error.toString());
                                            }
                                        }){
                                            @Override
                                            public Map<String, String> getHeaders() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("X-M2M-RI", "12345");
                                                params.put("X-M2M-Origin", "Shysorfd");
                                                params.put("Accept", "application/json");

                                                return params;
                                            }
                                        };
                                        mQueue.add(deviceInfoRequest);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-M2M-RI", "12345");
                        params.put("X-M2M-Origin", "Shysorfd");
                        params.put("Accept", "application/json");

                        return params;
                    }
                };
                mQueue.add(deviceListRequest);
            }
        });

        mCustomAdapterDevices.put(gatewayId, new CustomAdapterDevice(gatewayId, mDeviceList.get((gatewayId))));
        viewHolder.getRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        viewHolder.getRecyclerView().setAdapter(mCustomAdapterDevices.get(gatewayId));

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mGatewayList.size();
    }
}
