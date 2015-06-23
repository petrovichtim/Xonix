package com.rusdelphi.xonix;

import android.os.Bundle;


public class GameActivity extends BaseActivity {

    private DrawView mDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawView = new DrawView(this);
        setContentView(mDrawView);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
