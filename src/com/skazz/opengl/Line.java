package com.skazz.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class Line {

	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	private float[] vertices = new float[6];
	private float[] color = { 0.8f, 0.5f, 0.7f, 1.0f };

	Line(int mProgram, float[] p1, float[] p2) {
		this.mProgram = mProgram;
		
		float[] vector = new float[3];
		vector[0] = p2[0] - p1[0];
		vector[1] = p2[1] - p1[1];
		vector[2] = p2[2] - p1[2];
		
		vertices[0] = p1[0] + 1000 * vector[0];
		vertices[1] = p1[1] + 1000 * vector[1];
		vertices[2] = p1[2] + 1000 * vector[2];
		vertices[3] = p1[0] - 1000 * vector[0];
		vertices[4] = p1[1] - 1000 * vector[1];
		vertices[5] = p1[2] - 1000 * vector[2];

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				vertices.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);


	}

	public void draw(float[] mMVPMatrix) {
		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
				GLES20.GL_FLOAT, false,
				12, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// Draw the line
		GLES20.glLineWidth(0.1f);
		
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertices.length / 3);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
