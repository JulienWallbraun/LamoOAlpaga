package com.example.lamaoalpaga.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.media.MediaPlayer;
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
import com.example.lamaoalpaga.Model.Question;
import com.example.lamaoalpaga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mQuestionUrl;
    private ImageView mPicture;
    private Button mLamaButton;
    private Button mAlpagaButton;
    private MediaPlayer mJarabeMusic;
    private MediaPlayer mDeguelloMusic;
    private static final String PIXABAY_KEY = "18957740-cc52fa8c7975fbd4a749df833";

    private String lamaString;
    private String alpagaString;
    private Question question;
    private ArrayList lamasPicturesUrls;
    private ArrayList alpagasPicturesUrls;
    private Response.Listener<JSONObject> responseListenerGetRequestLamas;
    private Response.Listener<JSONObject> responseListenerGetRequestAlpagas;

    private TypedArray lamasPicturesOffline;
    private TypedArray alpagasPicturesOffline;

    private final Boolean ONLINE = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("on est dans le oncreate");

        super.onCreate(savedInstanceState);
        //layout wiring
        setContentView(R.layout.activity_game);
        mQuestionUrl = findViewById(R.id.activity_game_url_text);
        mPicture = findViewById(R.id.activity_game_picture_text);
        mLamaButton = findViewById(R.id.activity_game_answerLama_btn);
        mAlpagaButton = findViewById(R.id.activity_game_answerAlpaga_btn);
        mJarabeMusic = MediaPlayer.create(getApplicationContext(), R.raw.jarabe);
        mDeguelloMusic = MediaPlayer.create(getApplicationContext(), R.raw.deguello);

        mLamaButton.setOnClickListener(this);
        mAlpagaButton.setOnClickListener(this);

        mLamaButton.setTag(Animal.LAMA);
        mAlpagaButton.setTag(Animal.ALPAGA);

        lamaString = getResources().getString(R.string.lama);
        alpagaString = getResources().getString(R.string.alpaga);

        if (ONLINE) {
            lamasPicturesUrls = new ArrayList();
            alpagasPicturesUrls = new ArrayList();
            responseListenerGetRequestLamas = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("traitement de la réponse de la requête GET sur les lamas");
                    try {
                        JSONArray jsonArray = response.getJSONArray("hits");
                        System.out.println("try réussi");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            System.out.println("url de l'image lama n°" + i + " : " + jsonArray.getJSONObject(i).getString("webformatURL"));
                            lamasPicturesUrls.add(jsonArray.getJSONObject(i).getString("webformatURL"));
                            System.out.println("nombre d'urls de lamas = " + lamasPicturesUrls.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!lamasPicturesUrls.isEmpty() && !alpagasPicturesUrls.isEmpty()) {
                        generateQuestionOnline();
                    }
                }
            };
            responseListenerGetRequestAlpagas = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("traitement de la réponse de la requête GET sur les alpagas");
                    try {
                        JSONArray jsonArray = response.getJSONArray("hits");
                        System.out.println("try réussi");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            System.out.println("url de l'image alpaga n°" + i + " : " + jsonArray.getJSONObject(i).getString("webformatURL"));
                            alpagasPicturesUrls.add(jsonArray.getJSONObject(i).getString("webformatURL"));
                            System.out.println("nombre d'urls d'alpagas = " + alpagasPicturesUrls.size());
                        }
                        //picturesUrls = jsonArray;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!lamasPicturesUrls.isEmpty() && !alpagasPicturesUrls.isEmpty()) {
                        generateQuestionOnline();
                    }
                }
            };

            getPicturesOnline(lamaString, responseListenerGetRequestLamas);
            getPicturesOnline(alpagaString, responseListenerGetRequestAlpagas);
        } else {
            System.out.println("on est offline");
            getPicturesOffline(R.string.lama, lamasPicturesOffline);
            getPicturesOffline(R.string.alpaga, alpagasPicturesOffline);
            generateQuestionOffline();
        }
    }



    @Override
    public void onClick(View v) {
        Animal answer = (Animal) v.getTag();
        if (answer == question.getAnswer()){
            Toast.makeText(this, "Verdadero!", Toast.LENGTH_SHORT).show();
            if (mDeguelloMusic.isPlaying()){
                mDeguelloMusic.pause();
            }
            if (!mJarabeMusic.isPlaying()) {
                mJarabeMusic.start();
            }
        }
        else{
            Toast.makeText(this, "No es un "+answer.getAnimalString()+" sino un "+question.getAnswer().getAnimalString()+"!", Toast.LENGTH_SHORT).show();
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
                if (ONLINE){
                    generateQuestionOnline();
                }
                else{
                    generateQuestionOffline();
                }
            }
        }, 2000);
    }

    public void generateQuestionOffline() {
        Random random = new Random();
        int randomNumberAnimal = random.nextInt(2);
        int randomNumberPicture;
        int pic;
        if (randomNumberAnimal == 0) {
            question = new Question(Animal.LAMA);
            randomNumberPicture = random.nextInt(lamasPicturesOffline.length());
            pic = lamasPicturesOffline.getResourceId(randomNumberPicture, 0);
        } else {
            question = new Question(Animal.ALPAGA);
            randomNumberPicture = random.nextInt(alpagasPicturesOffline.length());
            pic = alpagasPicturesOffline.getResourceId(randomNumberPicture, 0);
        }
        mPicture.setImageResource(pic);
    }

    public void generateQuestionOnline() {
        String url = "";
        Random random = new Random();
        int randomNumberAnimal = random.nextInt(2);
        if (randomNumberAnimal == 0) {
            question = new Question(Animal.LAMA);
            int randomNumberPicture;
            System.out.println("yahouuuuuuuuuuuuuuuuuuuuuuuu     "+lamasPicturesUrls.size());
            randomNumberPicture = random.nextInt(lamasPicturesUrls.size());
            url = (String) lamasPicturesUrls.get(randomNumberPicture);
        } else {
            question = new Question(Animal.ALPAGA);
            int randomNumberPicture;
            System.out.println("yahouuuuuuuuuuuuuuuuuuuuuuuu    "+alpagasPicturesUrls.size());
            randomNumberPicture = random.nextInt(alpagasPicturesUrls.size());
            url = (String) alpagasPicturesUrls.get(randomNumberPicture);
        }
        mQuestionUrl.setText(url);
        Glide.with(this).load(url).into(mPicture);
    }

    private void getPicturesOffline(int animal, TypedArray typedArray){
        typedArray =  getResources().obtainTypedArray(animal);
    }

    private void getPicturesOnline(String animal, Response.Listener<JSONObject> responseListener){
        String url = "https://pixabay.com/api/?key="+PIXABAY_KEY+"&q="+animal.replace(" ","+")+"&image_type=photo";
        System.out.println("URL GET : "+url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("yahou");
                error.printStackTrace();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}