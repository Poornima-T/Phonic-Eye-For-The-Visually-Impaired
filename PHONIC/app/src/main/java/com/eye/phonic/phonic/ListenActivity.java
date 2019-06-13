package com.eye.phonic.phonic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.hardware.Camera;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class ListenActivity extends AppCompatActivity {

    ImageView mImageView;
    TextToSpeech textToSpeech;
    private Camera mCamera;
    private CameraPreview mPreview;
    String URL ="http://192.168.43.61:5000/";
    //String currentPhotoPath;

    //static final int REQUEST_TAKE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        mImageView = findViewById(R.id.imageView);
        speakObjects("Camera Opened. Press the Volume Down Button to capture an image.",1,0,0);

        if(checkCameraHardware(this)) {
            mCamera = getCameraInstance();

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }

    }





    public void speakObjects(final String str, final int repeat, final int returnHome, final int objects){

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


            @Override
            public void onInit(int status) {
                String data = str;
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.UK);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(objects==1)
                {
                    for(int i = 0;i<repeat;i++) {
                    textToSpeech.speak("The Objects in front of you are", TextToSpeech.QUEUE_ADD, null);

                    textToSpeech.speak(data, TextToSpeech.QUEUE_ADD, null);
                }
                }else{
                    textToSpeech.speak(data, TextToSpeech.QUEUE_ADD, null);
                }

                if(returnHome == 1)
                {
                    Intent returnIntent = new Intent(ListenActivity.this ,ReturnActivity.class);
                    startActivity(returnIntent);
                }


            }


        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){

            speakObjects("Picture Taken. Sending the picture now. Please wait for the results.",1,0,0);
            mCamera.takePicture(null, null, mPicture);


        }
        return true;
    }
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            final String imageString = Base64.encodeToString(data, Base64.DEFAULT);
            Log.println(Log.INFO,"ImgStr",imageString);
            //sending image to server
            StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    Toast.makeText(ListenActivity.this, s, Toast.LENGTH_LONG).show();
                    speakObjects(s,2,1,1);

                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    speakObjects("Sorry, there seems to be a problem with the network.",1,1,0);
                    Toast.makeText(ListenActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                }
            }) {
                //adding parameters to send
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("image", imageString);
                    return parameters;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    6000,
                    2,
                    1));


            RequestQueue rQueue = Volley.newRequestQueue(ListenActivity.this);
            rQueue.add(request);



            releaseCamera();
        }
    };



    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

}
