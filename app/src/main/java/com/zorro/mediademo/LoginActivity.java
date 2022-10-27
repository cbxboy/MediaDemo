package com.zorro.mediademo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_num,edit_id;
    private Button btn_login;
    private String device_id,store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_num = findViewById(R.id.edit_num);
        edit_id = findViewById(R.id.edit_id);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edit_num.getText().toString()) && !TextUtils.isEmpty(edit_id.getText().toString())) {
                    SharePreferenceUtils.putString(LoginActivity.this,"device_id",edit_num.getText().toString());
                    SharePreferenceUtils.putString(LoginActivity.this,"store_id",edit_id.getText().toString());
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this,"请输入设备号或ID",Toast.LENGTH_SHORT).show();
                }
            }
        });
        device_id = SharePreferenceUtils.getString(this,"device_id");
        store_id = SharePreferenceUtils.getString(this,"store_id");
        if (!TextUtils.isEmpty(device_id) && !TextUtils.isEmpty(store_id)) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }
}