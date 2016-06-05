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
            gl10.glViewport(0, 0, width, height);
            mScreenWidth = width;
            mScreenHeight = height;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monkey_512x512, options);

            mTextureWidth = imageBitmap.getWidth();
            mTextureHeight = imageBitmap.getHeight();
            // draw text

            Bitmap textBitmap = Bitmap.createBitmap(mTextureWidth, mTextureWidth, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(textBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(30);

            canvas.drawColor(0);
            canvas.drawText("This is sample.", 0, 30, paint);



            Log.d("GL", "screen w: " + mScreenWidth + " h: " + mScreenHeight +
                    " texture w: " + mTextureWidth + " h: " + mTextureHeight);

            gl10.glEnable(GL10.GL_TEXTURE_2D);

            mTextures = new int[2];
            gl10.glGenTextures(2, mTextures, 0);

            gl10.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, imageBitmap, 0);

            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            gl10.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[1]);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textBitmap, 0);

            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            imageBitmap.recycle();
            textBitmap.recycle();

        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);



            setTexture(gl10, 0);
            setTextureArea(gl10, 0, 0, 512, 512);
            drawSquare(gl10, 0, 0, 512, 512);

            setTexture(gl10, 1);
            setTextureArea(gl10, 0, 0, 256, 256);
            drawSquare(gl10, 512, 0, 256, 256);

            setTexture(gl10, 1);
            setTextureArea(gl10, 0, 0, 128, 128);
            drawSquare(gl10, 0, 512, 512, 512);

            setTexture(gl10, 0);
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

        private void setTexture(GL10 gl10, int index) {

            gl10.glEnable(GL10.GL_TEXTURE_2D);

            gl10.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[index]);

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

