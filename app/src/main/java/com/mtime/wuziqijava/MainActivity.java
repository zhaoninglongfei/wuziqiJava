package com.mtime.wuziqijava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button restart;//重新开始
    private Button regret;//悔棋
    private WuziqiPanel wuziqiPanel;//五子棋view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        restart = (Button) findViewById(R.id.restart);
        regret = (Button) findViewById(R.id.regret);
        wuziqiPanel = (WuziqiPanel) findViewById(R.id.wuziqi);

        restart.setOnClickListener(this);
        regret.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.restart) {
            wuziqiPanel.restart();
        } else if (id == R.id.regret) {
            wuziqiPanel.regret();
        }
    }
}
