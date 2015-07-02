package com.rusdelphi.xonix;

import android.content.Intent;
import android.net.Uri;
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

    public void onOtherAppsClick(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Владимир Тимофеев")));
    }

    public void onSettingsClick(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

}
