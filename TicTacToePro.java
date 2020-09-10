import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.*;

@SuppressWarnings("serial")
public class TicTacToePro extends JFrame {
	// Named-constants for the game board

	public int xWCount = 0;
	public int oWCount = 0;
	public int dCount = 0;
	// Named-constants of the various dimensions used for graphics drawing
	final int CELL_SIZE = 100; // cell width and height (square)
	final int CANVAS_WIDTH = CELL_SIZE * 1; // the drawing canvas
	final int CANVAS_HEIGHT = CELL_SIZE * 1;
	final int GRID_WIDTH = 8; // Grid-line's width
	final int GRID_WIDTH_HALF = GRID_WIDTH / 2; // Grid-line's half-width
	// Symbols (X and O) are displayed inside a cell, with padding from border
	final int CELL_PADDING = CELL_SIZE / 6;
	final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
	final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
	public Random random = new Random();
	public int rows3Comp[][] = { { 0, 2 }, { 3, 5 }, { 6, 8 }, { 0, 6 }, { 1, 7 }, { 2, 8 }, { 0, 8 }, { 2, 6 } };
	static boolean computer = false;

	int compROW = 0;
	int compCOL = 0;

	// Use an enumeration (inner class) to represent the various states of the game
	public enum GameState {
		PLAYING, DRAW, X_WON, O_WON
	}

	private GameState currentState; // the current game state

	// Use an enumeration (inner class) to represent the seeds and cell contents
	public enum GameChar {
		EMPTY, X_PLAYER, O_PLAYER
	}

	private GameChar currentPlayer; // the current player

	private GameChar[][] board; // Game board of ROWS-by-COLS cells
	private DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
	private JLabel statusBar; // Status Bar

	/** Constructor to setup the game and the GUI components */

