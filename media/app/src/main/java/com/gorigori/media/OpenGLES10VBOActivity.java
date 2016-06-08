package com.gorigori.media;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by takuma_okamoto on 6/8/16.
 */
public class OpenGLES10VBOActivity extends AppCompatActivity {

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
        private final int LOOP_UNIT = 120;
        private final int LOOP_MAX = LOOP_UNIT * 3;

        private int mScreenWidth = 0;
        private int mScreenHeight = 0;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            mScreenWidth = width;
            mScreenHeight = height;

            float positions[] = {
                    -0.5f, 0.5f, 0,
                    -0.5f, -0.5f, 0,
                    0.5f, 0.5f, 0,
                    0.5f, -0.5f, 0,
            };

            bindVBO(gl10, positions);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

            gl10.glColor4f(mCounter / (float) LOOP_MAX, mCounter / (float) LOOP_MAX, mCounter / (float) LOOP_MAX, 1);

            gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

            mCounter++;
            if (mCounter > LOOP_MAX) {
                mCounter -= LOOP_MAX;
            }

        }


        private float[] convertScreenPointerToPositions(GL10 gl10, int x, int y, int w, int h) {
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

            return positions;
        }

        private void bindVBO(GL10 gl10, float[] positions) {

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(positions.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(positions);
            floatBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            GL11 gl11 = (GL11) gl10;

            int buffers[] = new int[1];
            gl11.glGenBuffers(1, buffers, 0);
            int posBufferObject = buffers[0];
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, posBufferObject);
            gl11.glBufferData(GL11.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * 4,
                    floatBuffer, GL11.GL_STATIC_DRAW);
            gl11.glVertexPointer(3, GL10.GL_FLOAT, 0, 0);

            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        }

        private void drawQuad(GL10 gl10, int x, int y, int w, int h) {

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

            gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        }
    }
}
