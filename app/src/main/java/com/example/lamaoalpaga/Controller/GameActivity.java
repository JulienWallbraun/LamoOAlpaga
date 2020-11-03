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
import com.example.lamaoalpaga.Model.Animal;
import com.example.lamaoalpaga.Model.Question;
import com.example.lamaoalpaga.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.*;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mQuestionView;
    private ImageView mPicture;
    private Button mLamaButton;
    private Button mAlpagaButton;
    private MediaPlayer mJarabeMusic;
    private MediaPlayer mDeguelloMusic;
    private static final String PIXABAY_KEY = "18957740-cc52fa8c7975fbd4a749df833";

    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mQuestionView = findViewById(R.id.activity_game_question_text);
        mPicture = findViewById(R.id.activity_game_picture_text);
        mLamaButton = findViewById(R.id.activity_game_answerLama_btn);
        mAlpagaButton = findViewById(R.id.activity_game_answerAlpaga_btn);

        mLamaButton.setOnClickListener(this);
        mAlpagaButton.setOnClickListener(this);

        mLamaButton.setTag(Animal.LAMA);
        mAlpagaButton.setTag(Animal.ALPAGA);

        mJarabeMusic = MediaPlayer.create(getApplicationContext(), R.raw.jarabe);
        mDeguelloMusic = MediaPlayer.create(getApplicationContext(), R.raw.deguello);

        generateQuestion();
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
                generateQuestion();
            }
        }, 2000);
    }

    public void generateQuestion() {
        Random random = new Random();
        int randomNumberAnimal = random.nextInt(2);
        int randomNumberPicture;
        String AnimalString="";
        int pic;
        getPictures("lama");
        if (randomNumberAnimal == 0) {
            question = new Question(Animal.LAMA);
            AnimalString = Animal.LAMA.getAnimalString();
            TypedArray pics =  getResources().obtainTypedArray(R.array.lamaPictures);
            randomNumberPicture = random.nextInt(pics.length());
            pic = pics.getResourceId(randomNumberPicture, 0);
            //mPicture.setImageResource(R.drawable.lama);

        } else {
            question = new Question(Animal.ALPAGA);
            AnimalString = Animal.ALPAGA.getAnimalString();
            TypedArray pics =  getResources().obtainTypedArray(R.array.alpagaPictures);
            randomNumberPicture = random.nextInt(pics.length());
            pic = pics.getResourceId(randomNumberPicture, 0);
            //mPicture.setImageResource(R.drawable.alpaga);
        }
        mPicture.setImageResource(pic);
        //mPicture.setImageResource(getResources().getIdentifier("lama5","drawable", getPackageName()));
        //mPicture.setImageResource(getResources().getIdentifier(AnimalString+Integer.toString(randomNumberPicture),"drawable", getPackageName()));
        //Glide.with(this).load(R.drawable.logo).into(mPicture);
    }

    private void getPictures(String animal){
        String url = "https://pixabay.com/api/?key="+PIXABAY_KEY+"&q="+animal.replace(" ","+")+"&image_type=photo";
        System.out.println("URL GET : "+url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("youpi");
                /*try {
                    JSONArray jsonArray = response.getJSONArray("hits");
                    System.out.println("try réussi");
                    for (int i=0; i<jsonArray.length(); i++){
                        System.out.println("url de l'image n°"+i);//+" : "+jsonArray.getJSONObject(i).getString("webformatURL"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("try raté on est dans le catch");
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("yahou");
                error.printStackTrace();
            }
        });
    }

}