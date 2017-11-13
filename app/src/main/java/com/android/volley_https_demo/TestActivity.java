package com.android.volley_https_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/9/20.
 */

public class TestActivity extends Activity{

    private ImageView iv;

    private UserLikeAnimationView userLikeAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        iv = (ImageView) findViewById(R.id.iv);
        userLikeAnimationView = (UserLikeAnimationView) findViewById(R.id.likeView);
        setListener();
    }

    private void setListener(){
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLikeAnimationView.addHeart();
            }
        });
        userLikeAnimationView.addHeart(600);
    }
}
