package com.ccbfm.dice;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ccbfm.screen.adapter.ScreenAdapter;
import com.iigo.library.DiceView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCES_DICE_NUMBER = "preferences_dice_number";
    private static final String KEY_DICE_NUMBER = "key_dice_number";
    private static final String KEY_DICE_NUMBER_RECORD = "key_dice_number_record";

    private SharedPreferences mPreferences;
    private static boolean sAnimation = false;
    private boolean mStart = false;
    private int mStartCount, mNumRecord = 0;
    private LinearLayout mDiceViewContent;
    private TextView mDiceRecord;
    private Handler mHandler;

    @Override@ScreenAdapter
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = getSharedPreferences(PREFERENCES_DICE_NUMBER, MODE_PRIVATE);

        mDiceViewContent = findViewById(R.id.dice_view_content);
        mHandler = new DiceHandler(mDiceViewContent);
        mDiceRecord = findViewById(R.id.dice_record);

        final Spinner spinner = findViewById(R.id.dice_spinner);
        final String[] numbers = getResources().getStringArray(R.array.spinner_number);
        spinner.setAdapter(new DiceSpinnerAdapter(this, numbers));
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_DOWN && sAnimation;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.edit().putInt(KEY_DICE_NUMBER, position).apply();
                changeDiceView(mDiceViewContent, (position + 1), mDiceRecord, mStart, mStartCount, mNumRecord);
                mStart = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int initNum = mPreferences.getInt(KEY_DICE_NUMBER, 0);
        spinner.setSelection(initNum);

        final Spinner spinner_record = findViewById(R.id.dice_spinner_record);
        final String[] numbers_record = getResources().getStringArray(R.array.spinner_number_record);
        spinner_record.setAdapter(new DiceSpinnerAdapter(this, numbers_record));
        spinner_record.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNumRecord = position;
                mStartCount = 0;
                mStart = false;
                mDiceRecord.setText("");
                mPreferences.edit().putInt(KEY_DICE_NUMBER_RECORD, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int initNumRecord = mPreferences.getInt(KEY_DICE_NUMBER_RECORD, 0);
        spinner_record.setSelection(initNumRecord);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP && !sAnimation) {
            startAnimation();
            sAnimation = true;
        }
        return super.onTouchEvent(event);
    }

    private static void changeDiceView(ViewGroup diceViewContent, int number,
                                       TextView diceRecord, boolean start,
                                       int startCount, int numRecord) {

        recordViewNimber(diceViewContent, diceRecord, start, startCount, numRecord);

        diceViewContent.removeAllViews();
        for (int i = 0; i < number; i++) {
            LayoutInflater.from(diceViewContent.getContext()).inflate(R.layout.layout_dice_view, diceViewContent);
            DiceView diceView = (DiceView) diceViewContent.getChildAt(i);
            diceView.setNumber(1); //设置骰子点数，必须为1-6
            diceView.setPointColor(Color.BLACK); //设置点的颜色
            diceView.setBgColor(Color.WHITE); //设置背景颜色
            diceView.setBorderColor(Color.RED); //设置边界颜色
        }

    }

    private void startAnimation() {
        recordViewNimber(mDiceViewContent, mDiceRecord, mStart, mStartCount, mNumRecord);
        mHandler.sendEmptyMessage(1);
        mStart = true;
        mStartCount += 1;
    }

    private static void recordViewNimber(ViewGroup diceViewContent, TextView diceRecord,
                                         boolean start, int startCount, int numRecord){
        if(start && numRecord > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int index = ((startCount - 1) % numRecord) + 1;
            stringBuilder.append("[").append(index).append("]=>");
            String result = getDiceViewResult(diceViewContent, stringBuilder);
            diceRecord.append(result + "\n");
        }
    }

    private static String getDiceViewResult(ViewGroup diceViewContent, StringBuilder stringBuilder) {
        final int count = diceViewContent.getChildCount();
        for (int i = 0; i < count; i++) {
            DiceView diceView = (DiceView) diceViewContent.getChildAt(i);
            stringBuilder.append(diceView.getNumber());
            if(i < count - 1){
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private static int animationDiceView(ViewGroup diceViewContent, int skipIndex) {
        final int count = diceViewContent.getChildCount();
        if (count <= skipIndex) {
            return count;
        }
        Random random = new Random();
        int seed = random.nextInt(9999);
        for (int i = 0; i < count; i++) {
            if (i < skipIndex) {
                continue;
            }
            DiceView diceView = (DiceView) diceViewContent.getChildAt(i);
            int number = new Random(seed + i).nextInt(6) + 1;
            diceView.setNumber(number); //设置骰子点数，必须为1-6
        }
        return count;
    }

    private static class DiceHandler extends Handler {
        private ViewGroup mViewGroup;
        private int mExecutionTime = 0;
        private int mSkipIndex = 0;

        /*public*/ DiceHandler(ViewGroup viewGroup) {
            mViewGroup = viewGroup;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mExecutionTime = 0;
                    mSkipIndex = -1;
                    sendEmptyMessage(2);
                    break;
                case 2:
                    int delayTime;
                    if (mExecutionTime > 1000) {
                        delayTime = 200;
                        mSkipIndex += 1;
                        mExecutionTime += delayTime;
                    } else {
                        delayTime = 100;
                        mExecutionTime += delayTime;
                    }
                    int count = animationDiceView(mViewGroup, mSkipIndex);
                    if (count <= mSkipIndex) {
                        sAnimation = false;
                    } else {
                        sendEmptyMessageDelayed(2, delayTime);
                    }
                    break;
            }
        }
    }
}
