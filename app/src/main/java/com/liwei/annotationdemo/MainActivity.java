package com.liwei.annotationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liwei.bindview_annotation.BindView;
import com.liwei.viewbinder.ViewBinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    public Button mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "注解测试", Toast.LENGTH_LONG).show();
            }
        });
    }
}
