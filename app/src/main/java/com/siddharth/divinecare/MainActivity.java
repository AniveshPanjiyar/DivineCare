package com.siddharth.divinecare;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView etSearch;
    Button btnSearch, more;
    RecyclerView lvVideo;
    ArrayList<ModelVideoDetails> modelVideoDetailsArrayList;
    VideoAdapter customListAdapter;
    String searchName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String TAG = "MainActivity";
    String defURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=50&order=date&pageToken&type=video&key=AIzaSyDovtex4ZGDh3K9cJhdUAPc_feDyssTQrA";
    ArrayList<String> SUGGESTIONS = new ArrayList<String>();
    int pageItems = 10;
    int pageNumber = 1;

    int page = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //etSearch = findViewById(R.id.et_search);
        //more = findViewById(R.id.moreButton);
        //getSuggestionsFromDatabaseToArray();
        //btnSearch = (Button) findViewById(R.id.btn_search);
        //lvVideo = (RecyclerView) findViewById(R.id.videoList);
        //modelVideoDetailsArrayList = new ArrayList<>();
        //getVideosFromDatabaseToAdapter(pageItems);
        //uncomment this to push data to database from api call
        //pushNewVideosFromApiCallToDatabase(defURL);

        //navigation bar
        initUI();


        /*lvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getAdapter() != null)
                    if (recyclerView.getAdapter().getItemCount() != 0) {
                        int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                            Log.e(TAG, "Last");
                            pageNumber++;
                            getVideosFromDatabaseToAdapter(pageItems * pageNumber);
                        }
                    }

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelVideoDetailsArrayList.clear();
                getVideosFromDatabaseAfterSearch(etSearch.getText().toString());
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumber++;
                getVideosFromDatabaseToAdapter(pageItems * pageNumber);
            }
        });*/

    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.empty_layout, null, false);;
                if(position==1)
                {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.item_vp, null, false);
                    //final TextView txtPage = (TextView) view.findViewById(R.id.txt_vp_item_page);
                    //txtPage.setText(String.format("Page #%d", position));
                    etSearch = view.findViewById(R.id.et_search);
                    more = view.findViewById(R.id.moreButton);
                    getSuggestionsFromDatabaseToArray();
                    btnSearch = (Button) view.findViewById(R.id.btn_search);
                    lvVideo = (RecyclerView) view.findViewById(R.id.videoList);
                    modelVideoDetailsArrayList = new ArrayList<>();
                    getVideosFromDatabaseToAdapter(pageItems);
                    btnSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modelVideoDetailsArrayList.clear();
                            getVideosFromDatabaseAfterSearch(etSearch.getText().toString());
                        }
                    });
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pageNumber++;
                            getVideosFromDatabaseToAdapter(pageItems * pageNumber);
                        }
                    });
                }
                else if(position==2)
                {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.empty_layout, null, false);
                }
                else if(position==3)
                {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.empty_layout, null, false);
                }
                else if(position==4)
                {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.empty_layout, null, false);
                }
                else
                {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.empty_layout, null, false);
                }



                //final TextView txtPage = (TextView) view.findViewById(R.id.txt_vp_item_page);
                //txtPage.setText(String.format("Page #%d", position));

                container.addView(view);
                return view;
            }
        });

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Heart")
                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Cup")
                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Diploma")
                        .badgeTitle("state")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth),
                        Color.parseColor(colors[3]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Flag")
                        .badgeTitle("icon")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Medal")
                        .badgeTitle("777")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
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
                        if (modelVideoDetails1.getVideoName().toLowerCase().indexOf(search.toLowerCase()) > 0 ||
                                modelVideoDetails1.getVideoName().toLowerCase().matches(search.toLowerCase()) ||
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


    private void getVideosFromDatabaseToAdapter(final int pageitems) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("videos");
        ref.limitToFirst(pageitems).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (pageNumber == 1) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelVideoDetails modelVideoDetails = snapshot.getValue(ModelVideoDetails.class);
                        if (modelVideoDetails != null && modelVideoDetails.getUrl() != null) {
                            modelVideoDetailsArrayList.add(modelVideoDetails);
                            if (customListAdapter != null)
                                customListAdapter.notifyDataSetChanged();
                        }

                    }
                } else {
                    Log.e(TAG, String.valueOf(pageItems * (pageNumber - 1)));
                    int skipvideos = -1;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Log.e(TAG,String.valueOf(skipvideos));
                        skipvideos++;
                        if (skipvideos >= (pageItems * (pageNumber - 1))) {
                            ModelVideoDetails modelVideoDetails = snapshot.getValue(ModelVideoDetails.class);
                            if (modelVideoDetails != null && modelVideoDetails.getUrl() != null) {
                                modelVideoDetailsArrayList.add(modelVideoDetails);
                                if (customListAdapter != null)
                                    customListAdapter.notifyDataSetChanged();
                                if(skipvideos==pageItems * (pageNumber - 1))
                                {
                                    lvVideo.scrollToPosition(skipvideos);
                                }
                            }
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

        //uncomment to push suggestion (titles of videos) into datatbase
        //pushSuggestionsFromDatabseToDatabase();

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
                        if (jsonObject.has("nextPageToken")) {
                            String nextPageToken = jsonObject.getString("nextPageToken");
                            Log.e(TAG, nextPageToken);
                            pushNewVideosFromApiCallToDatabase("https://www.googleapis.com/youtube/v3/search?part=snippet&" +
                                    "channelId=UC0bB4q6DDEop428dHHraWVg&maxResults=50&order=date&pageToken=" + nextPageToken + "&type=video" +
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
