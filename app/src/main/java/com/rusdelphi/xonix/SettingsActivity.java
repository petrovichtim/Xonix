package com.rusdelphi.xonix;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


public class SettingsActivity extends BaseActivity {
    TextView tv_game_speed;
    EditText et_number_lifes;
    int gameSpeed, numberLifes;
    Preference prefs;
    SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = new Preference(this);
        tv_game_speed = (TextView) findViewById(R.id.tv_game_speed);
        et_number_lifes = (EditText) findViewById(R.id.et_number_lifes);


        seekbar = (SeekBar) findViewById(R.id.seekBar);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gameSpeed = seekBar.getProgress() + 1;
                String info = getString(R.string.game_speed) + gameSpeed;
                tv_game_speed.setText(info);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameSpeed = prefs.getData(Preference.GAME_SPEED);
        numberLifes = prefs.getData(Preference.NUMBER_OF_LIFES);
        String info = getString(R.string.game_speed) + gameSpeed;
        tv_game_speed.setText(info);
        et_number_lifes.setText(String.valueOf(numberLifes));
        seekbar.setProgress(gameSpeed - 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.setData(Preference.GAME_SPEED, gameSpeed);
        prefs.setData(Preference.NUMBER_OF_LIFES, Integer.parseInt(et_number_lifes.getText().toString()));

    }
}
