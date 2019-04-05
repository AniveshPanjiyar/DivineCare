package com.siddharth.divinecare;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnSuccessListener;
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
    String defURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=50&order=date&pageToken&type=video&key=AIzaSyDovtex4ZGDh3K9cJhdUAPc_feDyssTQrA";
    ArrayList<String> SUGGESTIONS = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etSearch = findViewById(R.id.et_search);
        getSuggestionsFromDatabaseToArray();

        btnSearch = (Button) findViewById(R.id.btn_search);
        lvVideo = (RecyclerView) findViewById(R.id.videoList);
        modelVideoDetailsArrayList = new ArrayList<>();
        getVideosFromDatabaseToAdapter();
        //uncomment this to push data to database from api call
        //pushNewVideosFromApiCallToDatabase(defURL);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelVideoDetailsArrayList.clear();
                getVideosFromDatabaseAfterSearch(etSearch.getText().toString());
            }
        });

    }

    public void getVideosFromDatabaseAfterSearch(final String search) {
        final ModelVideoDetails modelVideoDetails = new ModelVideoDetails();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("videos");
        //Log.e(TAG,search);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelVideoDetails modelVideoDetails1 = snapshot.getValue(ModelVideoDetails.class);
                    if (modelVideoDetails1 != null) {
                        if (modelVideoDetails1.getVideoName().toLowerCase().indexOf(search.toLowerCase()) > 0||
                                modelVideoDetails1.getVideoName().toLowerCase().matches(search.toLowerCase())||
                                modelVideoDetails1.getVideoName().toLowerCase().contains(search.toLowerCase())) {
                            //Log.e(TAG,"true in if search");
                            modelVideoDetailsArrayList.add(modelVideoDetails1);
                            if (customListAdapter != null)
                                customListAdapter.notifyDataSetChanged();
                        }

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
    }

    public void getSuggestionsFromDatabaseToArray() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("suggestions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String suggestion = snapshot.getValue(String.class);
                    if (!SUGGESTIONS.contains(suggestion))
                        SUGGESTIONS.add(suggestion);
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);
                etSearch.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getVideosFromDatabaseToAdapter() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("videos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelVideoDetails modelVideoDetails = snapshot.getValue(ModelVideoDetails.class);
                    if (modelVideoDetails != null && modelVideoDetails.getUrl() != null) {
                        modelVideoDetailsArrayList.add(modelVideoDetails);
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

        //uncomment to push suggestion (titles of videos) into datatbase
        //pushSuggestionsFromDatabseToDatabase();

    }

    private void pushNewVideosFromApiCallToDatabase(String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                        JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        final ModelVideoDetails modelVideoDetails = new ModelVideoDetails();

                        String videoid = jsonVideoId.getString("videoId");
                        modelVideoDetails.setUrl(jsonObjectdefault.getString("url"));
                        modelVideoDetails.setVideoName(jsonsnippet.getString("title"));
                        modelVideoDetails.setVideoDesc(jsonsnippet.getString("description"));
                        modelVideoDetails.setVideoId(videoid);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("videos");
                        reference.push().setValue(modelVideoDetails);
                    }
                    try {
                        if (jsonObject.has("nextPageToken"))
                        {
                            String nextPageToken=jsonObject.getString("nextPageToken");
                            Log.e(TAG,nextPageToken);
                            pushNewVideosFromApiCallToDatabase("https://www.googleapis.com/youtube/v3/search?part=snippet&" +
                            "channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=50&order=date&pageToken="+nextPageToken+"&type=video" +
                            "&key=AIzaSyDovtex4ZGDh3K9cJhdUAPc_feDyssTQrA");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void pushSuggestionsFromDatabseToDatabase() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("suggestions");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("videos");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelVideoDetails modelVideoDetails = snapshot.getValue(ModelVideoDetails.class);
                    if (modelVideoDetails != null) {
                        //modelVideoDetails.getVideoName();
                        databaseReference.push().setValue(modelVideoDetails.getVideoName());
                        String[] words = modelVideoDetails.getVideoName().split(" ");
                        for (String eachWord : words) {
                            //databaseReference.push().setValue(eachWord);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
