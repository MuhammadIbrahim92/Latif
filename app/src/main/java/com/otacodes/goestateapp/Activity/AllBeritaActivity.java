package com.otacodes.goestateapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.otacodes.goestateapp.Constants.Constants;
import com.otacodes.goestateapp.Item.BeritalistItem;
import com.otacodes.goestateapp.Models.BeritaModels;
import com.otacodes.goestateapp.Models.CategoryModels;
import com.otacodes.goestateapp.Models.CityModels;
import com.otacodes.goestateapp.Models.PropertyModels;
import com.otacodes.goestateapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllBeritaActivity extends AppCompatActivity {

    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    String purpose, Id;
    ArrayList<String> catNameList, cityNameList;
    ArrayList<CategoryModels> mListCat;
    ArrayList<CityModels> mListCity;
    ImageView search, backbtn;
    LinearLayout noresult;
    TextView toolbartext;
    RelativeLayout progress;
    CardView filterandsort;
    ArrayList<BeritaModels> mBeritaList;
    BeritalistItem beritaItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        mBeritaList = new ArrayList<>();
        listItem = new ArrayList<>();
        mListCat= new ArrayList<>();
        cityNameList= new ArrayList<>();
        catNameList= new ArrayList<>();
        mListCity= new ArrayList<>();
        recyclerView = findViewById(R.id.recycle);
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);
        search = findViewById(R.id.search);
        noresult = findViewById(R.id.noresult);
        toolbartext = findViewById(R.id.toolbartext);
        filterandsort = findViewById(R.id.rlfilter);

        filterandsort.setVisibility(View.GONE);

        toolbartext.setText("Latest News");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progress.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        getBerita();
    }





    private void getBerita() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLBERITA, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataBerita(respo);
                        progress.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataBerita(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    BeritaModels item = new BeritaModels();
                    item.setId(userdata.getString("id"));
                    item.setJudul(userdata.getString("judul"));
                    item.setDeskripsi(userdata.getString("deskripsi"));
                    item.setFoto(userdata.getString("foto"));
                    item.setTipe(userdata.getString("tipe"));
                    item.setTanggal(userdata.getString("tanggal"));

                    mBeritaList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        beritaItem = new BeritalistItem(this, mBeritaList, R.layout.item_berita);
        recyclerView.setAdapter(beritaItem);
    }
}
