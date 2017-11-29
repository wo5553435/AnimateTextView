package com.example.sinner.animatetextview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chou.view.AnimateTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(final View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int num= (int) (Math.random()*1000*Math.random());
                AnimateTextView av=((com.chou.view.AnimateTextView)findViewById(R.id.animateTextView1));
                av.setEndCount(num);
                av.StartCount();
              //  ((com.chou.view.AnimateTextView) findViewById(R.id.tv_text)).setMa(num, false, 2);
            }
        }, 500);

        ((com.chou.view.AnimateTextView)findViewById(R.id.animateTextView1)).StartCount();
        ((com.chou.view.AnimateTextView)findViewById(R.id.animateTextView2)).StartCount();
        ((com.chou.view.AnimateTextView)findViewById(R.id.animateTextView3)).StartCount();
    }
}
