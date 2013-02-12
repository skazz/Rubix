package com.skazz.rubix;

import com.skazz.opengl.MyRenderer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.rubix_menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.newGame:
	        	return true;
	        case R.id.save_on_exit:
	        	item.setChecked(!item.isChecked());
	        	return true;
	        case R.id.timer:
	        	item.setChecked(!item.isChecked());
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
}

class MyGLSurfaceView extends GLSurfaceView {

	private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private MyRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;
	private boolean hit = false;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    // MotionEvent reports input details from the touch screen
	    // and other input controls. In this case, you are only
	    // interested in events where the touch position changed.

	    float x = e.getX();
	    float y = e.getY();
	    
	    int width = getWidth();
	    int height = getHeight();

	    switch (e.getAction()) {
	    	case MotionEvent.ACTION_DOWN:
	    		if (mRenderer.intersect(x, y, width, height)) {
	    			hit = true;
	    			requestRender();
	    		}
	    		break;
	        case MotionEvent.ACTION_MOVE:
	        	if (hit) {
	        		
	        	}
	        	else {
	        		float dx = x - mPreviousX;
	        		float dy = y - mPreviousY;

	        		mRenderer.addAngle(dy * TOUCH_SCALE_FACTOR, dx * TOUCH_SCALE_FACTOR);
	        		requestRender();
	        	}
	        	break;
	        case MotionEvent.ACTION_UP:
	        	hit = false;
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
