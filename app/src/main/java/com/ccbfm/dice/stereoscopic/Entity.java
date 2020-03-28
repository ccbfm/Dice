package com.ccbfm.dice.stereoscopic;

import java.nio.FloatBuffer;

public interface Entity {

    void init(float[] matrix);

    void setVelocity();

    void startDraw();

    void draw(FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix);

    void drawShadow(FloatBuffer cameraBuffer, FloatBuffer lightBuffer, float[] projectionMatrix, float[] vMatrix);

}
