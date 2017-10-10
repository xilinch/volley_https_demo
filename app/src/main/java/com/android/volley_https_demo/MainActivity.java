package com.android.volley_https_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestTest();
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

    }

    private void requestTest(){
        //http://blog.csdn.net/xujiayin/article/details/51697355
        String url = "https://www.baidu.com";
        Map<String,String> parms = new HashMap<>();
        RequestUtil.httpGet(this, url, parms, new VolleyListener<String>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((TextView)findViewById(R.id.tv)).setText(error.getMessage());
            }

            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response,Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.tv)).setText(response);
            }
        });
    }
}
