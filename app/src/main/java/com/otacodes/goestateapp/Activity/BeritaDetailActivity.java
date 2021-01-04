package com.otacodes.goestateapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.otacodes.goestateapp.Constants.BaseApp;
import com.otacodes.goestateapp.Constants.Constants;
import com.otacodes.goestateapp.Models.BeritaModels;
import com.otacodes.goestateapp.R;
import com.otacodes.goestateapp.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by otacodes on 3/26/2019.
 */

public class BeritaDetailActivity extends AppCompatActivity {

    TextView propName,toolbartext, tanggal;
    String Id;
    RelativeLayout progress;
    DatabaseHelper databaseHelper;
    WebView description;
    BaseApp baseApp;
    ImageView backButton, images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beritadetail);

        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        databaseHelper = new DatabaseHelper(getApplicationContext());
        baseApp = BaseApp.getInstance();
        propName = findViewById(R.id.propertyname);
        progress = findViewById(R.id.progress);
        tanggal = findViewById(R.id.tanggal);

        toolbartext =findViewById(R.id.toolbartext);

        images = findViewById(R.id.image);
        backButton = findViewById(R.id.back_btn);

        description = findViewById(R.id.description);

        progress.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getBerita();
    }



    private void getBerita() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.BERITAID+Id, parameters, new Response.Listener<JSONObject>() {
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

                    toolbartext.setText(item.getTipe());
                    tanggal.setText(item.getTanggal());

                        propName.setText(item.getJudul());
                        Picasso.with(this)
                                .load(item.getFoto())
                                .resize(150,150)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(images);

                        String mimeType = "text/html";
                        String encoding = "utf-8";
                        String htmlText = item.getDeskripsi();

                        String text = "<html dir=" + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #a5a5a5;text-align:justify;line-height:1.2}"
                                + "</style></head>"
                                + "<body>"
                                + htmlText
                                + "</body></html>";

                    description.loadDataWithBaseURL(null, text, mimeType, encoding, null);


                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

}
