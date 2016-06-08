package com.gorigori.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        private int mTextureWidth = 0;
        private int mTextureHeight = 0;

        private int mTextures[];

        private int mBuffers[];

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            mScreenWidth = width;
            mScreenHeight = height;

            // x, y, z, u, v
            float vertices[] = {
                    -0.5f, 0.5f, 0, 0, 0,
                    -0.5f, -0.5f, 0, 0, 1,
                    0.5f, 0.5f, 0, 1, 0,
                    0.5f, -0.5f, 0, 1, 1,
            };

            bindVBO(gl10, vertices);
            bindTexture(gl10);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

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

            gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            GL11 gl11 = (GL11) gl10;

            mBuffers = new int[1];
            gl11.glGenBuffers(1, mBuffers, 0);
            int vertexBufferObject = mBuffers[0];
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexBufferObject);
            gl11.glBufferData(GL11.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * 4,
                    floatBuffer, GL11.GL_STATIC_DRAW);


            gl11.glVertexPointer(3, GL10.GL_FLOAT, 4 * 5, 0);
            gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 4 * 5, 4 * 3);

            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        }

        private void bindTexture(GL10 gl10) {
            // Bind Texture

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monkey_512x512, options);

            mTextureWidth = imageBitmap.getWidth();
            mTextureHeight = imageBitmap.getHeight();

            Log.d("GL", "screen w: " + mScreenWidth + " h: " + mScreenHeight +
                    " texture w: " + mTextureWidth + " h: " + mTextureHeight);

            gl10.glEnable(GL10.GL_TEXTURE_2D);

            mTextures = new int[1];
            gl10.glGenTextures(1, mTextures, 0);

            gl10.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, imageBitmap, 0);

            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            imageBitmap.recycle();

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
