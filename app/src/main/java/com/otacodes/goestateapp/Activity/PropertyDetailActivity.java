package com.otacodes.goestateapp.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ornolfr.ratingview.RatingView;
import com.otacodes.goestateapp.Constants.BaseApp;
import com.otacodes.goestateapp.Constants.Constants;
import com.otacodes.goestateapp.Fragment.ChatFragment;
import com.otacodes.goestateapp.Item.AmenitiesItem;
import com.otacodes.goestateapp.Item.GalleryItem;
import com.otacodes.goestateapp.Models.PropertyModels;
import com.otacodes.goestateapp.R;
import com.otacodes.goestateapp.Utils.BannerAds;
import com.otacodes.goestateapp.Utils.DatabaseHelper;
import com.otacodes.goestateapp.Utils.NetworkUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by otacodes on 3/26/2019.
 */

public class PropertyDetailActivity extends AppCompatActivity {

    TextView propName, address, nameuser, category, type, city, price, bed, bath, area, ratenow;
    String Id;
    ImageView imageuser, images, backButton, chat, phone, likeButton;
    PropertyModels item;
    RelativeLayout progress;
    LinearLayout llprofile, delete;
    DatabaseHelper databaseHelper;
    ArrayList<PropertyModels> mPropertyList;
    ArrayList<String> mAmenities, addgallery;
    WebView description;
    RecyclerView gallery, amenities;
    ImageView fab;
    GalleryItem galleryItem;
    RatingView ratingView;
    BaseApp baseApp;
    Button brosur;
    ProgressDialog pDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View rating_sheet;
    AmenitiesItem amenitiesItem;
    CardView rledit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propertydetail);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        rating_sheet = findViewById(R.id.rating_sheet);
        mBehavior = BottomSheetBehavior.from(rating_sheet);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        item = new PropertyModels();
        baseApp = BaseApp.getInstance();
        mPropertyList = new ArrayList<>();
        mAmenities = new ArrayList<>();
        addgallery = new ArrayList<>();
        phone = findViewById(R.id.phone);
        brosur = findViewById(R.id.brosur);
        propName = findViewById(R.id.propertyname);
        fab = findViewById(R.id.lokasi);
        progress = findViewById(R.id.progress);
        address = findViewById(R.id.address);
        nameuser = findViewById(R.id.name);
        imageuser = findViewById(R.id.imageuser);
        images = findViewById(R.id.image);
        backButton = findViewById(R.id.back_btn);
        likeButton = findViewById(R.id.like_btn);
        category = findViewById(R.id.category);
        type = findViewById(R.id.type);
        city = findViewById(R.id.city);
        price = findViewById(R.id.propertyprice);
        bed = findViewById(R.id.bed);
        bath = findViewById(R.id.bath);
        area = findViewById(R.id.square);
        description = findViewById(R.id.description);
        gallery = findViewById(R.id.galleryre);
        amenities = findViewById(R.id.amenities);
        chat = findViewById(R.id.chat);
        llprofile = findViewById(R.id.llprofile);
        rledit = findViewById(R.id.rledit);
        delete = findViewById(R.id.lldelete);
        ratenow =findViewById(R.id.rate);
        ratingView = findViewById(R.id.ratingView);

        progress.setVisibility(View.VISIBLE);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.getIsLogin()) {
                    chatFragment(MainActivity.user_id, item.getUserId(), item.getNameUser(), item.getImageUser());
                } else {
                    Intent intent = new Intent(PropertyDetailActivity.this, LoginFormActivity.class);
                    startActivity(intent);
                }
            }
        });



        gallery.setHasFixedSize(true);
        gallery.setNestedScrollingEnabled(false);
        gallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        amenities.setHasFixedSize(true);
        amenities.setNestedScrollingEnabled(false);
        amenities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDone();
            }
        });

        ratenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateNow();
            }
        });


        rledit.setVisibility(View.GONE);
        isFavourite();
        getData();
    }

    private void rateNow() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_rating, null);
        ImageView btnclose = mDialog.findViewById(R.id.bt_close);
        final RatingView ratingView = mDialog.findViewById(R.id.ratingView);
        Button submit = mDialog.findViewById(R.id.submit);
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        ratingView.setRating(0);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(PropertyDetailActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
                if (NetworkUtils.isConnected(PropertyDetailActivity.this)) {
                    JSONObject parameters = new JSONObject();
                    RequestQueue rq = Volley.newRequestQueue(PropertyDetailActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, Constants.RATING+Id+"&rate="+ ratingView.getRating()+"&device_id="+ deviceId, parameters, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String respo=response.toString();
                                    Log.d("responce",respo);
                                    pDialog.dismiss();
                                    Toast.makeText(PropertyDetailActivity.this, "Thanks For Review", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    Log.d("respo",error.toString());
                                    Toast.makeText(PropertyDetailActivity.this, "Problem", Toast.LENGTH_SHORT).show();
                                }
                            });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    rq.getCache().clear();
                    rq.add(jsonObjectRequest);
                    mBottomSheetDialog.hide();
                } else {
                    Toast.makeText(PropertyDetailActivity.this, "No connection Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(mDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }



    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.PROPPERTYDETAIL+Id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataProperty(respo);
                        getDataImage(respo);
                        progress.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
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

    public void getDataProperty(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setAddress(userdata.getString("address"));
                    item.setUserId(userdata.getString("userid"));
                    item.setNameUser(userdata.getString("fullname"));
                    item.setImageUser(userdata.getString("imageprofile"));
                    item.setCid(userdata.getString("cid"));
                    item.setCname(userdata.getString("cname"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setPurpose(userdata.getString("purpose"));
                    item.setAmenities(userdata.getString("amenities"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setImage(userdata.getString("image"));
                    item.setBed(userdata.getString("bed"));
                    item.setBath(userdata.getString("bath"));
                    item.setArea(userdata.getString("area"));
                    item.setPrice(userdata.getString("price"));
                    item.setPhone(userdata.getString("phone"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setDescription(userdata.getString("description"));
                    item.setPricelist(userdata.getString("pricelist"));
                    item.setBrosur(userdata.getString("brosur"));

                    if(item.getPhone().equals(""))
                    {
                        phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(PropertyDetailActivity.this, "Phone is not available", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + item.getPhone()));
                                startActivity(callIntent);
                            }
                        });
                    }

                    likeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentValues fav = new ContentValues();
                            if (databaseHelper.getFavouriteById(item.getPropid())) {
                                databaseHelper.removeFavouriteById(item.getPropid());
                                likeButton.setColorFilter(getResources().getColor(R.color.gray));
                                Toast.makeText(PropertyDetailActivity.this, "Remove To Favourite", Toast.LENGTH_SHORT).show();
                            } else {
                                fav.put(DatabaseHelper.KEY_ID, item.getPropid());
                                fav.put(DatabaseHelper.KEY_TITLE, item.getName());
                                fav.put(DatabaseHelper.KEY_IMAGE, item.getImage());
                                fav.put(DatabaseHelper.KEY_RATE, item.getRateAvg());
                                fav.put(DatabaseHelper.KEY_BED, item.getBed());
                                fav.put(DatabaseHelper.KEY_BATH, item.getBath());
                                fav.put(DatabaseHelper.KEY_ADDRESS, item.getAddress());
                                fav.put(DatabaseHelper.KEY_AREA, item.getArea());
                                fav.put(DatabaseHelper.KEY_PRICE, item.getPrice());
                                fav.put(DatabaseHelper.KEY_PURPOSE, item.getPurpose());
                                databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                                likeButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                Toast.makeText(PropertyDetailActivity.this, "Add To Favourite", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if(item.getUserId().equals(MainActivity.user_id)) {
                        llprofile.setVisibility(View.GONE);
                        rledit.setVisibility(View.VISIBLE);
                    } else {
                        llprofile.setVisibility(View.VISIBLE);
                        rledit.setVisibility(View.GONE);
                    }

                    if(item.getBrosur().equals(""))
                    {
                        brosur.setVisibility(View.GONE);
                    }
                    else {
                        brosur.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = item.getBrosur();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                    }

                        propName.setText(item.getName());
                        address.setText(item.getAddress());
                        nameuser.setText(item.getNameUser());
                        ratingView.setRating(Float.parseFloat(item.getRateAvg()));
                        Double getprice = Double.valueOf(item.getPrice());
                        String total = String.format(Locale.US, "Ryal.%s",
                            NumberFormat.getNumberInstance(Locale.US).format(getprice));
                        price.setText(total);
                        category.setText(item.getCname());
                        type.setText(item.getPurpose());
                        city.setText(item.getCityName());
                        bed.setText(item.getBed() + " " + "Year");
                        bath.setText(item.getBath() );
                        area.setText(item.getArea() );
                        Picasso.with(this)
                                .load(item.getImageUser())
                                .resize(250, 250)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(imageuser);
                        Picasso.with(this)
                                .load(item.getImage())
                                .resize(150,150)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(images);

                        String mimeType = "text/html";
                        String encoding = "utf-8";
                        String htmlText = item.getDescription();

                        String text = "<html dir=" + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #a5a5a5;text-align:justify;line-height:1.2}"
                                + "</style></head>"
                                + "<body>"
                                + htmlText
                                + "</body></html>";

                        if (!item.getAmenities().isEmpty())
                            mAmenities = new ArrayList<>(Arrays.asList(item.getAmenities().split(",")));

                        description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String geoUri = "http://maps.google.com/maps?q=loc:" + item.getLatitude() + "," + item.getLongitude() + " (" + item.getName() + ")";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                                startActivity(intent);
                            }
                        });




                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        amenitiesItem = new AmenitiesItem(this, mAmenities);
        amenities.setAdapter(amenitiesItem);
    }

    public void getDataImage(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    addgallery.add(userdata.getString("image"));
                    addgallery.add(userdata.getString("floorplan"));
                    JSONArray username_obj = userdata.getJSONArray("galleryimage");
                    for (int j = 0; i < msg.length(); j++) {
                        JSONObject userdata1 = username_obj.getJSONObject(j);
                        addgallery.add(userdata1.optString("gallery"));

                        galleryItem = new GalleryItem(this, addgallery, R.layout.item_square);
                        gallery.setAdapter(galleryItem);

                        galleryItem.setOnItemClickListener(new GalleryItem.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, String viewModel, int pos) {
                                Intent i = new Intent(PropertyDetailActivity.this, FullImageActivity.class);
                                i.putExtra(FullImageActivity.EXTRA_POS, pos);
                                i.putStringArrayListExtra(FullImageActivity.EXTRA_IMGS, addgallery);
                                startActivity(i);
                            }
                        });

                    }
                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }



    }



    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            likeButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
        } else {
            likeButton.setColorFilter(getResources().getColor(R.color.gray));
        }
    }
    public void chatFragment(String senderid,String receiverid,String name,String picture){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();

    }

    private void delete() {
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.DELETEPROPERTY+item.getPropid()+"&userid="+MainActivity.user_id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
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

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Delete?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delete();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