	public TicTacToePro(int rows, int cols) {

		int ROWS = rows; // ROWS by COLS cells
		int COLS = cols;

		canvas = new DrawCanvas(ROWS, COLS); // Construct a drawing canvas (a JPanel)
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH * COLS, CANVAS_HEIGHT * ROWS));

		// The canvas (JPanel) fires a MouseEvent upon mouse-click
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { // mouse-clicked handler

				int mouseX = e.getX();
				int mouseY = e.getY();
				// Get the row and column clicked
				int rowSelected = mouseY / CELL_SIZE;
				int colSelected = mouseX / CELL_SIZE;

				if (currentState == GameState.PLAYING) {
					if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0 && colSelected < COLS
							&& board[rowSelected][colSelected] == GameChar.EMPTY) {

						board[rowSelected][colSelected] = currentPlayer; // Make a move

						updateGame(currentPlayer, rowSelected, colSelected, ROWS, COLS); // update state
						// Switch player

						currentPlayer = (currentPlayer == GameChar.X_PLAYER) ? GameChar.O_PLAYER : GameChar.X_PLAYER;
					}
				} else { // game over
					initGame(rows, cols); // restart the game

				}
				// Refresh the drawing canvas
				repaint(); // Call-back paintComponent().
			}
		});

		// Setup the status bar (JLabel) to display status message
		statusBar = new JLabel("  ");
		statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack(); // pack all the components in this JFrame
		setTitle("Tic Tac Toe");
		setVisible(true); // show this JFrame

		board = new GameChar[ROWS][COLS]; // allocate array
		initGame(rows, cols); // initialize the game board contents and game variables
	}

	// computer game
	public TicTacToePro(int rows, int cols, boolean cmp) {

		int ROWS = rows; // ROWS by COLS cells
		int COLS = cols;

		canvas = new DrawCanvas(ROWS, COLS); // Construct a drawing canvas (a JPanel)
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH * COLS, CANVAS_HEIGHT * ROWS));

		// The canvas (JPanel) fires a MouseEvent upon mouse-click
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { // mouse-clicked handler

				int mouseX = e.getX();
				int mouseY = e.getY();
				// Get the row and column clicked
				int rowSelected = mouseY / CELL_SIZE;
				int colSelected = mouseX / CELL_SIZE;

				if (currentState == GameState.PLAYING) {
					if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0 && colSelected < COLS
							&& board[rowSelected][colSelected] == GameChar.EMPTY) {

						board[rowSelected][colSelected] = GameChar.X_PLAYER; // Make a move

						updateGame(GameChar.X_PLAYER, rowSelected, colSelected, ROWS, COLS); // update state
						// Switch player
						currentPlayer = (currentPlayer == GameChar.X_PLAYER) ? GameChar.O_PLAYER : GameChar.X_PLAYER;
						currentPlayer = (currentPlayer == GameChar.X_PLAYER) ? GameChar.O_PLAYER : GameChar.X_PLAYER;
						nextMove();
						updateGame(GameChar.O_PLAYER, compROW, compCOL, ROWS, COLS);

					}
				} else { // game over
					initGame(rows, cols); // restart the game

				}
				// Refresh the drawing canvas
				repaint(); // Call-back paintComponent().
			}
		});

		// Setup the status bar (JLabel) to display status message
		statusBar = new JLabel("  ");
		statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack(); // pack all the components in this JFrame
		setTitle("Tic Tac Toe");
		setVisible(true); // show this JFrame

		board = new GameChar[ROWS][COLS]; // allocate array
		initGame(rows, cols); // initialize the game board contents and game variables
	}

	public void county() {

		String result = "Result Log";

		if (JOptionPane.showConfirmDialog(null,
				"Player 1 (X) has " + xWCount + " wins, Player 2 (O) has " + oWCount + " wins, and there have been "
						+ dCount + " draws.\n" + "Play again?",
				result, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			System.exit(0);
		}

	}

	// Play X in the best spot
	void nextMove() {
		int rowVal = -1;
		int colVal = -1;

		int r = findRow(GameChar.O_PLAYER); // complete a row of O and win if possible
		if (r < 0)
			r = findRow(GameChar.X_PLAYER); // or try to block X from winning
		if (r < 0) { // otherwise move randomly
			do {
				r = random.nextInt(9);
				if (r == 0) {
					rowVal = 0;
					colVal = 0;
				} else if (r == 1) {
					rowVal = 0;
					colVal = 1;
				} else if (r == 2) {
					rowVal = 0;
					colVal = 2;
				} else if (r == 3) {
					rowVal = 1;
					colVal = 0;
				} else if (r == 4) {
					rowVal = 1;
					colVal = 1;
				} else if (r == 5) {
					rowVal = 1;
					colVal = 2;
				} else if (r == 6) {
					rowVal = 2;
					colVal = 0;
				} else if (r == 7) {
					rowVal = 2;
					colVal = 1;
				} else if (r == 8) {
					rowVal = 2;
					colVal = 2;
				}

			}

			while (board[rowVal][colVal] != GameChar.EMPTY);
		}

		if (r == 0) {
			rowVal = 0;
			colVal = 0;
		} else if (r == 1) {
			rowVal = 0;
			colVal = 1;
		} else if (r == 2) {
			rowVal = 0;
			colVal = 2;
		} else if (r == 3) {
			rowVal = 1;
			colVal = 0;
		} else if (r == 4) {
			rowVal = 1;
			colVal = 1;
		} else if (r == 5) {
			rowVal = 1;
			colVal = 2;
		} else if (r == 6) {
			rowVal = 2;
			colVal = 0;
		} else if (r == 7) {
			rowVal = 2;
			colVal = 1;
		} else if (r == 8) {
			rowVal = 2;
			colVal = 2;
		}

		board[rowVal][colVal] = GameChar.O_PLAYER;

		compROW = rowVal;
		compCOL = colVal;

	}

	// Return 0-8 for the position of a blank spot in a row if the
	// other 2 spots are occupied by player, or -1 if no spot exists
	int findRow(GameChar theChar) {
		for (int i = 0; i < 8; i++) {
			int result = find1Row(theChar, rows3Comp[i][0], rows3Comp[i][1]);
			if (result >= 0)
				return result;
		}
		return -1;
	}

	// If 2 of 3 spots in the row from position[a] to position[b]
	// are occupied by player and the third is blank, then return the
	// index of the blank spot, else return -1.
	int find1Row(GameChar theChar, int a, int b) {

		int rowVal = -1;
		int colVal = -1;

		if (a == 0) {
			rowVal = 0;
			colVal = 0;
		} else if (a == 1) {
			rowVal = 0;
			colVal = 1;
		} else if (a == 2) {
			rowVal = 0;
			colVal = 2;
		} else if (a == 3) {
			rowVal = 1;
			colVal = 0;
		} else if (a == 4) {
			rowVal = 1;
			colVal = 1;
		} else if (a == 5) {
			rowVal = 1;
			colVal = 2;
		} else if (a == 6) {
			rowVal = 2;
			colVal = 0;
		} else if (a == 7) {
			rowVal = 2;
			colVal = 1;
		} else if (a == 8) {
			rowVal = 2;
			colVal = 2;
		}

		int browVal = -1;
		int bcolVal = -1;

		if (b == 0) {
			browVal = 0;
			bcolVal = 0;
		} else if (b == 1) {
			browVal = 0;
			bcolVal = 1;
		} else if (b == 2) {
			browVal = 0;
			bcolVal = 2;
		} else if (b == 3) {
			browVal = 1;
			bcolVal = 0;
		} else if (b == 4) {
			browVal = 1;
			bcolVal = 1;
		} else if (b == 5) {
			browVal = 1;
			bcolVal = 2;
		} else if (b == 6) {
			browVal = 2;
			bcolVal = 0;
		} else if (b == 7) {
			browVal = 2;
			bcolVal = 1;
		} else if (b == 8) {
			browVal = 2;
			bcolVal = 2;
		}

		int c = (a + b) / 2;
		int crowVal = -1;
		int ccolVal = -1;

		if (c == 0) {
			crowVal = 0;
			ccolVal = 0;
		} else if (c == 1) {
			crowVal = 0;
			ccolVal = 1;
		} else if (c == 2) {
			crowVal = 0;
			ccolVal = 2;
		} else if (c == 3) {
			crowVal = 1;
			ccolVal = 0;
		} else if (c == 4) {
			crowVal = 1;
			ccolVal = 1;
		} else if (c == 5) {
			crowVal = 1;
			ccolVal = 2;
		} else if (c == 6) {
			crowVal = 2;
			ccolVal = 0;
		} else if (c == 7) {
			crowVal = 2;
			ccolVal = 1;
		} else if (c == 8) {
			crowVal = 2;
			ccolVal = 2;
		}

		// middle spot
		if (board[rowVal][colVal] == theChar && board[browVal][bcolVal] == theChar
				&& board[crowVal][ccolVal] == GameChar.EMPTY)
			return c;
		if (board[rowVal][colVal] == theChar && board[browVal][bcolVal] == GameChar.EMPTY
				&& board[crowVal][ccolVal] == theChar)
			return b;
		if (board[rowVal][colVal] == GameChar.EMPTY && board[browVal][bcolVal] == theChar
				&& board[crowVal][ccolVal] == theChar)
			return a;
		return -1;
	}

	/** Initialize the game-board contents and the status */
	public void initGame(int rNum, int cNum) {
		for (int row = 0; row < rNum; ++row) {
			for (int col = 0; col < cNum; ++col) {
				board[row][col] = GameChar.EMPTY; // all cells empty
			}
		}
		currentState = GameState.PLAYING; // ready to play
		currentPlayer = GameChar.X_PLAYER; // cross plays first

	}

	/**
	 * Update the currentState after the player with "theChar" has placed on
	 * (rowSelected, colSelected).
	 */
	public void updateGame(GameChar theChar, int rowSelected, int colSelected, int rNum, int cNum) {
		if (hasWon(theChar, rowSelected, colSelected, rNum, rNum)) { // check for win
			currentState = (theChar == GameChar.X_PLAYER) ? GameState.X_WON : GameState.O_WON;
			if (theChar == GameChar.X_PLAYER) {
				++xWCount;
			} else {
				++oWCount;
			}
			repaint();
			county();

		} else if (isDraw(rNum, rNum)) { // check for draw
			currentState = GameState.DRAW;
			++dCount;
			// System.out.println("else if");
			repaint();
			county();
		}
		// Otherwise, no change to current state (still GameState.PLAYING).
	}

	/** Return true if it is a draw (i.e., no more empty cell) */
	public boolean isDraw(int rNum, int cNum) {
		for (int row = 0; row < rNum; row++) {
			for (int col = 0; col < cNum; col++) {
				if (board[row][col] == GameChar.EMPTY) {
					return false; // an empty cell found, not draw, exit
				}
			}
		}

		return true; // no more empty cell, it's a draw
	}

	/**
	 * Return true if the player with "theChar" has won after placing at
	 * (rowSelected, colSelected)
	 */
	public boolean hasWon(GameChar theChar, int rowSelected, int colSelected, int rNum, int cNum) {

		int count = 0;
		for (int col = 0; col < rNum; ++col) {
			if (board[rowSelected][col] == theChar) {
				++count;
				if (count == rNum)
					return true; // found
			}
		}
		// Check column and diagonals

		count = 0;

		for (int row = 0; row < rNum; ++row) {
			if (board[row][colSelected] == theChar) {
				++count;
				if (count == rNum)
					return true; // found
				// else {

				// count = 0; // reset and count again if not consecutive
			} // }
		}

		count = 0;

		// 1st diag

		for (int row = 0; row < rNum; ++row) {
			if (board[row][row] == theChar) {
				++count;
				if (count == rNum)
					return true; // found
			}
		}

		count = 0;

		//

		for (int row = rNum - 1; row >= 0; row--) {
			if (board[row][cNum - 1 - row] == theChar) {
				++count;
				if (count == rNum)
					return true; // found
			}
		}

		count = 0;

		return false; // no 4-in-a-line found
	}

	/**
	 * Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
	 */
	class DrawCanvas extends JPanel {

		int ROWS;
		int COLS;

		public DrawCanvas(int rNum, int cNum) {
			ROWS = rNum;
			COLS = cNum;
		}

		@Override
		public void paintComponent(Graphics g) { // invoke via repaint()
			super.paintComponent(g); // fill background
			setBackground(Color.WHITE); // set its background color

			// Draw the grid-lines
			g.setColor(Color.LIGHT_GRAY);
			for (int row = 1; row < ROWS; ++row) {
				g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF, (CANVAS_WIDTH * COLS) - 1, GRID_WIDTH, GRID_WIDTH,
						GRID_WIDTH);
			}
			for (int col = 1; col < COLS; ++col) {
				g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0, GRID_WIDTH, (CANVAS_HEIGHT * ROWS) - 1,
						GRID_WIDTH, GRID_WIDTH);
			}

			// Draw the Seeds of all the cells if they are not empty
			// Use Graphics2D which allows us to set the pen's stroke
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Graphics2D
																												// only
			for (int row = 0; row < ROWS; ++row) {
				for (int col = 0; col < COLS; ++col) {
					int x1 = col * CELL_SIZE + CELL_PADDING;
					int y1 = row * CELL_SIZE + CELL_PADDING;
					if (board[row][col] == GameChar.X_PLAYER) {
						g2d.setColor(Color.RED);
						int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
						int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
						g2d.drawLine(x1, y1, x2, y2);
						g2d.drawLine(x2, y1, x1, y2);
					} else if (board[row][col] == GameChar.O_PLAYER) {
						g2d.setColor(Color.BLUE);
						g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
					}
				}
			}

			// Print status-bar message
			if (currentState == GameState.PLAYING) {
				statusBar.setForeground(Color.BLACK);
				if (currentPlayer == GameChar.X_PLAYER) {
					statusBar.setText("X's Turn");
				} else {
					statusBar.setText("O's Turn");
				}
			} else if (currentState == GameState.DRAW) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("It's a Draw! Click to play again.");
			} else if (currentState == GameState.X_WON) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("'X' Won! Click to play again.");
			} else if (currentState == GameState.O_WON) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("'O' Won! Click to play again.");
			}

		}
	}

	/** The entry main() method */
	public static void main(String[] args) {

		JFrame f = new JFrame();// creating instance of JFrame

		JToggleButton tb = new JToggleButton("Start 2-Player Game");
		JToggleButton tbC = new JToggleButton("Play Against Computer");

		final JTextField tf1 = new JTextField();
		tf1.setBounds(125, 170, 50, 20);

		JLabel labelft = new JLabel("Enter the # here:");

		labelft.setFont(new Font("Arial", Font.PLAIN, 12));
		labelft.setLocation(35, -20);

		labelft.setSize(400, 400);

		f.add(labelft);

		tb.setBounds(17, 200, 180, 40);// x axis, y axis, width, height
		tbC.setBounds(210, 200, 180, 40);// x axis, y axis, width, height

		f.getContentPane().add(tf1);

		f.getContentPane().add(tb);
		f.getContentPane().add(tbC);

		f.setSize(420, 300);// 400 width and 500 height
		f.getContentPane().setLayout(null);// using no layout managers
		f.setLocationRelativeTo(null);
		f.setVisible(true);// making the frame visible

		JLabel label1 = new JLabel("** Welcome to Tic-Tac-Toe **");
		label1.setFont(new Font("Verdana", Font.BOLD, 16));
		label1.setLocation(70, -160);

		label1.setSize(400, 400);

		f.add(label1);
		f.setVisible(true);

		// 2player instructions

		JLabel label2p = new JLabel("To play a two-player game (where the first player is X and the second is");

		label2p.setFont(new Font("Arial", Font.PLAIN, 12));
		label2p.setLocation(17, -130);

		label2p.setSize(400, 400);

		f.add(label2p);

		JLabel label2p1 = new JLabel("O), enter the # of the # by # board you would like to play on, then click");

		label2p1.setFont(new Font("Arial", Font.PLAIN, 12));
		label2p1.setLocation(17, -115);

		label2p1.setSize(400, 400);

		f.add(label2p1);

		JLabel label2p2 = new JLabel("the \"Start 2-Player Game\" button.");

		label2p2.setFont(new Font("Arial", Font.PLAIN, 12));
		label2p2.setLocation(17, -100);

		label2p2.setSize(400, 400);

		f.add(label2p2);

		JLabel label2p3 = new JLabel("To play a one-player game (where the player is X and the computer is O,");

		label2p3.setFont(new Font("Arial", Font.PLAIN, 12));
		label2p3.setLocation(17, -70);

		label2p3.setSize(400, 400);

		f.add(label2p3);

		JLabel label2p4 = new JLabel("click the \"Play Against Computer\" button.");

		label2p4.setFont(new Font("Arial", Font.PLAIN, 12));
		label2p4.setLocation(17, -55);

		label2p4.setSize(400, 400);

		f.add(label2p4);

		f.setVisible(true);

		JFrame window = new JFrame("Tic-Tac-Toe");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		tbC.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JToggleButton btn = (JToggleButton) e.getSource();
				btn.setText(btn.isSelected() ? "Play Against Computer" : "Computer Loading...");
				computer = true;
				if (btn.isSelected()) {

					computer = true;
					f.dispose();

					System.out.println("You are now playing against the computer. Enjoy!");

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							new TicTacToePro(3, 3, computer); // Let the constructor do the job
						}
					});

				}

			}
		});

		tb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JToggleButton btn = (JToggleButton) e.getSource();
				btn.setText(btn.isSelected() ? "pushed" : "push me");

				String s1 = tf1.getText();
				String s2 = s1;

				if (s1.equals("")) {
					s1 = "3";
				}
				if (s2.equals("")) {
					s2 = "3";
				}

				int a = Integer.parseInt(s1);
				int b = Integer.parseInt(s2);

				System.out.println("You are now playing a two-player game on a " + a + " by " + b + " board. Enjoy!");

				if (btn.isSelected()) {
					f.dispose();

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							new TicTacToePro(a, b); // Let the constructor do the job
						}
					});

				}
			}
		});

	}
}
