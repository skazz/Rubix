package com.skazz.opengl;

import java.util.Random;

public class RubicsCube {

	private Square square[] = new Square[48];
	private Square fixed[] = new Square[6];
	private static float SIZE = 0.1f;
	private static float EDGE = (SIZE / 2) * 3;
	private float lUpperEdge[] = {
			-EDGE, EDGE, -EDGE,	// up
			-EDGE, EDGE, -EDGE,	// left
			-EDGE, EDGE, EDGE,	// front
			EDGE, EDGE, EDGE,	// right
			EDGE, EDGE, -EDGE,	// back
			-EDGE, -EDGE, EDGE	// down
	};
	
	private int translation[] = {
		1, 0, 0,
		0, 0, 1,	// up
		0, 0, 1,
		0, -1, 0,	// left
		1, 0, 0,
		0, -1, 0,	// front
		0, 0, -1,
		0, -1, 0,	// right
		-1, 0, 0,
		0, -1, 0,	// back
		1, 0, 0,
		0, 0, -1	// down
	};
	
	// Moves Clockwise
	private int moves[][] = {
			{0, 2, 7, 5}, {1, 4, 6, 3}, {8, 32, 24, 16}, {9, 33, 25, 17}, {10, 34, 26, 18},				// Up
			{8, 10, 15, 13}, {9, 12, 14, 11}, {0, 16, 40, 39}, {3, 19, 43, 36}, {5, 21, 45, 34},		// Left
			{16, 18, 23, 21}, {17, 20, 22, 19}, {5, 24, 42, 15}, {6, 27, 41, 12}, {7, 29, 40, 10},		// Front
			{24, 26, 31, 29}, {25, 28, 30, 27}, {2, 37, 42, 18}, {4, 35, 44, 20}, {7, 32, 47, 23},		// Right
			{32, 34, 39, 37}, {33, 36, 38, 35}, {2, 8, 45, 31}, {1, 11, 46, 28}, {0, 13, 47, 26},		// Back
			{40, 42, 47, 45}, {41, 44, 46, 43}, {13, 21, 29, 37}, {14, 22, 30, 38}, {15, 23, 31, 39}	// Down
	};
	
	private int angle[][] = {
			{0, -1, 0},
			{1, 0, 0},
			{0, 0, -1},
			{-1, 0, 0},
			{0, 0, 1},
			{0, 1, 0}
	};
	
	
	private int faces[] = {
			0, 4, 5, 1,
			1, 5, 6, 2,
			2, 6, 7, 3,
			4, 8, 9, 5,
			6, 10, 11, 7,
			8, 12, 13, 9,
			9, 13, 14, 10,
			10, 14, 15, 11
	};
	private int fixedFace[] = { 5, 9, 10, 6 };
	
	
	public RubicsCube (int mProgram) {
		float vertices[];
		float temp[] = new float[12];

		for (int side = 0; side < 6; side++) {
			vertices = createVertices(side);
			
			// movable faces
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 4; j++) {
					for (int k = 0; k < 3; k++) {
						temp[j*3 + k] = vertices[(faces[i*4 + j] * 3) + k];
					}
				}
				square[side * 8 + i] = new Square(mProgram, temp, side);
			}
			
			// middle face
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 3; k++) {
					temp[j*3 + k] = vertices[(fixedFace[j] * 3) + k];
				}
			}
			fixed[side] = new Square(mProgram, temp, side);
		}
	}
	
	
	public void scramble() {
		Random rng = new Random();
		for (int i = 0; i < 20 + rng.nextInt(12); i++) {
			move(rng.nextInt(6), rng.nextInt(2) + 1);
		}
	}
	
	
	public void move(int move, int n) {
		System.out.println(n + "" + move + ", ");
		// rotate Faces (#TODO compute Squarelist instantly)
		for (int k = 0; k < n; k++) {
			Square toRotate[] = getSide(move);
			fixed[move].rotate(90, angle[move][0], angle[move][1], angle[move][2]);
			for (int i = 0; i < 20; i++) {
				toRotate[i].rotate(90, angle[move][0], angle[move][1], angle[move][2]);
			}

			// update square list
			for (int i = 0; i < 5; i++) {
				square[moves[move * 5 + i][0]] = toRotate[i * 4 + 3];
				for (int j = 1; j < 4; j++) {
					square[moves[move * 5 + i][j]] = toRotate[i * 4 + j - 1];
				}
			}
		}
	}
	
	private Square[] getSide(int side) {
		Square temp[] = new Square[20];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 4; j++) {
				temp[i*4 + j] = square[moves[side * 5 + i][j]];
			}
		}
		return temp;
	}

	
	public void draw(float[] mMVPMatrix) {
		for (int i = 0; i < 6; i++) {
			fixed[i].draw(mMVPMatrix);
		}
		
		for (int i = 0; i < 48; i++) {
			square[i].draw(mMVPMatrix);
		}
	}
	
	// create vertices from top-left to bottom-right
	private float[] createVertices(int side) {
		float vertices[] = new float[48];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 3; k++) {
					vertices[i*12 + j*3 + k] = lUpperEdge[side*3 + k] + (i * translation[side*6 + 3 + k] * SIZE) + (j *translation[side*6 + k] * SIZE);
				}
			}
		}
		
		return vertices;
	}
	
}
