package com.ccbfm.dice.stereoscopic;

import android.opengl.GLES20;

import com.ccbfm.dice.LogTools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;


public class Floor implements Entity {
    private static final String TAG = "Floor";

    private float[] mInitMatrix;
    private float[] mCurrentMatrix;
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mVerticesTextureBuffer;

    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mTextureCoordinationHandle;

    private int mTextureId;
    private int mProgramId;

    public Floor(int programId, int textureId) {
        LogTools.i(TAG, "Floor", "programId=" + programId + ",textureId=" + textureId);
        mProgramId = programId;
        mTextureId = textureId;
        initData();
        initShader();
    }

    //初始化数据
    private void initData() {
        final float[] VerticesData = {
                // X, Y, Z,
                -16.0f, 0.0f, -30.0f,
                16.0f, 0.0f, -30.0f,
                16.0f, 0.0f, 30.0f,
                -16.0f, 0.0f, -30.0f,
                16.0f, 0.0f, 30.0f,
                -16.0f, 0.0f, 30.0f,
        };

        final float[] TextureCoordinateData = {
                0, 0,
                1, 0,
                1, 1,
                0, 0,
                1, 1,
                0, 1
        };

        // Initialize the buffers.
        mVerticesBuffer = ByteBuffer.allocateDirect(VerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesBuffer.put(VerticesData).position(0);

        mVerticesTextureBuffer = ByteBuffer.allocateDirect(TextureCoordinateData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesTextureBuffer.put(TextureCoordinateData).position(0);

    }

    private void initShader() {
        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "a_Position");
        mTextureCoordinationHandle = GLES20.glGetAttribLocation(mProgramId, "a_TexCoordinate");

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

    @Override
    public void init(float[] matrix) {
        mInitMatrix = Arrays.copyOf(matrix, matrix.length);
    }

    @Override
    public void setVelocity() {

    }

    @Override
    public void startDraw() {
        mCurrentMatrix = Arrays.copyOf(mInitMatrix, mInitMatrix.length);
    }

    @Override
    public void draw(FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix) {
        GLES20.glUseProgram(mProgramId);

        // Pass in the position information
        mVerticesBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, mVerticesBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mVerticesTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinationHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mVerticesTextureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinationHandle);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        float[] finalMatrix = Environment.getFinalMatrix(projectionMatrix, vMatrix, mCurrentMatrix);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false,
                finalMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    @Override
    public void drawShadow(FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix) {

    }
}
