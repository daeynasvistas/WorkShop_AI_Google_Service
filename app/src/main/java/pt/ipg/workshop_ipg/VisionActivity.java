package pt.ipg.workshop_ipg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class VisionActivity extends AppCompatActivity {

    private TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);

    }

    public final static int MY_REQUEST_CODE = 1;

    public void takePictureLabel(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap picture = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 90, byteStream);
            ((ImageView) findViewById(R.id.previewImage)).setImageBitmap(picture);

            String base64Data = Base64.encodeToString(byteStream.toByteArray(), Base64.URL_SAFE);

            boolean ocr = ((Switch) findViewById(R.id.switchOCR)).isChecked();
            if(ocr) {ocrObjects(base64Data);}
            else    {recognizeObjects(base64Data);}

        }
    }

    private void recognizeObjects(String base64Data) {

        String requestURL = getResources().getString(R.string.mURLimage) +
                getResources().getString(R.string.mykey);

        try {

            //"features": [
            JSONObject feature = new JSONObject();
            feature.put("type", "LABEL_DETECTION");
            JSONArray features = new JSONArray();
            features.put(feature);
            JSONObject imageContent = new JSONObject();
            imageContent.put("content", base64Data);


            // "requests": [
            JSONObject request = new JSONObject();
            request.put("image", imageContent);
            request.put("features", features);
            JSONArray requests = new JSONArray();
            requests.put(request);

            // fábrica de JSON para envio
            JSONObject postData = new JSONObject();
            postData.put("requests", requests);

            String json = postData.toString();

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), json);


            Request requestAPI = new Request.Builder()
                    .header("content-type", "application/json")
                    .url("https://vision.googleapis.com/v1/images:annotate?key=" + getResources().getString(R.string.mykey))
                    .post(body)
                    .build();


            mTextViewResult = findViewById(R.id.textView);
            OkHttpClient client = new OkHttpClient();

            client.newCall(requestAPI).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myReponse = response.body().string();
                        VisionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray labels = new JSONObject(myReponse)
                                            .getJSONArray("responses")
                                            .getJSONObject(0)
                                            .getJSONArray("labelAnnotations");

                                    String results = "";

                                    for (int i = 0; i < labels.length(); i++) {
                                        results = results +
                                                labels.getJSONObject(i).getString("description") +
                                                " (" + labels.getJSONObject(i).getString("score") + ") " +
                                                "\n";
                                    }

                                    ((TextView) findViewById(R.id.textView)).setText(results);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } // if statment
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ocrObjects(String base64Data) {

        String requestURL = getResources().getString(R.string.mURLimage) +
                getResources().getString(R.string.mykey);

        try {

            //"features": [
            JSONObject feature = new JSONObject();
            feature.put("type", "TEXT_DETECTION");
            JSONArray features = new JSONArray();
            features.put(feature);
            JSONObject imageContent = new JSONObject();
            imageContent.put("content", base64Data);


            // "requests": [
            JSONObject request = new JSONObject();
            request.put("image", imageContent);
            request.put("features", features);
            JSONArray requests = new JSONArray();
            requests.put(request);

            // fábrica de JSON para envio
            JSONObject postData = new JSONObject();
            postData.put("requests", requests);

            String json = postData.toString();

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), json);


            Request requestAPI = new Request.Builder()
                    .header("content-type", "application/json")
                    .url("https://vision.googleapis.com/v1/images:annotate?key=" + getResources().getString(R.string.mykey))
                    .post(body)
                    .build();


            mTextViewResult = findViewById(R.id.textView);
            OkHttpClient client = new OkHttpClient();

            client.newCall(requestAPI).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myReponse = response.body().string();
                        VisionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // ver JSON mais fácil-> http://jsonviewer.stack.hu/
                                    JSONArray labels = new JSONObject(myReponse)
                                            .getJSONArray("responses")
                                            .getJSONObject(0)
                                            .getJSONArray("textAnnotations");

                                    String results = "";
                                    results = results +
                                            labels.getJSONObject(0).getString("description") +
                                            "\n";
                                    ((TextView) findViewById(R.id.textView)).setText(results);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } // if statment
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void translateTo(View view) throws JSONException {

        String request = "https://translation.googleapis.com/language/translate/v2?key=AIzaSyBdJT3uN7Rw5HNINHunhAR7fFe3pz2Xj2A";

        String[] queries = ((TextView)findViewById(R.id.textView))
                .getText().toString().split("\n");

        MultipartBody.Builder multipart = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

         /*{
            'source': 'en',
            'target': 'pt',
            'q': 'Hello world',
            'q': 'My name is Daniel',
          }*/

    //    multipart.addFormDataPart("source","en");
        multipart.addFormDataPart("target","pt");
        for(String query:queries) {
            multipart.addFormDataPart("q",query);
        }

        RequestBody requestBody = multipart.build();

        Request requestAPI = new Request.Builder()
                .header("content-type", "application/json")
                .url(request)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(requestAPI).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {e.printStackTrace();}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myReponse = response.body().string();// JSON da google API
                VisionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Access the translations array
                            /*
                            { "data": { "translations": [
                                {
                                    "translatedText": "...."
                                },
                                {
                                    "translatedText": "...."
                                },
                                  ...
                                ] } }*/
                        try {
                            JSONArray translations = new JSONObject(myReponse)
                                    .getJSONObject("data")
                                    .getJSONArray("translations");

                              String result = "";
                            for(int i=0;i<translations.length();i++) {
                                result += translations.getJSONObject(i)
                                        .getString("translatedText") + "\n";
                            }

                            ((TextView)findViewById(R.id.textView)).setText(result);  // mostra array Traduções

                        }
                        catch (Exception e) { e.printStackTrace();}
                    }
                });

            //.-..... onResponse
            }

        });


    }

}