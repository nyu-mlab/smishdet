package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listViewData;
    ArrayAdapter<String> adapter;
    ArrayList<String> smsList;
    private ProgressBar loadingPB;
    String messages = "";
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED );
        Uri inboxUri = Uri.parse("content://sms");
        smsList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(inboxUri,null,null,null,null);
        while(cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
            smsList.add("Number: "+number+ "\n" + "Body: "+body);
        }
        cursor.close();

        listViewData = findViewById(R.id.listView_data);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, smsList);
        listViewData.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_done){
            String itemSelected = "Selected items: \n";
            for(int i =0; i<listViewData.getCount();i++){
                if(listViewData.isItemChecked(i)){
                    String itemMsg = listViewData.getItemAtPosition(i) + "";
                    String[] itemText = itemMsg.split("Body:", 2);
                    itemSelected += listViewData.getItemAtPosition(i) + "\n";
                    messages += itemText[1]+ "; ";

                }
            }
            messages = messages.substring(0, messages.length()-1);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            // url to post our data
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("messages", messages);
            String url = "http://161.35.150.122/smishdet/";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //resultTextView.setText("String Response : "+ response.toString());
                            String resp = response.toString();
                            String res = "";
                            try {
                                JSONObject obj = response.getJSONObject("results");
                                String message_list[] = messages.split("; ");
                                for(int cnt=0; cnt<message_list.length; cnt++){
                                    String url_result = obj.getJSONObject(message_list[cnt]).toString();
                                    String num = Integer.toString(cnt+1);
                                    res = res +num+ ") The results for Message : " + message_list[cnt] + "are: \n" + url_result+ "\n\n\n";
                                }
                                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                                String r = obj.toString();
                                res = res.replace("\\/","/");
                                i.putExtra("key",res);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //resultTextView.setText("Error getting response" + error.getMessage());
                    String resp = error.getMessage();
                    Intent i = new Intent(MainActivity.this, ResultActivity.class);
                    i.putExtra("key",resp);
                    startActivity(i);
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> headers=new HashMap<String,String>();
                    headers.put("Content-Type","application/json");
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
            Toast.makeText(this, messages, Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);

    }

}