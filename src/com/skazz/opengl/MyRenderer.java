package com.skazz.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;

public class MyRenderer implements Renderer {

	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	private float[] mRotationMatrix = new float[16];
	private int mProgram;
	
	private final String vertexShaderCode =
			// This matrix member variable provides a hook to manipulate
			// the coordinates of the objects that use this vertex shader
			"uniform mat4 uMVPMatrix;" +

	        "attribute vec4 vPosition;" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = vPosition * uMVPMatrix;" +
	        "}";

	private final String fragmentShaderCode =
			"precision mediump float;" +
					"uniform vec4 vColor;" +
					"void main() {" +
					"  gl_FragColor = vColor;" +
					"}";
	
	public volatile float xAngle;
	public volatile float yAngle;

	private RubicsCube rubic;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask( true );
		GLES20.glDepthRangef(0, 1);
		GLES20.glClearDepthf(1);
		
		/*
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glFrontFace(GLES20.GL_CW);
		*/
		
		// prepare shader and OpenGL program
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);
		
		
		// create empty OpenGL Program
		mProgram = GLES20.glCreateProgram();
		// add the vertex shader to program
		GLES20.glAttachShader(mProgram, vertexShader);
		// add the fragment shader to program
		GLES20.glAttachShader(mProgram, fragmentShader);
		// create OpenGL program executables
		GLES20.glLinkProgram(mProgram);
		
		// Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);
        
        rubic = new RubicsCube(mProgram);
        rubic.scramble();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mVMatrix, 0, 0, 1, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		
		/* Create a rotation transformation for xAxis
		Matrix.setRotateM(mRotationMatrix, 0, xAngle, -1.0f, 0, 0);

		// Combine the rotation matrix with the projection and camera view
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
		
		// Same for yAxis
		Matrix.setRotateM(mRotationMatrix, 0, yAngle, 0, -1.0f, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0); */

		
		// Draw shape
		rubic.draw(mMVPMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

	public static int loadShader(int type, String shaderCode){

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}
	
	public void rotateCube(float xAngle, float yAngle) {
		rubic.rotateXY(xAngle, yAngle);
	}
	
	public boolean moveSide(float x, float y, int width, int height) {
		float inverted[] = new float[16];
		float nearPoint[] = new float[4];
		float farPoint[] = new float[4];
		float mX = x;
		float mY = height - y;
		
		nearPoint[0] = ((mX * 2.0f) / width - 1);
		nearPoint[1] = ((mY * 2.0f) / height - 1);
		nearPoint[2] = 1.0f;
		nearPoint[3] = 1.0f;
		
		farPoint[0] = ((mX * 2.0f) / width - 1);
		farPoint[1] = ((mY * 2.0f) / height - 1);
		farPoint[2] = -1.0f;
		farPoint[3] = 1.0f;
		
		Matrix.invertM(inverted, 0, mMVPMatrix, 0);
		Matrix.multiplyMV(nearPoint, 0, inverted, 0, nearPoint, 0);
		Matrix.multiplyMV(farPoint, 0, inverted, 0, farPoint, 0);
		System.out.println(nearPoint[0] + ", " + nearPoint[1] + ", " + nearPoint[3]);
		System.out.println(farPoint[0] + ", " + farPoint[1] + ", " + farPoint[3]);
		return false;
	}

}
