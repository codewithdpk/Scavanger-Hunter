package com.deepak.scavengerhunter.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.deepak.scavengerhunter.APIs.AppController;
import com.deepak.scavengerhunter.APIs.EndPoints;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.classes.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LearnMoreAboutPostActivity extends AppCompatActivity {

    TextView txt_post_name;
    ImageView img_post_image;
    TextView txt_post_details;
    Button btn_take_quiz;
    ImageView ic_speaker_on;
    ImageView ic_speaker_mute;
    private ProgressDialog progressDialog;
    private MediaPlayer mediaPlayer;
    private String TITLE = "my-audio-file";
    View rootView;
    String audioUrl;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more_about_post);
        init();


        // Get hunt & post details
        JSONObject params = new JSONObject();
        try {
            progressDialog.show();
            params.put("hunt_id", getIntent().getStringExtra("hunt_id"));
            params.put("post_id", getIntent().getStringExtra("post_id"));
            getPostAndHuntDetails(params);
            progressDialog.dismiss();




        } catch (JSONException e) {
            e.printStackTrace();
        }

        ic_speaker_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.createToast(LearnMoreAboutPostActivity.this, rootView, "Playing");
                ic_speaker_mute.setVisibility(View.VISIBLE);
                ic_speaker_on.setVisibility(View.GONE);

                //mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/Folder/" + TITLE + ".mp3");
                //mediaPlayer.prepare();
                mediaPlayer = MediaPlayer.create(LearnMoreAboutPostActivity.this, Uri.parse(audioUrl));
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

                mediaPlayer.start();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mp.start();
//                    }
//                });
            }
        });

        ic_speaker_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.createToast(LearnMoreAboutPostActivity.this, rootView, "Stopped");

                ic_speaker_on.setVisibility(View.VISIBLE);
                ic_speaker_mute.setVisibility(View.GONE);


                //mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/Folder/" + TITLE + ".mp3");
                mediaPlayer.stop();
            }
        });

    }

    private void init() {

        txt_post_details = findViewById(R.id.txt_post_details);
        img_post_image = findViewById(R.id.img_post_image);
        txt_post_name = findViewById(R.id.tv_post_name);
        btn_take_quiz = findViewById(R.id.btn_take_quiz);
        ic_speaker_mute = findViewById(R.id.ic_speaker_mute);
        ic_speaker_on = findViewById(R.id.ic_speaker_on);
        rootView = getWindow().getDecorView().getRootView();
        ic_speaker_on.setClickable(false);
        progressBar();
    }

    private void getPostAndHuntDetails(JSONObject params) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_POST_HUNT_DETAILS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //progressDialog.dismiss();
                try {
                    JSONObject postDetails = response.getJSONObject("post");
                    JSONObject huntDetails = response.getJSONObject("hunt");
                    //downLoadMediaFiles(postDetails.getString("voice_url"));
                    audioUrl = postDetails.getString("voice_url");

                    ic_speaker_on.setClickable(true);

                    Glide.with(LearnMoreAboutPostActivity.this)
                            .load(postDetails.getString("post_image"))
                            .into(img_post_image);
                    txt_post_details.setText(postDetails.getString("information"));
                    txt_post_name.setText("Explation ( " + postDetails.getString("post_name") + " )");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                Utils.createToast(LearnMoreAboutPostActivity.this,rootView,error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(request);

    }


    private void progressBar() {
        progressDialog = new ProgressDialog(LearnMoreAboutPostActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void downLoadMediaFiles(String DOWNLOAD_URL) {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root + "/Folder");
        if (!dir.exists()) {
            dir.mkdirs();

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(DOWNLOAD_URL);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false);
            request.setTitle(TITLE);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, TITLE);
            request.allowScanningByMediaScanner();
        }
    }
}