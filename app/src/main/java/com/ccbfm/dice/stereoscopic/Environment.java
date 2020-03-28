package com.ccbfm.dice.stereoscopic;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;

public class Environment {

    //摄像机
    private FloatBuffer mCameraFB;
    //灯光位置
    private FloatBuffer mLightPositionFB;
    //4x4矩阵 投影用
    private float[] mProjectionMatrix = new float[16];
    //摄像机位置朝向9参数矩阵
    private float[] mVMatrix = new float[16];
    //当前变换矩阵
    private float[] mCurrentMatrix;

    //关于灯光的变量
    private static final float LIGHT_DISTANCE = 100f;
    private static final float LIGHT_ELEVATION = 50f;//灯光仰角
    private static final float LIGHT_AZIMUTH = -30f;//灯光的方位角

    //目标点
    private static final float TARGET_X = 0f;
    private static final float TARGET_Y = 0f;
    private static final float TARGET_Z = 0f;
    private static final float TARGET_DISTANCE = 30f;//摄像机和目标的距离
    public static final float TARGET_ELEVATION = 90f;//仰角
    public static final float TARGET_AZIMUTH = 0f;//方位角

    public Environment() {
        mCurrentMatrix = new float[16];
        Matrix.setRotateM(mCurrentMatrix, 0, 0, 1, 0, 0);
    }

    /**
     * 设置透视投影参数
     *
     * @param left   near面的left
     * @param right  near面的right
     * @param bottom near面的bottom
     * @param top    near面的top
     * @param near   near面距离
     * @param far    far面距离
     */
    public void setProjectionFrustum(float left, float right, float bottom,
                                     float top, float near, float far) {
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    /**
     * 设置摄像机
     *
     * @param upx 摄像机UP向量X分量
     * @param upy 摄像机UP向量Y分量
     * @param upz 摄像机UP向量Z分量
     */
    public void initCamera(float targetElevation, float targetAzimuth, float upx, float upy, float upz) {

        if (targetElevation == 0f) {
            targetElevation = TARGET_ELEVATION;
        }
        if (targetAzimuth == 0f) {
            targetAzimuth = TARGET_AZIMUTH;
        }
        //计算摄像机的位置
        double angleElevation = Math.toRadians(targetElevation);// 仰角（弧度）
        double angledAzimuth = Math.toRadians(targetAzimuth);// 方位角

        float cameraX = (float) (TARGET_X - TARGET_DISTANCE * Math.cos(angleElevation) * Math.sin(angledAzimuth));
        float cameraY = (float) (TARGET_Y + TARGET_DISTANCE * Math.sin(angleElevation));
        float cameraZ = (float) (TARGET_Z - TARGET_DISTANCE * Math.cos(angleElevation) * Math.cos(angledAzimuth));

        Matrix.setLookAtM(mVMatrix, 0,
                cameraX, cameraY, cameraZ,
                TARGET_X, TARGET_Y, TARGET_Z,
                upx, upy, upz);

        float[] cameraLocation = new float[3];//摄像机位置
        cameraLocation[0] = cameraX;
        cameraLocation[1] = cameraY;
        cameraLocation[2] = cameraZ;

        ByteBuffer bb = ByteBuffer.allocateDirect(3 * 4);
        bb.order(ByteOrder.nativeOrder());//设置字节顺序
        mCameraFB = bb.asFloatBuffer();
        mCameraFB.put(cameraLocation);
        mCameraFB.position(0);
    }

    /**
     * 设置灯光位置的方法
     */
    public void initLightLocation() {
        //计算灯光的位置
        double angleElevation = Math.toRadians(LIGHT_ELEVATION);// 仰角（弧度）
        double angledAzimuth = Math.toRadians(LIGHT_AZIMUTH);// 方位角
        float lightX = (float) (-LIGHT_DISTANCE * Math.cos(angleElevation) * Math.sin(angledAzimuth));
        float lightY = (float) (+LIGHT_DISTANCE * Math.sin(angleElevation));
        float lightZ = (float) (-LIGHT_DISTANCE * Math.cos(angleElevation) * Math.cos(angledAzimuth));

        float[] lightLocation = new float[]{lightX, lightY, lightZ};
        ByteBuffer bb = ByteBuffer.allocateDirect(3 * 4);
        //设置字节顺序
        bb.order(ByteOrder.nativeOrder());
        mLightPositionFB = bb.asFloatBuffer();
        mLightPositionFB.put(lightLocation);
        mLightPositionFB.position(0);
    }

    public float[] getCurrentMatrix() {
        return mCurrentMatrix;
    }

    public FloatBuffer getCameraFB() {
        return mCameraFB;
    }

    public FloatBuffer getLightPositionFB() {
        return mLightPositionFB;
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public float[] getVMatrix() {
        return mVMatrix;
    }

    /**
     * 获取具体物体的总变换矩阵
     */
    public static float[] getFinalMatrix(float[] projectionMatrix, float[] vMatrix, float[] currentMatrix) {
        float[] mVPMatrix = new float[16];
        Matrix.multiplyMM(mVPMatrix, 0, vMatrix, 0, currentMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVPMatrix, 0);
        return mVPMatrix;
    }

    public static float[] getViewProjMatrix(float[] projectionMatrix, float[] vMatrix) {
        float[] mVPMatrix = new float[16];
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, vMatrix, 0);
        return mVPMatrix;
    }

    //设置沿xyz轴移动
    public static void translate(float[] currentMatrix, float x, float y, float z) {
        Matrix.translateM(currentMatrix, 0, x, y, z);
    }

    //设置绕xyz轴移动
    public static void rotate(float[] currentMatrix, float angle, float x, float y, float z) {
        Matrix.rotateM(currentMatrix, 0, angle, x, y, z);
    }

    public static void scale(float[] currentMatrix, float x, float y, float z) {
        Matrix.scaleM(currentMatrix, 0, x, y, z);
    }

    //将四元数转换为角度及转轴向量
    public static float[] fromSYStoAXYZ(Quat4f q4) {
        double sitaHalf = Math.acos(q4.w);
        float nx = (float) (q4.x / Math.sin(sitaHalf));
        float ny = (float) (q4.y / Math.sin(sitaHalf));
        float nz = (float) (q4.z / Math.sin(sitaHalf));

        return new float[]{(float) Math.toDegrees(sitaHalf * 2), nx, ny, nz};
    }
}
