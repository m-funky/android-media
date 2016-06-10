package com.gorigori.media;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma_okamoto on 6/9/16.
 */

public class OpenGLES10IndexBufferActivity extends AppCompatActivity {

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

        private int mScreenWidth = 0;
        private int mScreenHeight = 0;

        private int LOOP_UNIT = 200;
        private int LOOP_MAX = LOOP_UNIT * 2;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            mScreenWidth = width;
            mScreenHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

            setCamera(gl10);

            gl10.glColor4f(mCounter / (float)LOOP_MAX, mCounter / (float)LOOP_MAX, mCounter / (float)LOOP_MAX, 1);
            setCube(gl10);

            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glRotatef(1, 0, 1, 0);

            final byte[] indices = new byte[] {
                    0, 1, 2, 3, 6, 7, 4, 5, 0, 1,
                    1, 5, 3, 7,
                    0, 2, 4, 6,
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(indices);

            gl10.glColor4f(0, 1, 0, 1);
            byteBuffer.position(0);
            gl10.glDrawElements(GL10.GL_TRIANGLE_STRIP, 10, GL10.GL_UNSIGNED_BYTE, byteBuffer);

            gl10.glColor4f(0, 0, 1, 1);
            byteBuffer.position(10);
            gl10.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, byteBuffer);

            gl10.glColor4f(1, 0, 0, 1);
            byteBuffer.position(14);
            gl10.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, byteBuffer);

            mCounter++;
            if (mCounter > LOOP_MAX) {
                mCounter -= LOOP_MAX;
            }

        }

        private void setTriangle(GL10 gl10) {
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
        }

        private void drawByIndexBuffer(GL10 gl10, byte[] indices) {

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(indices);
            byteBuffer.position(0);

            gl10.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.length, GL10.GL_UNSIGNED_BYTE, byteBuffer);
        }

        private void setSquare(GL10 gl10, int x, int y ,int w, int h) {

            float left = ((float) x / (float) mScreenWidth) * 2.0f - 1.0f;
            float top = -(((float) y / (float) mScreenHeight) * 2.0f - 1.0f);

            float right = left + ((float) w / (float) mScreenWidth) * 2.0f;
            float bottom = top - ((float) h / (float) mScreenHeight) * 2.0f;

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
        }

        private void setCube(GL10 gl10) {

            float positions[] = {
                    1, 1, 1,
                    1, 1, -1,
                    -1, 1, 1,
                    -1, 1, -1,
                    1, -1, 1,
                    1, -1, -1,
                    -1, -1, 1,
                    -1, -1, -1,
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(positions.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(positions);
            floatBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuffer);
        }

        private void setCamera(GL10 gl10) {
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();

            float aspect = (float) mScreenWidth / (float) mScreenHeight;
            float cameraZ = 10.0f * mCounter / LOOP_UNIT;

            GLU.gluPerspective(gl10, 45.0f, aspect, 0.01f, 100.0f);
            GLU.gluLookAt(gl10,
                    0, 5.0f, 5.0f, // camera
                    0, 0, 0.0f, // target
                    0.0f, 1.0f, 0.0f); // camera direction
        }

    }
}
