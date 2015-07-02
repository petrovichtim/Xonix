package com.rusdelphi.xonix;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


public class GameActivity extends BaseActivity {

    private static DrawView mDrawView;
    private static PopupWindow mPopupWindow;
    private static Context ctx;
    private static Resources res;
    private static Activity self;
    static int mSidePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        res = getResources();
        ctx = this;
        mDrawView = new DrawView(this);
        setContentView(mDrawView);
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int width = dimension.widthPixels;
        int height = dimension.heightPixels;
        if (width < height)
            mSidePopup = (int) (width * 0.8);
        else
            mSidePopup = (int) (height * 0.8);

    }

    static void showGameOver() {
        LayoutInflater layoutInflater
                = (LayoutInflater) ctx
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup, null);
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
        mPopupWindow = new PopupWindow(
                popupView,
                mSidePopup,
                mSidePopup);
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        RelativeLayout rl = (RelativeLayout) popupView.findViewById(R.id.PopUp);
        rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mPopupWindow.dismiss();
                self.finish();
            }
        });
        mPopupWindow.showAtLocation(mDrawView, Gravity.CENTER, 0, 0);
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
