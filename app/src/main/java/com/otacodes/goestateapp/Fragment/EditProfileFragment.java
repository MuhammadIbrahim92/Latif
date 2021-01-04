package com.otacodes.goestateapp.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.otacodes.goestateapp.Activity.PicklocationActivity;
import com.otacodes.goestateapp.Item.ItemProfile;
import com.otacodes.goestateapp.Constants.Functions;
import com.otacodes.goestateapp.Constants.Constants;
import com.otacodes.goestateapp.Activity.MainActivity;
import com.otacodes.goestateapp.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;
import com.wonshinhyo.dragrecyclerview.SimpleDragListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.otacodes.goestateapp.Constants.Constants.Select_image_from_gallry_code;


public class EditProfileFragment extends Fragment {

    View getView;
    Context context;

    DragRecyclerView listPhoto;
    ImageView backBtn;
    IOSDialog iosDialog;
    EditText fullName, phone, email;

    byte[] imageByteArray;

    ItemProfile profilePhotosAdapter;
    ArrayList<String> imagesList;
    private LatLng destinationLocation;
    TextView address, latitude, longitude;
    private final int DESTINATION_ID = 1;
    LinearLayout lladdress;
    Button submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_editprofile, container, false);
        context=getContext();


        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(true)
                .setMessageContentGravity(Gravity.END)
                .build();

        fullName = getView.findViewById(R.id.firstname);
        phone = getView.findViewById(R.id.phone);
        email = getView.findViewById(R.id.email);
        latitude = getView.findViewById(R.id.latitude);
        longitude =  getView.findViewById(R.id.longitude);
        backBtn = getView.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });
        address = getView.findViewById(R.id.address);
        lladdress = getView.findViewById(R.id.lladdress);
        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PicklocationActivity.class);
                intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
                startActivityForResult(intent, PicklocationActivity.LOCATION_PICKER_ID);
            }
        });

        submit = getView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagesList.isEmpty()){
                    Toast.makeText(getActivity(), "Select Image", Toast.LENGTH_SHORT).show();
                }else
                    Edit();
            }
        });

        imagesList =new ArrayList<>();


        listPhoto = getView.findViewById(R.id.listphoto);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
        listPhoto.setLayoutManager(layoutManager);
        listPhoto.setHasFixedSize(false);

        profilePhotosAdapter =new ItemProfile(context, imagesList, new ItemProfile.OnItemClickListener() {
            @Override
            public void onItemClick(String item,int postion, View view) {
                if(view.getId()==R.id.button){
                    if(item.equals("")){
                        selectImage();
                    }else {
                        deletelink(item);
                        profilePhotosAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        profilePhotosAdapter.setOnItemDragListener(new SimpleDragListener() {

            @Override
            public void onDrop(int fromPosition, int toPosition) {
                super.onDrop(fromPosition, toPosition);
            }

            @Override
            public void onSwiped(int pos) {
                super.onSwiped(pos);
                Log.d("drag", "onSwiped " + pos);
            }
        });
        listPhoto.setAdapter(profilePhotosAdapter);
        getUser();

        return getView;
    }



    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Select_image_from_gallry_code);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PicklocationActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String addressset = data.getStringExtra(PicklocationActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
                address.setText(addressset);
                destinationLocation = latLng;
                String latitudetext = String.valueOf(destinationLocation.latitude);
                String longitudetext = String.valueOf(destinationLocation.longitude);
                latitude.setText(latitudetext);
                longitude.setText(longitudetext);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Select_image_from_gallry_code) {

                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            }
            else if (requestCode == 123) {
                handleCrop(resultCode, data);
            }

        }
    }



    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(context,getCurrentFragment(),123);

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri group_image_uri=Crop.getOutput(result);
            Bitmap bmpSample = BitmapFactory.decodeFile(group_image_uri.getPath());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmpSample.compress(Bitmap.CompressFormat.JPEG, 100, out);
            imageByteArray = out.toByteArray();

            SavePicture();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public android.support.v4.app.Fragment getCurrentFragment(){
        return getActivity().getSupportFragmentManager().findFragmentById(R.id.MainFragment);

    }

    public void SavePicture(){
        iosDialog.show();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        String id=reference.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference filelocation = storageReference.child(MainActivity.user_id)
                .child(id+".jpg");
        filelocation.putBytes(imageByteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url=taskSnapshot.getDownloadUrl().toString();
                uploadLink(url);
                iosDialog.cancel();

            }});
    }

    private void uploadLink(String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", MainActivity.user_id);
            parameters.put("image_link",link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.UPLOADIMAGES, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();

                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){
                                getUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void deletelink(String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", MainActivity.user_id);
            parameters.put("image_link",link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.DELETEIMAGES, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();
                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){
                                getUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void getUser() {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", MainActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.USERDATA, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        parseUser(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void parseUser(String loginData){
        iosDialog.cancel();
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);


                imagesList.clear();
                imagesList.add(userdata.optString("imageprofile"));
                fullName.setText(userdata.optString("fullname"));
                phone.setText(userdata.optString("phone"));
                email.setText(userdata.optString("email"));
                address.setText(userdata.optString("alamat"));
                latitude.setText(userdata.optString("alamat_latitude"));
                longitude.setText(userdata.optString("alamat_longitude"));
                profilePhotosAdapter.notifyDataSetChanged();



            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }

    private void Edit() {

        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", MainActivity.user_id);
            parameters.put("fullname", fullName.getText().toString());
            parameters.put("email", email.getText().toString());
            parameters.put("phone", phone.getText().toString());
            parameters.put("alamat", address.getText().toString());
            parameters.put("alamat_latitude", latitude.getText().toString());
            parameters.put("alamat_longitude", longitude.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.EDITPROFILE, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        editData(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void editData(String loginData){
        iosDialog.cancel();
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);

                // if data is save then we will go back
                getActivity().onBackPressed();

            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }




}
