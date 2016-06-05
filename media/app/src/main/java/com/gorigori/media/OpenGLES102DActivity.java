package com.gorigori.media;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma_okamoto on 6/5/16.
 */

public class OpenGLES102DActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(new GL10RenderSample());
        setContentView(mGLSurfaceView);

    }

    @Override
    public void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    class GL10RenderSample implements GLSurfaceView.Renderer {

        private int mCounter = 0;

        private int mWidth = 0;
        private int mHeight = 0;

        private int LOOP_UNIT = 200;
        private int LOOP_MAX = LOOP_UNIT * 2;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

            gl10.glColor4f(mCounter / (float)LOOP_MAX, mCounter / (float)LOOP_MAX, mCounter / (float)LOOP_MAX, 1);
            if (mCounter / LOOP_UNIT == 0) {
                drawTriangle(gl10);
            } else {
                drawSquare(gl10, 100, 200, mCounter, mCounter * 2);
            }

            mCounter++;
            if (mCounter > LOOP_MAX) {
                mCounter -= LOOP_MAX;
            }

        }

        private void drawTriangle(GL10 gl10) {
            float positions[] = {
                    0, 1, 0,
                    0, 0, 0,
                    1, 1, 0,
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(positions.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(positions);
            floatBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuffer);
            gl10.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        }

        private void drawSquare(GL10 gl10, int x, int y ,int w, int h) {

            float left = ((float)x / (float)mWidth) * 2.0f - 1.0f;
            float top = -(((float)y / (float)mHeight) * 2.0f - 1.0f);

            float right = left + ((float)w / (float)mWidth) * 2.0f;
            float bottom = top - ((float)h / (float)mHeight) * 2.0f;

            float positions[] = {
                    left, top, 0,
                    left, bottom, 0,
                    right, top, 0,
                    right, bottom, 0,
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(positions.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(positions);
            floatBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuffer);
            gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        }

    }
}
