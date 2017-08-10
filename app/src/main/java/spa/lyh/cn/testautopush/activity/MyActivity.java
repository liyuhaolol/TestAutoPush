package spa.lyh.cn.testautopush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import spa.lyh.cn.testautopush.R;

/**
 * Created by liyuhao on 2017/8/4.
 */

public class MyActivity extends AppCompatActivity{
    TextView my_text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        my_text = (TextView) findViewById(R.id.my_text);
        Intent i = getIntent();
        my_text.setText(i.getStringExtra("msg"));

    }
}
