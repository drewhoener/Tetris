package me.drewhoener.tetris;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JoinedPiece implements Cloneable {

	private CopyOnWriteArrayList<Rectangle> pieces = new CopyOnWriteArrayList<>();

	private int pixelWidth;
	private int pixelHeight;

	private int baseX;
	private int baseY;
	private int[][] currentGrid;
	private Color color;

	private final TetrisPiece piece;

	public static long PIECE_DELAY = 200;

	private BasicStroke rectangleStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	public JoinedPiece(TetrisPiece piece) {

		this.piece = piece;
		this.currentGrid = piece.pieceGrid;
		this.color = piece.color;
		this.pieces.clear();
		this.generateRectangles(this.currentGrid);
	}

	public void rotateShape() {

		int[][] newGrid = new int[this.pixelWidth][this.pixelHeight];
		for (int i = 0; i < this.pixelHeight; i++) {
			for (int j = 0; j < this.pixelWidth; j++) {
				//System.out.println(i + " " + j);
				newGrid[j][i] = this.currentGrid[i][this.pixelWidth - 1 - j];
			}
		}

		this.currentGrid = newGrid;
		this.pieces.clear();
		this.generateRectangles(this.currentGrid);
	}

	public void generateRectangles(int[][] matrix) {

		this.pixelHeight = matrix.length;
		this.pixelWidth = matrix[0].length;
		for (int i = 0; i < this.pixelHeight; i++)
			for (int j = 0; j < this.pixelWidth; j++) {
				if (matrix[i][j] == 0)
					continue;
				Rectangle rec = new Rectangle(j * TetrisPanel.PIXEL_SIZE, i * TetrisPanel.PIXEL_SIZE,
						TetrisPanel.PIXEL_SIZE, TetrisPanel.PIXEL_SIZE);
				rec.setLocation((int) (rec.getX() + this.baseX), (int) (rec.getY() + baseY));
				pieces.add(rec);
			}

	}

	public void drawObject(Graphics2D graphics2D) {

		graphics2D.setStroke(rectangleStroke);

		for (Rectangle rect : this.pieces) {

			graphics2D.setColor(this.color);
			graphics2D.fill(rect);
			graphics2D.setColor(Color.WHITE);
			graphics2D.draw(rect);

		}

	}

	public void translate(int deltaX, int deltaY) {

		this.baseX += deltaX;
		this.baseY += deltaY;

		for (Rectangle rectangle : this.pieces) {

			rectangle.translate(deltaX, deltaY);
		}

	}

	public void setPosition(int x, int y) {
		this.baseX = x;
		this.baseY = y;

		for (Rectangle rect : this.pieces) {

			rect.setLocation((int) (rect.getX() + this.baseX), (int) (rect.getY() + baseY));

		}

	}

	public void centerPiece(double centerX, double centerY) {

		double centerPieceX = ((double)this.pixelWidth) / 2D;
		double centerPieceY = ((double)this.pixelHeight) / 2D;

		this.setPosition((int) (centerX - (centerPieceX * TetrisPanel.PIXEL_SIZE)), (int) (centerY - (centerPieceY * TetrisPanel.PIXEL_SIZE)));

	}

	public int[][] getMatrix() {
		return currentGrid;
	}

	public boolean removePart(Rectangle rect) {
		this.pieces.remove(rect);
		return this.pieces.size() == 0;
	}

	public boolean removeParts(List<Rectangle> parts) {
		this.pieces.removeAll(parts);
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		for (Rectangle rect : this.pieces) {
			if (rect.getMinX() < minX) minX = (int) rect.getMinX();
			if (rect.getMaxX() > maxX) maxX = (int) rect.getMaxX();
			if (rect.getMinY() < minY) minY = (int) rect.getMinY();
			if (rect.getMaxY() > maxY) maxY = (int) rect.getMaxY();
			this.pixelWidth = (maxX - minX) / TetrisPanel.PIXEL_SIZE;
			this.pixelHeight = (maxY - minY) / TetrisPanel.PIXEL_SIZE;
		}
		return this.pieces.size() == 0;
	}

	public int getMinX() {
		return this.baseX;
	}

	public int getMaxX() {
		return this.baseX + (this.pixelWidth * TetrisPanel.PIXEL_SIZE);
	}

	public int getMinY() {
		return this.baseY;
	}

	public int getMaxY() {
		return this.baseY + (this.pixelHeight * TetrisPanel.PIXEL_SIZE);
	}

	public int getIncrementedY() {
		return this.getMaxY() + TetrisPanel.PIXEL_SIZE;
	}

	public int getPixelWidth() {
		return pixelWidth;
	}

	public int getPixelHeight() {
		return pixelHeight;
	}

	public Rectangle[] getIncrementedPieces(TetrisPanel.Direction direction) {

		Rectangle[] newArray = new Rectangle[this.pieces.size()];
		int i = 0;
		for (Rectangle rect1 : this.pieces) {

			newArray[i] = new Rectangle(rect1);
			switch (direction) {

				case LEFT:
					newArray[i].translate(-TetrisPanel.PIXEL_SIZE, 0);
					break;
				case RIGHT:
					newArray[i].translate(TetrisPanel.PIXEL_SIZE, 0);
					break;
				case DOWN:
					newArray[i].translate(0, TetrisPanel.PIXEL_SIZE);
					break;
				default:
					newArray[i].translate(0, TetrisPanel.PIXEL_SIZE);
					break;

			}
			i++;
		}

		return newArray;
	}

	public TetrisPiece getType(){
		return this.piece;
	}

	public Rectangle[] getPieces() {
		return this.pieces.toArray(new Rectangle[this.pieces.size()]);
	}

}
