package com.keti.hysorfd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    protected RecyclerView mGatewayRecyclerView;
    protected CustomAdapterGateway mAdapterGateway;
    protected RecyclerView.LayoutManager mGatewayLayoutManager;
    protected ArrayList<String> mGatewayList = new ArrayList<String>();
    protected HashMap<String, ArrayList<DeviceInfo>> mDeviceList = new HashMap<String, ArrayList<DeviceInfo>>();
    protected Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGatewayRecyclerView = (RecyclerView) findViewById(R.id.gateway_list);
        mGatewayRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://203.253.128.161:7579/Mobius/hysorfd/gateways?fu=1&ty=3";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: "+ response);
                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("m2m:uril");
                            for(int i=0; i<jsonArray.length(); i++){
                                String item = jsonArray.getString(i);
                                String[] str_list = item.split("/");
                                if (str_list.length == 4){
                                    // gateway
                                    mGatewayList.add(str_list[3]);
                                    System.out.println(str_list[3]);
                                }
                                System.out.println(item);
                            }

                            mAdapterGateway = new CustomAdapterGateway(mGatewayList, mContext, queue);
                            mGatewayRecyclerView.setAdapter(mAdapterGateway);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-M2M-RI", "12345");
                params.put("X-M2M-Origin", "Shysorfd");
                params.put("Accept", "application/json");

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}