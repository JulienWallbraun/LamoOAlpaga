package com.example.lamaoalpaga.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.lamaoalpaga.Model.Animal;
import com.example.lamaoalpaga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.*;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mAppQuestion;
    private ImageView mPicture;
    private ImageView mPictureSource;
    private Button mLamaButton;
    private Button mAlpagaButton;
    private MediaPlayer mJarabeMusic;
    private MediaPlayer mDeguelloMusic;
    private static final String PIXABAY_KEY = "18957740-cc52fa8c7975fbd4a749df833";

    private Animal animalCorrectAnswer;
    private ArrayList lamasPicturesUrls;
    private ArrayList alpagasPicturesUrls;
    private Response.Listener<JSONObject> responseListenerGetRequestLamas;
    private Response.Listener<JSONObject> responseListenerGetRequestAlpagas;

    private TypedArray lamasPicturesOffline;
    private TypedArray alpagasPicturesOffline;

    public GameActivity(){
        lamasPicturesUrls = new ArrayList();
        alpagasPicturesUrls = new ArrayList();
        responseListenerGetRequestLamas = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        lamasPicturesUrls.add(jsonArray.getJSONObject(i).getString("webformatURL"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!lamasPicturesUrls.isEmpty() && !alpagasPicturesUrls.isEmpty()) {
                    selectPictureFromOfflinePictures();
                }
            }
        };
        responseListenerGetRequestAlpagas = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        alpagasPicturesUrls.add(jsonArray.getJSONObject(i).getString("webformatURL"));
                    }
                    //picturesUrls = jsonArray;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!lamasPicturesUrls.isEmpty() && !alpagasPicturesUrls.isEmpty()) {
                    selectPictureFromOfflinePictures();
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layout wiring
        setContentView(R.layout.activity_game);

        mAppQuestion = findViewById(R.id.activity_game_question_text);
        mPicture = findViewById(R.id.activity_game_picture);
        mPictureSource = findViewById(R.id.activity_game_picture_source);
        mLamaButton = findViewById(R.id.activity_game_answerLama_btn);
        mAlpagaButton = findViewById(R.id.activity_game_answerAlpaga_btn);
        mJarabeMusic = MediaPlayer.create(getApplicationContext(), R.raw.jarabe);
        mDeguelloMusic = MediaPlayer.create(getApplicationContext(), R.raw.deguello);

        mAppQuestion .setText("¿"+getResources().getString(R.string.lama)+" o "+getResources().getString(R.string.alpaga)+"?");

        mLamaButton.setOnClickListener(this);
        mAlpagaButton.setOnClickListener(this);

        mLamaButton.setTag(Animal.LAMA);
        mAlpagaButton.setTag(Animal.ALPAGA);

        if (isNetworkAvailable()) {
            getPicturesOnline(getAnimalString(Animal.LAMA), responseListenerGetRequestLamas);
            getPicturesOnline(getAnimalString(Animal.ALPAGA), responseListenerGetRequestAlpagas);
        } else {
            selectPictureFromOnlinePictures();
        }
    }

    @Override
    public void onClick(View v) {
        Animal answer = (Animal) v.getTag();
        if (answer == animalCorrectAnswer){
            Toast.makeText(this, "Verdadero!", Toast.LENGTH_SHORT).show();
            if (mDeguelloMusic.isPlaying()){
                mDeguelloMusic.pause();
            }
            if (!mJarabeMusic.isPlaying()) {
                mJarabeMusic.start();
            }
        }
        else{
            Toast.makeText(this, "No es un "+getAnimalString(answer)+" sino un "+ getAnimalString(animalCorrectAnswer)+"!", Toast.LENGTH_SHORT).show();
            if (mJarabeMusic.isPlaying()){
                mJarabeMusic.pause();
            }
            if (!mDeguelloMusic.isPlaying()) {
                mDeguelloMusic.start();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()){
                    if (!lamasPicturesUrls.isEmpty() && !alpagasPicturesUrls.isEmpty()) {
                        selectPictureFromOfflinePictures();
                    }
                    else if (lamasPicturesUrls.isEmpty()) {
                        getPicturesOnline(getAnimalString(Animal.LAMA), responseListenerGetRequestLamas);
                    }
                    else if (alpagasPicturesUrls.isEmpty()) {
                        getPicturesOnline(getAnimalString(Animal.ALPAGA), responseListenerGetRequestAlpagas);
                    }
                }
                else{
                    selectPictureFromOnlinePictures();
                }
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDeguelloMusic.isPlaying()){
            mDeguelloMusic.pause();
        }
        if (mJarabeMusic.isPlaying()) {
            mJarabeMusic.pause();
        }
    }

    public void selectPictureFromOnlinePictures() {
        if (lamasPicturesOffline==null){
            lamasPicturesOffline = getResources().obtainTypedArray(R.array.lamaPictures);
        }
        if (alpagasPicturesOffline==null){
            alpagasPicturesOffline = getResources().obtainTypedArray(R.array.alpagaPictures);
        }
        Random random = new Random();
        int randomNumberAnimal = random.nextInt(2);
        int randomNumberPicture;
        int pic;
        if (randomNumberAnimal == 0) {
            animalCorrectAnswer = Animal.LAMA;
            randomNumberPicture = random.nextInt(lamasPicturesOffline.length());
            pic = lamasPicturesOffline.getResourceId(randomNumberPicture, 0);
        } else {
            animalCorrectAnswer = Animal.ALPAGA;
            randomNumberPicture = random.nextInt(alpagasPicturesOffline.length());
            pic = alpagasPicturesOffline.getResourceId(randomNumberPicture, 0);
        }
        mPicture.setImageResource(pic);
        mPictureSource.setImageResource(0);
    }

    public void selectPictureFromOfflinePictures() {
        String url = "";
        Random random = new Random();
        int randomNumberAnimal = random.nextInt(2);
        if (randomNumberAnimal == 0) {
            animalCorrectAnswer = Animal.LAMA;
            int randomNumberPicture;
            randomNumberPicture = random.nextInt(lamasPicturesUrls.size());
            url = (String) lamasPicturesUrls.get(randomNumberPicture);
        } else {
            animalCorrectAnswer = Animal.ALPAGA;
            int randomNumberPicture;
            randomNumberPicture = random.nextInt(alpagasPicturesUrls.size());
            url = (String) alpagasPicturesUrls.get(randomNumberPicture);
        }
        Glide.with(this).load(url).into(mPicture);
        mPictureSource.setImageResource(R.drawable.pixabay_logo);
    }

    private void getPicturesOnline(String animal, Response.Listener<JSONObject> responseListener){
        String url = "https://pixabay.com/api/?key="+PIXABAY_KEY+"&q="+animal.replace(" ","+")+"&image_type=photo&per_page=200";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public String getAnimalString(Animal animal){
        String res = "";
        if (animal==Animal.LAMA){
            res = getResources().getString(R.string.lama);
        }
        else if (animal==Animal.ALPAGA){
            res = getResources().getString(R.string.alpaga);
        }
        return res;
    }

}