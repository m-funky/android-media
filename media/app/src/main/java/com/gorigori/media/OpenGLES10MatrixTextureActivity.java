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

public class OpenGLES10MatrixTextureActivity extends AppCompatActivity {

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

        private int mScreenWidth = 0;
        private int mScreenHeight = 0;

        private int mTextureWidth = 0;
        private int mTextureHeight = 0;

        private int[] mTextures = new int[2];

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            mScreenWidth = width;
            mScreenHeight = height;

            float positions[] = {
                    -0.5f, 0.5f, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, 0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
            };

            setDrawBuffer(gl10, positions);


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


            // set uv buffer for crapping texture

            float uv[] = {
                    0, 0,
                    0, 1,
                    1, 0,
                    1, 1,
            };

            ByteBuffer uvByteBuffer = ByteBuffer.allocateDirect(uv.length * 4);
            uvByteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer uvBuffer = uvByteBuffer.asFloatBuffer();
            uvBuffer.put(uv);
            uvBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, uvBuffer);

        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(0, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);


            gl10.glColor4f(1, 0, 0, 1);
            setTextureArea(gl10, 0, 0, 128, 128);
            drawQuad(gl10, 0, 0, mScreenWidth / 2, mScreenHeight / 2);

            gl10.glColor4f(0, 1, 0, 1);
            setTextureArea(gl10, 128, 128, 256, 256);
            drawQuad(gl10, mScreenWidth / 2, mScreenHeight / 2, mScreenWidth / 2, mScreenHeight / 2);

        }

        private void drawQuad(GL10 gl10, int x, int y ,int w, int h) {


            // Matrix draw buffer

            float sX = ((float)x / (float)mScreenWidth) * 2.0f;
            float sY = ((float)y / (float)mScreenHeight) * 2.0f;

            float sizeX = ((float)w / (float)mScreenWidth) * 2.0f;
            float sizeY = ((float)h / (float)mScreenHeight) * 2.0f;

            gl10.glLoadIdentity();
            gl10.glTranslatef(-1.0f + sizeX /2.0f + sX, 1.0f - sizeY / 2.0f - sY, 0.0f);
            gl10.glScalef(sizeX, sizeY, 1.0f);
            gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0 ,4);

        }

        private void setDrawBuffer(GL10 gl10, float[] positions) {

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(positions.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(positions);
            floatBuffer.position(0);

            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuffer);
        }

        private void setTextureArea(GL10 gl10, int x, int y ,int w, int h) {


            // Matrix uv

            float tX = (float)x / (float)mTextureWidth;
            float tY = (float)y / (float)mTextureHeight;

            float tW = ((float)w / (float)mTextureWidth);
            float tH = ((float)h / (float)mTextureHeight);

            gl10.glMatrixMode(GL10.GL_TEXTURE);
            gl10.glLoadIdentity();

            gl10.glTranslatef(tX, tY, 0);
            gl10.glRotatef(5.0f, 0, 0, 1.0f);
            gl10.glScalef(tW, tH, 1.0f);

            gl10.glMatrixMode(GL10.GL_MODELVIEW);
        }

    }
}
