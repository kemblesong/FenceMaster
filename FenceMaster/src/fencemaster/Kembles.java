package fencemaster;

import java.io.PrintStream;

public class Kembles implements Player, Piece {
	
	Board board;
	int colour, enemy;

	@Override
	public int getWinner() {
		return testBoard(board);
	}
	
	private int testBoard(Board input) {
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
		if (black && white)
		{
			return INVALID;
		}
		if (white)
		{
			return WHITE;
		}
		if (black)
		{
			return BLACK;
		}
		return EMPTY;
	}

	@Override
	public int init(int n, int p) {
		if (n < 1 || !(p == BLACK || p == WHITE)) {
            return -1;
        }
		board = new Board(n);
		this.colour = p;
		if(p == WHITE) this.enemy = BLACK;
		else this.enemy = WHITE;
		return 0;
	}

	@Override
	public Move makeMove() {
	int i, j;
	float current, best = -10000;
	Board nextBoard = board;
	Move choice = new Move();
	for (i = 0; i < board.rows.length; i++) {
		for (j = 0; j < board.rows[i].length; j++) {
			current = minimax(nextBoard.applyMove(i, j, colour),false);
			if (current > best) choice = new Move(colour, false, i, j);
			nextBoard = board;
		}
	}
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
	
	private float minimax(Board board, boolean max) {
		if (testBoard(board) > 0) {
			return utility(board);
		}
		int i, j;
		Board nextBoard = board;
		float util, ideal = 0;
		if (max) {
			for (i = 0; i < board.rows.length; i++) {
				for (j = 0; j < board.rows[i].length; j++) {
					util = minimax(nextBoard.applyMove(i, j, colour), false);
					if (util > ideal) {
						ideal = util;
					}
					nextBoard = board;
				}
			}
			return ideal;
		}
		for (i = 0; i < board.rows.length; i++) {
			for (j = 0; j < board.rows[i].length; j++) {
				util = minimax(nextBoard.applyMove(i, j, enemy), true);
				if (util < ideal) {
					ideal = util;
				}
				nextBoard = board;
			}
		}
		return ideal;
	}
	
	private float utility(Board board) {
		int state = testBoard(board);
        int total = 0;
        if (state == this.colour) {
            total += 5;
        } else if (state == this.enemy) {
            total -= 5;
        }

        return total;
	}
}