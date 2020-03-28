package com.ccbfm.dice.stereoscopic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ccbfm.dice.LogTools;
import com.ccbfm.dice.R;
import com.ccbfm.dice.SPTools;
import com.ccbfm.dice.widget.ChooseGilrsProgressView;

import java.lang.ref.WeakReference;

public class DiceActivity extends AppCompatActivity {
    private static final String TAG = "DiceActivity";
    private DiceSurfaceView mDiceSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stereoscopic_dice);
        Intent intent = getIntent();
        final int number = intent.getIntExtra(SPTools.KEY_DICE_NUMBER, 1);
        final ViewGroup content = findViewById(R.id.dice_stereoscopic_content);
        final ChooseGilrsProgressView progressView = findViewById(R.id.dice_stereoscopic_progress);
        new LoadAsyncTask(this, new LoadAsyncTask.Callback() {

            @Override
            public void callbackStart(float maxProgress) {
                progressView.setMaxProgress(maxProgress);
            }

            @Override
            public void callbackResult(ResourceData data) {
                data.number = number;
                progressView.setVisibility(View.GONE);
                mDiceSurfaceView = new DiceSurfaceView(DiceActivity.this, data);
                content.addView(mDiceSurfaceView);
                mDiceSurfaceView.requestFocus();//获取焦点
                mDiceSurfaceView.setFocusableInTouchMode(true);
            }

            @Override
            public void callbackProgress(float progress) {
                LogTools.d(TAG, "callbackProgress", "progress=" + progress);
                progressView.setProgress(progress);
            }
        }).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDiceSurfaceView != null) {
            mDiceSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDiceSurfaceView != null) {
            mDiceSurfaceView.onPause();
        }
    }

    private static class LoadAsyncTask extends AsyncTask<Void, Integer, ResourceData> {
        private WeakReference<Context> mReference;
        private Callback mCallback;
        private static final float MAX_PROGRESS = 20f;

        public LoadAsyncTask(Context context, Callback callback) {
            mReference = new WeakReference<>(context);
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallback != null) {
                mCallback.callbackStart(MAX_PROGRESS);
            }
        }

        @Override
        protected ResourceData doInBackground(Void... voids) {
            Context context = mReference.get();
            ResourceData data = new ResourceData();
            if (context != null) {
                data.diceInfo = LoadDiceTools.loadDiceObj("dice_3d.obj", context.getResources());
            }
            publishProgress(1);
            try {
                for (int i = 2; i < MAX_PROGRESS + 2; i++) {
                    Thread.sleep(50);
                    publishProgress(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ResourceData resourceData) {
            super.onPostExecute(resourceData);
            mCallback.callbackResult(resourceData);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mCallback != null) {
                float progress = values[0];
                if (progress > MAX_PROGRESS) {
                    progress = MAX_PROGRESS;
                }
                mCallback.callbackProgress(progress);
            }
        }

        public interface Callback {
            void callbackStart(float maxProgress);

            void callbackResult(ResourceData data);

            void callbackProgress(float progress);
        }
    }

    static class ResourceData {
        float[][] diceInfo;
        int number;
    }
}
