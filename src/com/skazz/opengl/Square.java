package com.skazz.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.skazz.utility.Vector;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Square {

	private final FloatBuffer vertexBuffer;
	private final FloatBuffer normalBuffer;
	private final ShortBuffer drawListBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 4;
	private static float COLOR[][] = {
			{ 0.8f, 0.8f, 0.2f, 1.0f},	// up yellow
			{ 0.2f, 0.2f, 0.8f, 1.0f},	// left blue
			{ 0.8f, 0.2f, 0.2f, 1.0f},	// front red
			{ 0.2f, 0.8f, 0.2f, 1.0f},	// right green
			{ 0.8f, 0.4f, 0.2f, 1.0f},	// back orange
			{ 0.8f, 0.8f, 0.8f, 1.0f}	// down white
			};
	private float squareCoords[];
	
	private float normals[];

	private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	// Set color with red, green, blue and alpha (opacity) values
	private int color;
	
	public int getColor() { return color; };

	public Square(int mProgram, float vertices[], float normals[], int color) {
		this.mProgram = mProgram;
		this.color = color;
		
		squareCoords = new float[16];
		this.normals = new float[16];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				squareCoords[i*4 + j] = vertices[i*3 + j];
				this.normals[i*4 + j] = normals[i*3 + j];
			}
			squareCoords[i*4 + 3] = 1.0f;
			this.normals[i*4 + 3] = 1.0f;
		}
		
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				squareCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);
		
		// initialize NormalenBuffer
		ByteBuffer nb = ByteBuffer.allocateDirect(this.normals.length * 4);
		nb.order(ByteOrder.nativeOrder());
		normalBuffer = nb.asFloatBuffer();
		normalBuffer.put(this.normals);
		normalBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);
	}
	
	
	public void rotate(float angle, float x, float y, float z) {
		float mRM[] = new float[16];
		Matrix.setRotateM(mRM, 0, angle, x, y, z);
		for (int i = 0; i < 4; i++) {
			Matrix.multiplyMV(squareCoords, i*4, mRM, 0, squareCoords, i*4);
			Matrix.multiplyMV(normals, i*4, mRM, 0, normals, i*4);
		}
		
		// update buffer
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);
		
		normalBuffer.put(normals);
		normalBuffer.position(0);
	}

	public void draw(float[] mvpMatrix) {
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
		GLES20.glUniform4fv(mColorHandle, 1, COLOR[color], 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		// Draw the square
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
				GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	
	public boolean intersect(float[] P0, float[] P1) {
		
		float[] n = { normals[0], normals[1], normals[2] };
		float[] vP = Vector.minus(P1, P0);
		
		float dotNV = Vector.dot(n, vP);
		if (dotNV > 0) {
			float[] V0 = { squareCoords[0], squareCoords[1], squareCoords[2] };
			float[] V1 = { squareCoords[4], squareCoords[5], squareCoords[6] };
			float[] V3 = { squareCoords[12], squareCoords[13], squareCoords[14] };
			
			float r = (Vector.dot(n, (Vector.minus(V0, P0))) / dotNV);
			float[] I = Vector.addition(P0, Vector.scalarProduct(r, vP));
			
			float[] u = Vector.minus(V1, V0);
			float[] v = Vector.minus(V3, V0);
			float[] w = Vector.minus(I, V0);
			
			float temp = Vector.dot(u, v) * Vector.dot(u, v) - Vector.dot(u, u) * Vector.dot(v, v);
			float s = (Vector.dot(u, v) * Vector.dot(w, v) - Vector.dot(v, v) * Vector.dot(w, u)) / temp;
			float t = (Vector.dot(u, v) * Vector.dot(w, u) - Vector.dot(u, u) * Vector.dot(w, v)) / temp;
			
			if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
				return true;
			}
		}
		
		return false;
	}
}
