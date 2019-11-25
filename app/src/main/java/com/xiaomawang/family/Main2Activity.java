package com.xiaomawang.family;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/main/main2")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("back","asia");
            setResult(Activity.RESULT_OK, new Intent().putExtras(bundle));
            finish();
        });
    }
}
