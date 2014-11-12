package com.nasko.naz.translation;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {

    EditText translateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onTranslateClick(View view) {
        InputMethodManager inputMethod = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        translateEditText = (EditText) findViewById(R.id.editText);

        if (!isEmpty(translateEditText)) {
            Toast.makeText(this, "Getting Translation", Toast.LENGTH_LONG)
                    .show();

            new SaveTheFeed().execute();
        } else {
            Toast.makeText(this, "Enter something to translate", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    class SaveTheFeed extends AsyncTask<Void, Void, Void> {

        String jsonString = "";
        String result = "";


        @Override
        protected Void doInBackground(Void... voids) {

            EditText translateEditText = (EditText) findViewById(R.id.editText);
            String wordsToTranslate = translateEditText.getText().toString();
            wordsToTranslate = wordsToTranslate.replace(" ", "+");

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost("http://newjustin.com/translateit.php?action=translations&english_words=" + wordsToTranslate);
            httpPost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;

            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                        "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                jsonString = sb.toString();
                JSONObject jObject = new JSONObject(jsonString.toString());

                JSONArray jArray = jObject.getJSONArray("translations");

                outputTranslations(jArray);


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            TextView translationTextView = (TextView) findViewById(R.id.textView);
            translationTextView.setText(result);

        }

        protected void outputTranslations(JSONArray jsonArray) {
            String[] languages = {"arabic", "chinese", "danish", "dutch",
                    "french", "german", "italian", "portuguese", "russian",
                    "spanish"};
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject translationObject =
                            jsonArray.getJSONObject(i);
                    result = result + languages[i] + "  : " +
                            translationObject.getString(languages[i]) +
                            "\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    protected boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
}
