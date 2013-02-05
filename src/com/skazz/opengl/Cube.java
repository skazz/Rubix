package com.skazz.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Cube {

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	// 4 bytes per vertex
	private final int vertexStride = COORDS_PER_VERTEX * 4;

	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	/** The buffer holding the indices */
	private final ShortBuffer  indexBuffer;
	/** The buffer holding the colors */
	//private final FloatBuffer colorBuffer;

	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	private float vertices[];
	private short indices[] = {
			0, 4, 5,    0, 5, 1,
			1, 5, 6,    1, 6, 2,
			2, 6, 7,    2, 7, 3,
			3, 7, 4,    3, 4, 0,
			4, 7, 6,    4, 6, 5,
			3, 0, 1,    3, 1, 2
	};
	private float colors[][] = {
			{ 0.2f, 0.2f, 0.8f, 1.0f},
			{ 0.2f, 0.8f, 0.2f, 1.0f},
			{ 0.8f, 0.2f, 0.2f, 1.0f},
			{ 0.2f, 0.8f, 0.8f, 1.0f},
			{ 0.8f, 0.8f, 0.2f, 1.0f},
			{ 0.8f, 0.2f, 0.8f, 1.0f} };
	private int color;
	private float xAngle;
	private float yAngle;
	private float zAngle;
	
	private float[] mRotationMatrix = new float[16];
	private float[] mMVPMatrix;


	public Cube(float coords[], float size, int color, int program) {

		// testCube();
		this.color = color;
		float offset = size / 2;
		float sCoords[] = { coords[0] - offset, coords[1] - offset, coords[2] - offset,
				coords[0] + offset, coords[1] - offset, coords[2] - offset,
				coords[0] + offset, coords[1] + offset, coords[2] - offset,
				coords[0] - offset, coords[1] + offset, coords[2] - offset,
				coords[0] - offset, coords[1] - offset, coords[2] + offset,
				coords[0] + offset, coords[1] - offset, coords[2] + offset,
				coords[0] + offset, coords[1] + offset, coords[2] + offset,
				coords[0] - offset, coords[1] + offset, coords[2] + offset };
		vertices = sCoords;

		// initialize vertex byte buffer for cube coordinates
		// (# of coordinate values * 4 bytes per float)
		ByteBuffer vbb = ByteBuffer.allocateDirect(
				vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);


		// initialize byte buffer for the indices
		// (# of coordinate values * 2 bytes per short)
		ByteBuffer ibb = ByteBuffer.allocateDirect(
				indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);


		// initialize byte buffer for the colors
		// (# of coordinate values * 4 bytes per float)
		/*ByteBuffer cbb = ByteBuffer.allocateDirect(
				colors.length * 4);
		ibb.order(ByteOrder.nativeOrder());
		colorBuffer = cbb.asFloatBuffer();
		colorBuffer.put(colors[0]); // MIES!!!!!!!!!!!!
		colorBuffer.position(0); */

		mProgram = program;
	}


	public void draw(float[] mMVPMatrix) {
		this.mMVPMatrix = mMVPMatrix.clone();
		
		// Create a rotation transformation for xAxis
		Matrix.setRotateM(mRotationMatrix, 0, xAngle, -1.0f, 0, 0);

		// Combine the rotation matrix with the projection and camera view
		Matrix.multiplyMM(this.mMVPMatrix, 0, mRotationMatrix, 0, this.mMVPMatrix, 0);

		// Same for yAxis
		Matrix.setRotateM(mRotationMatrix, 0, yAngle, 0, -1.0f, 0);
		Matrix.multiplyMM(this.mMVPMatrix, 0, mRotationMatrix, 0, this.mMVPMatrix, 0);
		
		// Same for zAxis
		Matrix.setRotateM(mRotationMatrix, 0, zAngle, 0, 0, -1.0f);
		Matrix.multiplyMM(this.mMVPMatrix, 0, mRotationMatrix, 0, this.mMVPMatrix, 0);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false,
				vertexStride, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, colors[color], 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);

		// Draw the square
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
				GLES20.GL_UNSIGNED_SHORT, indexBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}


	public void testCube() {
		float tVertices[] = {
				-0.1f, -0.1f, -0.1f,	//lower back left (0)
				0.1f, -0.1f, -0.1f,		//lower back right (1)
				0.1f,  0.1f, -0.1f,		//upper back right (2)
				-0.1f, 0.1f, -0.1f,		//upper back left (3)
				-0.1f, -0.1f,  0.1f,	//lower front left (4)
				0.1f, -0.1f,  0.1f,		//lower front right (5)
				0.1f,  0.1f,  0.1f,		//upper front right (6)
				-0.1f,  0.1f,  0.1f		//upper front left (7)
		};

		/** 
		 * The initial indices definition
		 * 
		 * The indices define our triangles.
		 * Always two define one of the six faces
		 * a cube has.
		 */	
		short tIndices[] = {
				/*
				 * Example: 
				 * Face made of the vertices lower back left (lbl),
				 * lfl, lfr, lbl, lfr, lbr
				 */
				0, 4, 5,    0, 5, 1,
				//and so on...
				1, 5, 6,    1, 6, 2,
				2, 6, 7,    2, 7, 3,
				3, 7, 4,    3, 4, 0,
				4, 7, 6,    4, 6, 5,
				3, 0, 1,    3, 1, 2
		};

		vertices = tVertices;
		indices = tIndices;
	}
	
	public void rotate(float xAngle, float yAngle, float zAngle) {
		this.xAngle = (this.xAngle + xAngle) % 360;
		this.yAngle = (this.yAngle + yAngle) % 360;
		this.zAngle = (this.zAngle + zAngle) % 360;
	}
}
