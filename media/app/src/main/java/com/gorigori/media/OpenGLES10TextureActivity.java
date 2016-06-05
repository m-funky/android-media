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

/**
 * Created by takuma_okamoto on 6/5/16.
 */

public class OpenGLES10TextureActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(new GL10TextureRenderSample());
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

    class GL10TextureRenderSample implements GLSurfaceView.Renderer {

        int mScreenWidth = 0;
        int mScreenHeight = 0;

        int mTextureWidth = 0;
        int mTextureHeight = 0;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            mScreenWidth = width;
            mScreenHeight = height;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monkey_512x512, options);

            mTextureWidth = bitmap.getWidth();
            mTextureHeight = bitmap.getHeight();

            Log.d("GL", "screen w: " + mScreenWidth + " h: " + mScreenHeight +
                    " texture w: " + mTextureWidth + " h: " + mTextureHeight);

            gl10.glEnable(GL10.GL_TEXTURE_2D);

            int[] buffers = new int[1];
            gl10.glGenTextures(1, buffers, 0);
            int texture = buffers[0];

            gl10.glBindTexture(GL10.GL_TEXTURE_2D, texture);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            bitmap.recycle();
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);



            setTextureArea(gl10, 0, 0, 512, 512);
            drawSquare(gl10, 0, 0, 512, 512);

            setTextureArea(gl10, 0, 0, 256, 256);
            drawSquare(gl10, 0, 512, 256, 256);

            setTextureArea(gl10, 256, 256, 256, 256);
            drawSquare(gl10, mScreenWidth / 2, mScreenHeight / 2, 800, 1000);

        }

        private void drawSquare(GL10 gl10, int x, int y ,int w, int h) {

            float left = ((float)x / (float)mScreenWidth) * 2.0f - 1.0f;
            float top = -(((float)y / (float)mScreenHeight) * 2.0f - 1.0f);

            float right = left + ((float)w / (float)mScreenWidth) * 2.0f;
            float bottom = top - ((float)h / (float)mScreenHeight) * 2.0f;

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

        private void setTextureArea(GL10 gl10, int x, int y ,int w, int h) {

            float left = (float)x / (float)mTextureWidth;
            float top = (float)y / (float)mTextureHeight;

            float right = left + ((float)w / (float)mTextureWidth);
            float bottom = top + ((float)h / (float)mTextureHeight);

            float uv[] = {
                    left, top,
                    left, bottom,
                    right, top,
                    right, bottom,
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(uv.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(uv);
            floatBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, floatBuffer);
        }

    }
}

