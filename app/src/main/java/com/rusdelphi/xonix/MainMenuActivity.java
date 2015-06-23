package com.rusdelphi.xonix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainMenuActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void onStartGameClick(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }

}
