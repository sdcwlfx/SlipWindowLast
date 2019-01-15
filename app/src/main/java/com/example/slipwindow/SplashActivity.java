package com.example.slipwindow;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {
    private ImageView imageView;
    /**
     * Animation实现UI界面动画效果的API
     */
    private Animation mFadeIn;
    private Animation mFadeInScale;//缩放效果
    private Animation mFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//隐藏标题栏
            actionBar.hide();
        }
        imageView=(ImageView)findViewById(R.id.splash_image);
        int index=new Random().nextInt(6);
        if(index==1){
            imageView.setImageResource(R.drawable.entrance2);
        }else if(index==2){
            imageView.setImageResource(R.drawable.entrance3);
        }else if(index==3){
            imageView.setImageResource(R.drawable.start1);
        }else if(index==4){
            imageView.setImageResource(R.drawable.start2);
        }else if(index==5){
            imageView.setImageResource(R.drawable.start3);
        }else{
            imageView.setImageResource(R.drawable.start4);
        }
        initAnim();
        setListener();

    }

    private void initAnim(){
        mFadeIn=AnimationUtils.loadAnimation(SplashActivity.this,R.anim.welcome_fade_in);
        mFadeIn.setDuration(500);//动画持续时间
        mFadeInScale = AnimationUtils.loadAnimation(this,
                R.anim.welcome_fade_in_scale);
        mFadeInScale.setDuration(2000);
        mFadeOut = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_out);
        mFadeOut.setDuration(500);
        imageView.startAnimation(mFadeIn);
    }


    public void setListener(){
        mFadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(mFadeInScale);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFadeInScale.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
             //   imageView.startAnimation(mFadeOut);
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFadeOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
             //   Intent intent=new Intent(SplashActivity.this,MainActivity.class);
              //  startActivity(intent);
              //  finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



}
