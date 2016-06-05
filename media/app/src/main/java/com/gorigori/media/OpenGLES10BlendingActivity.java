package com.gorigori.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma_okamoto on 6/5/16.
 */

public class OpenGLES10BlendingActivity extends AppCompatActivity {

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

        private int mWidth = 0;
        private int mHeight = 0;

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

            gl10.glEnable(GL10.GL_BLEND);
            gl10.glEnable(GL10.GL_ALPHA);

            gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

            gl10.glColor4f(1, 0, 0, 0.25f);
            drawTriangle(gl10);
            gl10.glColor4f(0, 0, 1, 0.5f);
            drawSquare(gl10, 100, 200, 400, 400);
            gl10.glColor4f(0, 0, 1, 0.25f);
            drawSquare(gl10, 500, 200, 400, 400);
            gl10.glColor4f(1, 0, 0, 0.25f);
            drawSquare(gl10, 600, 600, 400, 400);
            gl10.glColor4f(1, 0, 0, 0.5f);
            drawSquare(gl10, 600, 1000, 400, 400);
            gl10.glColor4f(1, 0, 0, 0.75f);
            drawSquare(gl10, 600, 1400, 400, 400);
            gl10.glColor4f(1, 0, 0, 1);
            drawSquare(gl10, 600, 1800, 400, 400);

        }

        private void drawTriangle(GL10 gl10) {
            float positions[] = {
                    1, 1, 0,
                    -1, 1, 0,
                    -1, -1, 0,
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
