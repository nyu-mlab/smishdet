package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        String resp = getIntent().getStringExtra("key");
        resultTextView.setText(resp);
        //Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(intent);
    }
}