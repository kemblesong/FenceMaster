package fencemaster;

import java.io.PrintStream;

public class Kembles implements Player, Piece {
	
	Board board;
	int colour, enemy;
	int nummoves;

	@Override
	public int getWinner() {
		return testBoard(board, nummoves);
	}
	
	private int testBoard(Board input, int nummoves) {
		boolean white = false;
		boolean black = false;
        if (input.findLoop(board, 'B')) {
            black = true;
        }
        if (input.findLoop(board, 'W')) {
            white = true;
        }
        if (input.findTripod(board, 'B')) {
            black = true;
        }
        if (input.findTripod(board, 'W')) {
            white = true;
        }
		if (white)
		{
			return WHITE;
		}
		if (black)
		{
			return BLACK;
		}
		if (nummoves == board.numHexes)
		{
			return EMPTY;
		}
		return INVALID;
	}

	@Override
	public int init(int n, int p) {
		if (n < 1 || !(p == BLACK || p == WHITE)) 
		{
			return -1;
		}
		board = new Board(n);
		this.colour = p;
		if(p == WHITE) this.enemy = BLACK;
		else this.enemy = WHITE;
		this.nummoves = 0;
		return 0;
	}

	@Override
	public Move makeMove() {
	int i, j;
	float current, best = -10000;
	Board testboard = board.clone();
	Move choice = new Move();
	for (i = 0; i < testboard.rows.length; i++) {
		for (j = 0; j < testboard.rows[i].length; j++) {
			if (testboard.rows[i][j].colour == EMPTY) {
				current = minimax(testboard.applyMove(i, j, colour),false, this.nummoves);
				if (current > best) choice = new Move(colour, false, i, j);
				testboard = board.clone();
			}
		}
	}
		this.nummoves += 1;
		board.rows[choice.Row][choice.Col] = new Hex (choice.Row,choice.Col,choice.P,board);
		return choice;
	}

	@Override
	public int opponentMove(Move m) {
		if (!board.isLegal(m.Row, m.Col)) {
			return -1;
		}
		if (!(m.P == BLACK || m.P == WHITE)) {
			return -1;
		}
		/* Deliberately not checking to see if the opponent is using pieces
		 * of his assigned colour; if he wants to make moves that help me,
		 * that's fine by me.
		 */
		if (board.rows[m.Row][m.Col].colour != EMPTY && !m.IsSwap) {
			return -1; // Doesn't check to see whether opponent actually can use swap rule
		}
		board.rows[m.Row][m.Col] = new Hex (m.Row,m.Col,m.P,board);
		this.nummoves += 1;
		return 0;
	}

	@Override
	public void printBoard(PrintStream output) {
		int i, j;
		for (i = 0; i < board.rows.length; i++) {
			for (j = 0; j < board.rows[i].length; j++) {
				output.print(board.rows[i][j] + " ");
			}
			output.println();
		}
	}
	
	private float minimax(Board testboard, boolean max, int nummoves) {
		testboard.output();
		if (testBoard(testboard, nummoves) != INVALID) {
			return utility(testboard);
		}
		int i, j;
		Board nextboard = testboard.clone();
		float util = 0, ideal = 0;
		if (max) {
			for (i = 0; i < testboard.rows.length; i++) {
				for (j = 0; j < testboard.rows[i].length; j++) {
					if (testboard.rows[i][j].colour == EMPTY) {
						util = minimax(nextboard.applyMove(i, j, colour), false, nummoves +1);
						if (util > ideal) {
							ideal = util;
						}
						nextboard = testboard;
					}
				}
			}
			return ideal;
		}
		for (i = 0; i < testboard.rows.length; i++) {
			for (j = 0; j < testboard.rows[i].length; j++) {
				if (testboard.rows[i][j].colour == EMPTY) {
					util = minimax(nextboard.applyMove(i, j, enemy), true, nummoves +1);
					if (util < ideal) {
						ideal = util;
					}
					nextboard = testboard.clone();
				}
			}
		}
		return ideal;
	}
	
	private float utility(Board board) {
		return 0;
	}
}