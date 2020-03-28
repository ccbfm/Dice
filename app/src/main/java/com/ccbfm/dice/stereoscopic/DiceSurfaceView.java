package com.ccbfm.dice.stereoscopic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class DiceSurfaceView extends GLSurfaceView {

    private DiceRenderer mDiceRenderer;

    public DiceSurfaceView(Context context, DiceActivity.ResourceData resourceData) {
        super(context);
        init(resourceData);
    }

    public void init(DiceActivity.ResourceData resourceData) {
        //设置使用opengl版本
        setEGLContextClientVersion(2);
        //创建并设置场景渲染器
        mDiceRenderer = new DiceRenderer(getContext(), resourceData);
        setRenderer(mDiceRenderer);
        //设置渲染模式为主动渲染
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDiceRenderer != null) {
            return mDiceRenderer.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }


}
