package com.zorro.mediademo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DynamicLayoutActivity extends AppCompatActivity {

    private RelativeLayout rlay_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_layout);
        rlay_root = findViewById(R.id.rlay_root);
        addView();
        addViewTwo();
    }

    @SuppressLint("ResourceType")
    private void addViewTwo() {
        // 将Button1 加入到RelativeLayout 中
        Button btn_r1 = new Button(this);
        btn_r1.setText("取消");//设置显示的字符
        btn_r1.setId(24);
        rlay_root.addView(btn_r1);

        // 将Button2 加入到RelativeLayout 中
        Button btn_r2 = new Button(this);
        btn_r2.setText("确定");//设置显示的字符
        btn_r2.setId(25);
        rlay_root.addView(btn_r2);
        // 设置RelativeLayout布局的宽高
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 100;
        lp.leftMargin = 100;
        lp.width = 100;
        lp.height = 100;
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        btn_r1.setLayoutParams(lp); ////设置按钮的布局属性
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 100;
        lp.leftMargin = 100;
        lp.addRule(RelativeLayout.RIGHT_OF, btn_r1.getId());
        btn_r2.setLayoutParams(lp); ////设置按钮的布局属性
    }

    private void addView() {
        TextView textView = new TextView(this);
        textView.setText("测试一...");
        textView.setTextColor(getResources().getColor(R.color.purple_200));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);//获得对应父窗体类型的LayoutParams
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE); //紧贴父控件的右边边缘
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);//紧贴父控件的顶部边缘
        rlay_root.addView(textView, lp);
    }
}