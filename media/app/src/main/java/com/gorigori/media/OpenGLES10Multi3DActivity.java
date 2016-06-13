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
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by takuma_okamoto on 6/13/16.
 */

public class OpenGLES10Multi3DActivity extends AppCompatActivity {

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

        private float mAspect;

        private int LOOP_UNIT = 200;
        private int LOOP_MAX = LOOP_UNIT * 2;

        private int mVertices = 0;
        private int mIndices = 0;
        private int mIndicesLength = 0;

        private Object3D mBox0 = new Object3D();
        private Object3D mBox1 = new Object3D();

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            mScreenWidth = width;
            mScreenHeight = height;

            mAspect = (float) width / (float) height;

            int[] buffer = new int[2];
            ((GL11)gl10).glGenBuffers(2, buffer, 0);

            mVertices = buffer[0];
            mIndices = buffer[1];

            setCube(gl10);

            final byte[] indices = new byte[] {
                    0, 1, 2,
                    2, 1, 3,
                    2, 3, 6,
                    6, 3, 7,
                    6, 7, 4,
                    4, 7, 5,
                    4, 5, 0,
                    0, 5, 1,
                    1, 5, 3,
                    3, 5, 7,
                    0, 2, 4,
                    4, 2, 6,
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(indices);
            byteBuffer.position(0);

            mIndicesLength = indices.length;

            ((GL11)gl10).glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndices);
            ((GL11)gl10).glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.capacity(),
                    byteBuffer, GL11.GL_STATIC_DRAW);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

            setCamera(gl10);

            setCube(gl10);

            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glRotatef(1, 0, 1, 0);

            mBox0.posX = -0.5f;
            mBox0.rotateY += 1.0f;
            mBox0.red = 1.0f;
            mBox0.green = 0.0f;
            mBox0.blue = 1.0f;

            mBox0.drawBox(gl10);

            mBox1.posX = 0.5f;
            mBox1.rotateY -= 0.5f;
            mBox1.red = 0.0f;
            mBox1.green = 0.0f;
            mBox1.blue = 1.0f;

            mBox1.drawBox(gl10);



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

            GLU.gluPerspective(gl10, 45.0f, mAspect, 0.01f, 100.0f);
            GLU.gluLookAt(gl10,
                    0, 5.0f, 5.0f, // camera
                    0, 0, 0.0f, // target
                    0.0f, 1.0f, 0.0f); // camera direction
        }


        class Object3D {

            public float posX = 0;
            public float posY = 0;
            public float posZ = 0;
            public float rotateY = 0;
            public float scale = 1;

            public float red = 1;
            public float green = 1;
            public float blue = 1;
            public float alpha = 1;

            public void drawBox(GL10 gl10) {
                gl10.glMatrixMode(GL10.GL_MODELVIEW);
                gl10.glLoadIdentity();

                gl10.glTranslatef(posX, posY, posZ);
                gl10.glRotatef(rotateY, 0, 1, 0);
                gl10.glScalef(scale, scale, scale);

                gl10.glColor4f(red, green, blue, alpha);

                ((GL11)gl10).glDrawElements(GL10.GL_TRIANGLES, mIndicesLength, GL10.GL_UNSIGNED_BYTE, 0);
            }

        }
    }
}
