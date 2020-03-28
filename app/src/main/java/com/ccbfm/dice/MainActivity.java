package com.ccbfm.dice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ccbfm.dice.flat.DiceSpinnerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner spinner = findViewById(R.id.dice_spinner);
        final String[] numbers = getResources().getStringArray(R.array.spinner_number);
        spinner.setAdapter(new DiceSpinnerAdapter(this, numbers));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPTools.putIntValue(SPTools.KEY_DICE_NUMBER, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int initNum = SPTools.getIntValue(SPTools.KEY_DICE_NUMBER);
        spinner.setSelection(initNum);
    }

    public void onClickModeFlat(View view){
        int initNum = SPTools.getIntValue(SPTools.KEY_DICE_NUMBER);
        Intent intent = new Intent(this, com.ccbfm.dice.flat.DiceActivity.class);
        intent.putExtra(SPTools.KEY_DICE_NUMBER, initNum + 1);
        startActivity(intent);
    }

    public void onClickModeStereoscopic(View view){
        int initNum = SPTools.getIntValue(SPTools.KEY_DICE_NUMBER);
        Intent intent = new Intent(this, com.ccbfm.dice.stereoscopic.DiceActivity.class);
        intent.putExtra(SPTools.KEY_DICE_NUMBER, initNum + 1);
        startActivity(intent);
    }
}
