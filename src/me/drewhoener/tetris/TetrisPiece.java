package me.drewhoener.tetris;

import java.awt.*;

public enum TetrisPiece {

	//TODO figure out a way to dynamically get boundaries for each piece in the future
	//TODO EDIT: I did it.

	SQUARE(new int[][]{{1, 1}, {1, 1}}, Color.GRAY),
	LIGHTNING(new int[][]{{1, 0}, {1, 1}, {0, 1}}, Color.GREEN),
	INVERSE_LIGHTNING(new int[][]{{0, 1}, {1, 1}, {1, 0}}, Color.RED),
	INVERSE_T(new int[][]{{0, 1, 0}, {1, 1, 1}}, new Color(128, 0, 128)),
	LONG_PIECE(new int[][]{{1}, {1}, {1}, {1}}, Color.CYAN),
	LONG_LEFT(new int[][]{{0, 1}, {0, 1}, {1, 1}}, Color.BLUE),
	LONG_RIGHT(new int[][]{{1, 0}, {1, 0}, {1, 1}}, Color.ORANGE);
	//LONG_U(new int[][]{{1, 1, 1}, {1, 0, 1}}, Color.PINK);


	public int[][] pieceGrid;
	public Color color;

	TetrisPiece(int[][] grid, Color color) {
		this.pieceGrid = grid;
		this.color = color;
	}

}
