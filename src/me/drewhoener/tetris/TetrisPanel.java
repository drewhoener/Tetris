package me.drewhoener.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TetrisPanel extends JPanel implements KeyListener, Runnable {

	Thread updateThread;
	private boolean isRunning = false;
	private boolean isPaused = false;
	private BufferedImage image;

	private Font defaultFont;
	private Font mediumFont = new Font("Gill Sans", Font.PLAIN, 47);
	private Font bigFont = new Font("Gill Sans", Font.PLAIN, 68);
	public static final int PIXEL_SIZE = 60;
	public static final int ACTUAL_HEIGHT = 13 * PIXEL_SIZE;
	public static final int RENDERING_HEIGHT = ACTUAL_HEIGHT - 1;
	public static final int PLAY_WIDTH = PIXEL_SIZE * 16;
	public static final int ACTUAL_WIDTH = PLAY_WIDTH + 1;
	public static final int SIDEBAR_WIDTH = (int) (4.4D * PIXEL_SIZE);
	public static final int TOTAL_WIDTH = ACTUAL_WIDTH + SIDEBAR_WIDTH;

	public static final int SLEEP_TIME = 10;
	public static final int ROW_SCAN = 30;
	public List<Rectangle> scanRects = new ArrayList<>();

	private BasicStroke dividerStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

	public List<JoinedPiece> settledPieces = new ArrayList<>();
	public JoinedPiece nextPiece;
	public JoinedPiece activePiece;

	public TetrisPanel() {
		super(true);
		this.setPreferredSize(new Dimension(TOTAL_WIDTH, ACTUAL_HEIGHT));
		this.nextPiece = new JoinedPiece(TetrisPiece.values()[new Random().nextInt(TetrisPiece.values().length)]);
		this.centerPreview();
		JoinedPiece piece = new JoinedPiece(TetrisPiece.values()[new Random().nextInt(TetrisPiece.values().length)]);
		piece.setPosition(2 * PIXEL_SIZE, 2 * PIXEL_SIZE - 1);
		this.activePiece = piece;
		for (int i = 30; i < PLAY_WIDTH; i += 60) {
			Rectangle temp = new Rectangle(i - 1, RENDERING_HEIGHT - ROW_SCAN - 1, 2, 2);
			this.scanRects.add(temp);
		}

	}

	public void addNotify() {
		super.addNotify();
		if (this.updateThread == null) {
			this.isRunning = true;
			this.updateThread = new Thread(this);
			this.addKeyListener(this);
			this.updateThread.start();
			this.requestFocusInWindow();
			this.defaultFont = this.getFont();
		}
	}

	@Override
	public void run() {

		long timer = JoinedPiece.PIECE_DELAY;

		this.image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(TOTAL_WIDTH, ACTUAL_HEIGHT);
		Graphics2D imageGraphics = ((Graphics2D) this.image.getGraphics());

		while (this.isRunning) {

			if (timer <= 0) {
				if(!this.isPaused) {
					this.updatePieces();
					timer = JoinedPiece.PIECE_DELAY;
				}
			}

			imageGraphics.setColor(Color.BLACK);
			imageGraphics.fillRect(0, 0, TOTAL_WIDTH, ACTUAL_HEIGHT);
			imageGraphics.setColor(new Color(81, 81, 81));
			imageGraphics.fillRect(ACTUAL_WIDTH + 1, 0, TOTAL_WIDTH, ACTUAL_HEIGHT);
			this.drawToImage(imageGraphics);
			this.drawImage();
			imageGraphics.dispose();
			imageGraphics = ((Graphics2D) this.image.getGraphics());

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(!this.isPaused)
				timer -= SLEEP_TIME;
		}
	}

	public void drawToImage(Graphics2D g) {

		g.setStroke(this.dividerStroke);
		g.setColor(Color.WHITE);
		g.drawLine(ACTUAL_WIDTH, 0, ACTUAL_WIDTH, ACTUAL_HEIGHT);

		this.addRectStrokes(g);

		this.activePiece.drawObject(g);

		for (JoinedPiece piece : this.settledPieces) {
			piece.drawObject(g);
		}

		//for (Rectangle rect : this.scanRects) {
			//g.setColor(Color.GREEN);
			//g.fill(rect);
		//}
		//g.drawLine(PLAY_WIDTH - SIDEBAR_WIDTH, 0, PLAY_WIDTH - SIDEBAR_WIDTH, ACTUAL_HEIGHT);

		if(this.isPaused){
			g.setColor(Color.WHITE);
			g.setFont(this.bigFont);
			g.drawString("PAUSED", PLAY_WIDTH / 2 - this.bigFont.getSize() * 2, RENDERING_HEIGHT / 2);
		}

		g.dispose();

	}

	public void addRectStrokes(Graphics2D g){
		g.setStroke(this.dividerStroke);
		double minX = ACTUAL_WIDTH + (int)(.1D * PIXEL_SIZE);
		double maxX = TOTAL_WIDTH - (int)(.1D * PIXEL_SIZE);
		double minY = (int)(.5 * PIXEL_SIZE);
		double maxY = (int)(.5 * PIXEL_SIZE) + 4 * PIXEL_SIZE + (int)(.2D * PIXEL_SIZE);
		g.drawLine((int)minX, (int)minY, (int)minX, (int)maxY);
		g.drawLine((int)maxX, (int)minY, (int)maxX, (int)maxY);
		g.drawLine((int)minX, (int)minY, (int)maxX, (int)minY);
		g.drawLine((int)minX, (int)maxY, (int)maxX, (int)maxY);

		g.setColor(Color.WHITE);
		g.setFont(this.mediumFont);
		g.drawString("NEXT PIECE", ACTUAL_WIDTH + (int)(.1D * PIXEL_SIZE), (int)(.4 * PIXEL_SIZE) + 5 * PIXEL_SIZE);


		this.nextPiece.drawObject(g);
	}

	public void centerPreview(){
		double minX = ACTUAL_WIDTH + (int)(.1D * PIXEL_SIZE);
		double maxX = TOTAL_WIDTH - (int)(.1D * PIXEL_SIZE);
		double minY = (int)(.5 * PIXEL_SIZE);
		double maxY = (int)(.5 * PIXEL_SIZE) + 4 * PIXEL_SIZE + (int)(.2D * PIXEL_SIZE);
		double centerX = ((maxX - minX) / 2D) + minX;
		double centerY = ((maxY - minY) / 2D) + minY;

		this.nextPiece.centerPiece(centerX, centerY);

	}

	public void updatePieces() {

		for (JoinedPiece piece : this.settledPieces) {

			for (Rectangle rect1 : piece.getPieces()) {
				for (Rectangle rect2 : this.activePiece.getIncrementedPieces(Direction.DOWN)) {

					if (rect2.intersects(rect1)) {
						this.settledPieces.add(this.activePiece);
						this.spawnNewPiece();
						return;
					}

				}
			}

		}

		if (this.activePiece.getIncrementedY() > ACTUAL_HEIGHT) {
			this.settledPieces.add(this.activePiece);
			this.spawnNewPiece();
			return;
		}

		this.activePiece.translate(0, PIXEL_SIZE);

	}

	public void updateSettledAbove(int yVal) {

		//noinspection Convert2streamapi
		for (JoinedPiece piece : this.settledPieces) {
			if (piece.getMaxY() <= yVal && piece.getIncrementedY() <= ACTUAL_HEIGHT) {
				piece.translate(0, PIXEL_SIZE);
			}
		}
	}

	public void calculateRows() {

		List<Rectangle> toRemove = new ArrayList<>();

		for (Rectangle scanner : this.scanRects) {

			for (JoinedPiece piece : Collections.unmodifiableCollection(this.settledPieces)) {

				for (Rectangle rect : piece.getPieces()) {
					if (rect.contains(scanner)) {
						toRemove.add(rect);
					}
				}

			}

		}

		if (toRemove.size() == this.scanRects.size()) {

			//noinspection Convert2streamapi
			for (JoinedPiece piece : new ArrayList<>(this.settledPieces)) {
				if (piece.removeParts(toRemove)) {
					this.settledPieces.remove(piece);
				}
			}
			this.updateSettledAbove((int) (this.scanRects.get(0).getMinY() - (ROW_SCAN - 1)));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.calculateRows();

		}

	}

	public void spawnNewPiece() {

		for (int i = RENDERING_HEIGHT; i > 0; i -= PIXEL_SIZE) {

			this.calculateRows();
			for (Rectangle rectangle : this.scanRects) {
				rectangle.translate(0, -PIXEL_SIZE);
			}

		}

		this.scanRects.clear();

		for (int i = 30; i < PLAY_WIDTH; i += 60) {
			Rectangle temp = new Rectangle(i - 1, RENDERING_HEIGHT - ROW_SCAN - 1, 2, 2);
			this.scanRects.add(temp);
		}

		Random rand = new Random();
		this.activePiece = new JoinedPiece(this.nextPiece.getType());
		this.nextPiece = new JoinedPiece(TetrisPiece.values()[rand.nextInt(TetrisPiece.values().length)]);
		this.centerPreview();
		int x = rand.nextInt(PLAY_WIDTH / PIXEL_SIZE + 1);
		this.activePiece.setPosition(PIXEL_SIZE * x, -(this.activePiece.getPixelHeight() * PIXEL_SIZE + 1));
		while (this.activePiece.getMaxX() > PLAY_WIDTH) {
			this.activePiece.translate(-TetrisPanel.PIXEL_SIZE, 0);
		}

	}

	public boolean doesIntersectActive() {
		for (JoinedPiece piece : this.settledPieces) {
			for (Rectangle rect1 : piece.getPieces()) {
				for (Rectangle rect2 : this.activePiece.getPieces()) {
					if (rect1.intersects(rect2)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean doesIntersectActiveNext(Direction direction) {
		for (JoinedPiece piece : this.settledPieces) {
			for (Rectangle rect1 : piece.getPieces()) {
				for (Rectangle rect2 : this.activePiece.getIncrementedPieces(direction)) {
					if (rect1.intersects(rect2)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void drawImage() {
		Graphics paneGraphics = this.getGraphics();
		paneGraphics.drawImage(this.image, 0, 0, TOTAL_WIDTH, ACTUAL_HEIGHT, null);
		paneGraphics.dispose();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_P:
				this.isPaused = !this.isPaused;
				break;
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_R:
				this.activePiece.rotateShape();
				//Make sure that we aren't going over the sides
				while (this.activePiece.getMaxX() > PLAY_WIDTH) {
					this.activePiece.translate(-TetrisPanel.PIXEL_SIZE, 0);
				}
				while (this.activePiece.getMinX() < 0) {
					this.activePiece.translate(TetrisPanel.PIXEL_SIZE, 0);
				}
				while (this.activePiece.getMinY() < 0) {
					this.activePiece.translate(0, PIXEL_SIZE);
				}
				break;
			case KeyEvent.VK_RIGHT:
				//Simple check to make sure we can't override the boundary
				if (this.activePiece.getMaxX() <= PLAY_WIDTH - PIXEL_SIZE && !this.doesIntersectActive() && !this.doesIntersectActiveNext(Direction.RIGHT))
					this.activePiece.translate(TetrisPanel.PIXEL_SIZE, 0);
				break;
			case KeyEvent.VK_LEFT:
				//Another Simple Check
				if (this.activePiece.getMinX() >= PIXEL_SIZE && !this.doesIntersectActive() && !this.doesIntersectActiveNext(Direction.LEFT))
					this.activePiece.translate(-TetrisPanel.PIXEL_SIZE, 0);
				break;
			case KeyEvent.VK_DOWN:
				if (this.activePiece.getIncrementedY() < RENDERING_HEIGHT && !this.doesIntersectActiveNext(Direction.DOWN))
					this.activePiece.translate(0, PIXEL_SIZE);
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public enum Direction {

		LEFT,
		RIGHT,
		DOWN

	}

}
