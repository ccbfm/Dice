package com.ccbfm.dice.stereoscopic;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.ccbfm.dice.LogTools;
import com.ccbfm.dice.R;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

public class DiceRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "DiceRenderer";
    private final static float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private Environment mEnvironment;
    private DiscreteDynamicsWorld mDynamicsWorld;
    private List<Entity> mEntities = new LinkedList<>();
    private Context mContext;
    private DiceActivity.ResourceData mResourceData;

    //目标点
    private float mTargetElevation = Environment.TARGET_ELEVATION;//仰角
    private float mTargetAzimuth = Environment.TARGET_AZIMUTH;//方位角

    //上次的触控位置Y坐标
    private float mPreviousY;
    //上次的触控位置X坐标
    private float mPreviousX;

    public DiceRenderer(Context context, DiceActivity.ResourceData resourceData) {
        mContext = context;
        mResourceData = resourceData;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogTools.d(TAG, "onSurfaceCreated", "-start-");
        //设置屏幕背景色RGBA
        GLES20.glClearColor(0.3f, 0.3f, 0.3f, 0.5f);
        //打开深度检测
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //关闭背面剪裁
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        loadResource(mResourceData);

        mEnvironment = new Environment();

        float[] currentMatrix = mEnvironment.getCurrentMatrix();
        for (Entity entity : mEntities) {
            entity.init(currentMatrix);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogTools.d(TAG, "onSurfaceChanged", "-start-");
        //设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);
        //计算GLSurfaceView的宽高比
        float ratio = (float) height / width;
        //调用此方法计算产生透视投影矩阵
        mEnvironment.setProjectionFrustum(-1, 1, -ratio, ratio, 2, 100);

        //设置camera位置,在上面往下面看
        mEnvironment.initCamera(0f, 0f, 0, 1, 0);
        mEnvironment.initLightLocation();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除深度缓冲与颜色缓冲
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        //绘制阴影开启混合
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glCullFace(GLES20.GL_BACK);

        if (mEntities != null) {
            Environment environment = mEnvironment;
            FloatBuffer cameraBuffer = environment.getCameraFB();
            FloatBuffer lightBuffer = environment.getLightPositionFB();
            float[] projectionMatrix = environment.getProjectionMatrix();
            float[] vMatrix = environment.getVMatrix();
            mDynamicsWorld.stepSimulation(1.0f / 60, 5);
            for (Entity entity : mEntities) {
                entity.startDraw();
                entity.draw(cameraBuffer, lightBuffer, projectionMatrix, vMatrix);
                entity.drawShadow(cameraBuffer, lightBuffer, projectionMatrix, vMatrix);
            }
        }
    }

    private static final int MAX_CLICK_TIME = 1000;
    private long mStartTime;

    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;
                float dx = x - mPreviousX;
                //设置沿x轴旋转角度
                mTargetAzimuth += dx * TOUCH_SCALE_FACTOR;
                //设置沿z轴旋转角度
                mTargetElevation += dy * TOUCH_SCALE_FACTOR;
                //将仰角限制在5～90度范围内
                mTargetElevation = Math.max(mTargetElevation, 5);
                mTargetElevation = Math.min(mTargetElevation, 90);
                //设置摄像机的位置
                mEnvironment.initCamera(mTargetElevation, mTargetAzimuth, 0, 1, 0);
                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                if (endTime - mStartTime < MAX_CLICK_TIME) {
                    for (Entity entity : mEntities) {
                        entity.setVelocity();
                    }
                }
                break;
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }

    private void loadResource(DiceActivity.ResourceData resourceData) {
        Context context = mContext;
        int diceSize = resourceData.number;
        mDynamicsWorld = SubstanceGenerator.createWorld();

        float[][] diceInfo = resourceData.diceInfo;//LoadDiceTools.loadDiceObj("dice_3d.obj", context.getResources());
        int diceProgramId = GLTools.buildProgram(context, R.raw.dice_scene_vertex, R.raw.dice_scene_frag);
        int diceTextureId = GLTools.loadTexture(context, R.mipmap.dice_texture, new int[2]);
        int floorProgramId = GLTools.buildProgram(context, R.raw.dice_bg_vertex, R.raw.dice_bg_fragment);
        int floorTextureId = GLTools.loadTexture(context, R.mipmap.dice_container_marble, new int[2]);
        LogTools.i(TAG, "loadResource", "diceProgramId=" + diceProgramId + ",diceTextureId=" + diceTextureId);
        LogTools.i(TAG, "loadResource", "floorProgramId=" + floorProgramId + ",floorTextureId=" + floorTextureId);
        Floor floor = SubstanceGenerator.createFloor(floorProgramId, floorTextureId);
        if (mEntities == null) {
            mEntities = new LinkedList<>();
        } else {
            mEntities.clear();
        }
        mEntities.add(floor);
        if (diceInfo != null) {
            //骰子大小
            BoxShape boxShape = new BoxShape(new Vector3f(1.1f, 1.1f, 1.1f));
            for (int i = 1; i <= diceSize; i++) {
                RigidBody rigidBody = SubstanceGenerator.createDynamicRigidBody(boxShape, mDynamicsWorld, 1, i * 2, 3, 0);
                Dice dice = SubstanceGenerator.createDice(rigidBody, diceInfo, diceProgramId, diceTextureId);
                rigidBody.forceActivationState(RigidBody.WANTS_DEACTIVATION);
                mEntities.add(dice);
            }
        }
    }

}
