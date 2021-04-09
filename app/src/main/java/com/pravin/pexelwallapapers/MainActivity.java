package com.pravin.pexelwallapapers;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    List<ImageModel> imageModelList;
    ImageButton ibSearch;
    int pageNumber = 1;

    Boolean isScrolling  = false;
    int currentItems,totalItems,scrollOutItems;
    String url ="https://api.pexels.com/v1/curated/?page="+pageNumber+"&per_page=80";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        ibSearch = findViewById(R.id.ibSearch);

        imageModelList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageModelList);

        recyclerView.setAdapter(imageAdapter);

      //  final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling= true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = staggeredGridLayoutManager.getChildCount();
                totalItems = staggeredGridLayoutManager.getItemCount();
              //  int scrollOutItems = (recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                scrollOutItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];

                if(isScrolling && (currentItems+scrollOutItems==totalItems)){
                    isScrolling = false;
                    fetchWallpaper();
                }
            }
        });


        fetchWallpaper();

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog();
            }
        });

    }

    public void fetchWallpaper(){

        StringRequest request = new StringRequest(Request.Method.GET,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray= jsonObject.getJSONArray("photos");

                            int length = jsonArray.length();

                            for(int i=0;i<length;i++){

                                JSONObject object = jsonArray.getJSONObject(i);

                                int id = object.getInt("id");

                                JSONObject objectImages = object.getJSONObject("src");

                                String orignalUrl = objectImages.getString("original");
                                String mediumUrl = objectImages.getString("medium");

                                ImageModel wallpaperModel = new ImageModel(id,orignalUrl,mediumUrl);
                                imageModelList.add(wallpaperModel);



                            }

                            imageAdapter.notifyDataSetChanged();
                            pageNumber++;

                        }catch (JSONException e){

                        }





                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","563492ad6f917000010000011069c623c7ad4919b7f934771d8adbe9");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }


    private void searchDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        alert.setMessage("Enter Category e.g. Nature");
        alert.setTitle("Search Wallpaper");

        alert.setView(editText);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String query = editText.getText().toString().toLowerCase();

                url = "https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+query;
                imageModelList.clear();
                fetchWallpaper();

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.show();

    }

}