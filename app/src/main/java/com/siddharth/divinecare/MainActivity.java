package com.siddharth.divinecare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   EditText etSearch;
   Button btnSearch;
   ListView lvVideo;
   ArrayList<VideoDetails> videoDetailsArrayList;
   CustomListAdapter customListAdapter;
   String searchName;
   String TAG="MainActivity";
   String defURL="https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=24&order=date&type=video&key=AIzaSyCDgWb24K_JbWgJi8q9grBSm9N-bINUfPo";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etSearch=(EditText)findViewById(R.id.et_search);
        btnSearch=(Button)findViewById(R.id.btn_search);
        lvVideo=(ListView)findViewById(R.id.videoList);
        videoDetailsArrayList=new ArrayList<>();
        customListAdapter=new CustomListAdapter(MainActivity.this,videoDetailsArrayList);
        showVideo(defURL);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchName=etSearch.getText().toString();
                videoDetailsArrayList.clear();
                showVideo("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=10&order=date&type=video&q="+searchName+"&key=AIzaSyCDgWb24K_JbWgJi8q9grBSm9N-bINUfPo");
            }
        });

    }

    private void showVideo(String url) {

        String URL="https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=10&order=date&type=video&q="+searchName+"&key=AIzaSyCDgWb24K_JbWgJi8q9grBSm9N-bINUfPo";
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("items");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId=jsonObject1.getJSONObject("id");
                        Log.e(TAG,"video ID"+jsonVideoId);
                        JSONObject jsonsnippet= jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        VideoDetails videoDetails=new VideoDetails();

                        String videoid=jsonVideoId.getString("kind");
                        Log.e(TAG," New Video Id" +videoid);
                        videoDetails.setURL(jsonObjectdefault.getString("url"));
                        videoDetails.setVideoName(jsonsnippet.getString("title"));
                        videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                     //   videoDetails.setVideoId(videoID);

                       videoDetailsArrayList.add(videoDetails);

//                         videoDetailsArrayList.clear();
                    }
                    lvVideo.setAdapter(customListAdapter);

             customListAdapter.notifyDataSetChanged();
                   // lvVideo.setAdapter(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
