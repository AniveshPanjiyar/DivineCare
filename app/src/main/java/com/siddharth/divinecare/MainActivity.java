package com.siddharth.divinecare;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.siddharth.divinecare.Adapters.VideoAdapter;
import com.siddharth.divinecare.Models.ModelVideoDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView etSearch;
    Button btnSearch;
    RecyclerView lvVideo;
    ArrayList<ModelVideoDetails> modelVideoDetailsArrayList;
    VideoAdapter customListAdapter;
    String searchName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String TAG = "MainActivity";
    String defURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=24&order=date&type=video&key=AIzaSyB_imF8YXU8atV9RMcKaBerNmOrlw0yx8k";
    private String[] SUGGESTIONS = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etSearch = findViewById(R.id.et_search);
        //findsuggestions();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);
        etSearch.setAdapter(adapter);

        btnSearch = (Button) findViewById(R.id.btn_search);
        lvVideo = (RecyclerView) findViewById(R.id.videoList);
        modelVideoDetailsArrayList = new ArrayList<>();
        getdata();
        //showVideo(defURL);
        /*btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchName = etSearch.getText().toString();
                modelVideoDetailsArrayList.clear();
                showVideo("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=10&order=date&type=video&q=" + searchName + "&key=AIzaSyB_imF8YXU8atV9RMcKaBerNmOrlw0yx8k");
                findsuggestions();
            }
        });*/

    }

    private void findsuggestions() {
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=10&order=date&type=video&q=" + searchName + "&key=AIzaSyB_imF8YXU8atV9RMcKaBerNmOrlw0yx8k";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                        //Log.e(TAG, "video ID" + jsonVideoId);
                        JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        ModelVideoDetails modelVideoDetails = new ModelVideoDetails();

                        String videoid = jsonVideoId.getString("kind");
                        //Log.e(TAG, " New Video Id" + videoid);
                        modelVideoDetails.setUrl(jsonObjectdefault.getString("url"));
                        modelVideoDetails.setVideoName(jsonsnippet.getString("title"));
                        modelVideoDetails.setVideoDesc(jsonsnippet.getString("description"));
                        String title = modelVideoDetails.getVideoName();
                        //Log.e(TAG,title);
                        String[] words = title.split(" ");
                        for (String eachWord : words) {
                            int index = SUGGESTIONS.length - 1;
                            SUGGESTIONS[index] = eachWord;
                            Log.e(TAG, eachWord);
                        }
                    }
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

    private void showVideo(String url) {

        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=10&order=date&type=video&q=" + searchName + "&key=AIzaSyB_imF8YXU8atV9RMcKaBerNmOrlw0yx8k";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                        //Log.e(TAG, "video ID" + jsonVideoId);
                        JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        final ModelVideoDetails modelVideoDetails = new ModelVideoDetails();

                        String videoid = jsonVideoId.getString("videoId");
                        //Log.e(TAG, " New Video Id" + videoid);
                        modelVideoDetails.setUrl(jsonObjectdefault.getString("url"));
                        modelVideoDetails.setVideoName(jsonsnippet.getString("title"));
                        modelVideoDetails.setVideoDesc(jsonsnippet.getString("description"));
                        modelVideoDetails.setVideoId(videoid);
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("videos");
                        HashMap<String,Object> upload=new HashMap<>();
                        upload.put(jsonsnippet.getString("title").toString(),modelVideoDetails);
                        //reference.updateChildren(upload);
                        reference.push().setValue(modelVideoDetails);
                        /*DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("videos");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    ModelVideoDetails videoDetails1=snapshot.getValue(ModelVideoDetails.class);
                                    if(videoDetails1!=null&&videoDetails1.getUrl()!=null)
                                    {
                                        Log.e(TAG,videoDetails1.getUrl());
                                        videoDetails1.getVideoDesc();
                                        videoDetails1.getVideoId();
                                        videoDetails1.getVideoName();

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/


                        modelVideoDetailsArrayList.add(modelVideoDetails);

//                         modelVideoDetailsArrayList.clear();
                    }

                    //lvVideo.setAdapter(customListAdapter);

                    //customListAdapter.notifyDataSetChanged();
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

    private void getdata() {
        //database
        //DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("videos");
        //HashMap<String,Object> upload=new HashMap<>();
        //upload.put(jsonsnippet.getString("title").toString(),modelVideoDetails);
        //reference.updateChildren(upload);
        final ModelVideoDetails modelVideoDetails = new ModelVideoDetails();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("videos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelVideoDetails modelVideoDetails1 = snapshot.getValue(ModelVideoDetails.class);
                    if (modelVideoDetails1 != null && modelVideoDetails1.getUrl() != null) {
                        modelVideoDetailsArrayList.add(modelVideoDetails1);
                        if (customListAdapter != null)
                            customListAdapter.notifyDataSetChanged();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        customListAdapter = new VideoAdapter(MainActivity.this, modelVideoDetailsArrayList);
        lvVideo.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        lvVideo.setAdapter(customListAdapter);

        //customListAdapter.notifyDataSetChanged();

    }
}
