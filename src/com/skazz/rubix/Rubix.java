package com.skazz.rubix;

import com.skazz.opengl.MyRenderer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

public class Rubix extends Activity {

	private GLSurfaceView mGLView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		mGLView = new MyGLSurfaceView(this);
		setContentView(mGLView);
	}

}

class MyGLSurfaceView extends GLSurfaceView {
	
	private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private MyRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    // MotionEvent reports input details from the touch screen
	    // and other input controls. In this case, you are only
	    // interested in events where the touch position changed.

	    float x = e.getX();
	    float y = e.getY();

	    switch (e.getAction()) {
	        case MotionEvent.ACTION_MOVE:

	            float dx = x - mPreviousX;
	            float dy = y - mPreviousY;
	            
	            mRenderer.xAngle += dy * TOUCH_SCALE_FACTOR;
	            mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;
	            requestRender();
	    }

	    mPreviousX = x;
	    mPreviousY = y;
	    return true;
	}

	public MyGLSurfaceView(Context context){
		super(context);

		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyRenderer();
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
}
