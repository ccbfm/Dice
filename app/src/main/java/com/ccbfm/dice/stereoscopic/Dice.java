package com.ccbfm.dice.stereoscopic;

import android.opengl.GLES20;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Dice implements Entity {

    private float[] mInitMatrix;
    private float[] mCurrentMatrix;
    private RigidBody mRigidBody;
    //顶点坐标数据缓冲
    private FloatBuffer mVerticesBuffer;
    //顶点法向量数据缓冲
    private FloatBuffer mNormalBuffer;
    //纹理坐标
    private FloatBuffer mVerticesTextureBuffer;
    //纹理资源
    private int mTextureId;
    //着色器程序
    private int mProgramId;
    private int mVertexCount;

    private int mModelMatrixHandle;
    private int mVPMatrixHandle;
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mTextureCoordinationHandle;
    private int mIsShadowHandle;
    private int mLightPositionHandle;
    private int mCameraPositionHandle;
    private int mNormalHandle;

    public Dice(RigidBody rigidBody) {
        mRigidBody = rigidBody;
    }

    public void initFloatBuffer(float[] verticesData, float[] verticesNormalData, float[] textureCoordinateData) {

        mVertexCount = verticesData.length / 3;
        // Initialize the buffers.
        mVerticesBuffer = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesBuffer.put(verticesData).position(0);

        mNormalBuffer = ByteBuffer.allocateDirect(verticesNormalData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormalBuffer.put(verticesNormalData).position(0);

        mVerticesTextureBuffer = ByteBuffer.allocateDirect(textureCoordinateData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesTextureBuffer.put(textureCoordinateData).position(0);
    }

    public void initShader(int programId, int textureId) {
        mProgramId = programId;
        mTextureId = textureId;

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "uMVPMatrix");
        mModelMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "uMMatrix");
        mVPMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "uMProjectionCameraMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "aPosition");
        mCameraPositionHandle = GLES20.glGetUniformLocation(mProgramId, "uCamera");
        mLightPositionHandle = GLES20.glGetUniformLocation(mProgramId, "uLightLocation");
        mTextureCoordinationHandle = GLES20.glGetAttribLocation(mProgramId, "aTextureCoord");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramId, "aNormal");
        mIsShadowHandle = GLES20.glGetUniformLocation(mProgramId, "isShadow");

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

    @Override
    public void init(float[] matrix) {
        mInitMatrix = Arrays.copyOf(matrix, matrix.length);
    }

    @Override
    public void setVelocity() {
        RigidBody body = mRigidBody;
        if (!body.isActive()) {
            body.activate();
        }
        body.setLinearVelocity(new Vector3f(0, 50.0f, 0.0f));
        body.setAngularVelocity(new Vector3f(1, 1, 1));
    }

    @Override
    public void startDraw() {
        RigidBody body = mRigidBody;
        if (!body.isActive()) {
            body.activate();
        }
        mCurrentMatrix = Arrays.copyOf(mInitMatrix, mInitMatrix.length);
        Environment.scale(mCurrentMatrix, 1.8f, 1.8f, 1.8f);
    }

    @Override
    public void draw(FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix) {
        //获取这个箱子的变换信息对象
        Transform trans = mRigidBody.getMotionState().getWorldTransform(new Transform());
        //进行移位变换
        Environment.translate(mCurrentMatrix, trans.origin.x, trans.origin.y, trans.origin.z);
        Quat4f ro = trans.getRotation(new Quat4f());//获取当前变换的旋转信息
        if (ro.x != 0 || ro.y != 0 || ro.z != 0) {
            float[] fa = Environment.fromSYStoAXYZ(ro);//将四元数转换成AXYZ的形式
            Environment.rotate(mCurrentMatrix, fa[0], fa[1], fa[2], fa[3]);//执行旋转
        }
        draw(0, cameraBuffer, lightBuffer, projectionMatrix, vMatrix);
    }

    @Override
    public void drawShadow(FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix) {
        draw(1, cameraBuffer, lightBuffer, projectionMatrix, vMatrix);
    }

    private void draw(int shadow, FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix) {
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgramId);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false,
                Environment.getFinalMatrix(projectionMatrix, vMatrix, mCurrentMatrix), 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false,
                mCurrentMatrix, 0);
        //将光源位置传入着色器程序
        GLES20.glUniform3fv(mLightPositionHandle, 1, lightBuffer);
        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(mCameraPositionHandle, 1, cameraBuffer);
        //将是否绘制阴影属性传入着色器程序
        GLES20.glUniform1i(mIsShadowHandle, shadow);
        //将投影、摄像机组合矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(mVPMatrixHandle, 1, false,
                Environment.getViewProjMatrix(projectionMatrix, vMatrix), 0);

        //将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        mPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVerticesBuffer
                );
        //将顶点法向量数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        mNormalHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        //纹理数据
        GLES20.glVertexAttribPointer(
                mTextureCoordinationHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                mVerticesTextureBuffer
        );
        //启用顶点位置、法向量、纹理坐标数据
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordinationHandle);

        //设置纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        //绘制被加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
    }
}
