package fencemaster;

import java.io.PrintStream;

public class Kembles implements Player, Piece {
	
	Board board;
	int colour, enemy;

	@Override
	public int getWinner() {
		boolean white = false;
		boolean black = false;
        if (board.findLoop(board, 'B')) {
            black = true;
        }
        if (board.findLoop(board, 'W')) {
            white = true;
        }
        if (board.findTripod(board, 'B')) {
            black = true;
        }
        if (board.findTripod(board, 'W')) {
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
		if (n < 1 || !(p == BLACK || p == WHITE)) 
		{
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
	Board nextboard = board;
	Move choice = new Move();
	for (i = 0; i < board.rows.length; i++) {
		for (j = 0; j < board.rows[i].length; j++) {
			current = minimax(nextboard.applyMove(i, j, translatePiece(colour)),false);
			if (current > best) choice = new Move(colour, false, i, j);
			nextboard = board;
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
		if (hexToPiece(board.rows[m.Row][m.Col]) != EMPTY && !m.IsSwap) {
			return -1; // Doesn't check to see whether opponent actually can use swap rule
		}
		board.rows[m.Row][m.Col] = new Hex (m.Row,m.Col,translatePiece(m.P),board);
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
		if (getWinner() > 0) {
			return utility(board);
		}
		int i, j;
		Board nextboard = board;
		float util, ideal = 0;
		if (max) {
			for (i = 0; i < board.rows.length; i++) {
				for (j = 0; j < board.rows[i].length; j++) {
					util = minimax(nextboard.applyMove(i, j, translatePiece(colour)), false);
					if (util > ideal) {
						ideal = util;
					}
					nextboard = board;
				}
			}
			return ideal;
		}
		for (i = 0; i < board.rows.length; i++) {
			for (j = 0; j < board.rows[i].length; j++) {
				util = minimax(nextboard.applyMove(i, j, translatePiece(enemy)), true);
				if (util < ideal) {
					ideal = util;
				}
				nextboard = board;
			}
		}
		return ideal;
	}
	
	private float utility(Board board) {
		return 0;
	}
	
	@SuppressWarnings("unused")
	private Move hexToMove(Hex input) {
		return new Move(this.colour, false, input.y, input.x);
	}
	
	private int hexToPiece(Hex input) {
		if (input.colour == '-') {
			return EMPTY;
		}
		if (input.colour == 'B') {
			return BLACK;
		}
		if (input.colour == 'W') {
			return WHITE;
		}
		return INVALID;
	}
	
	private char translatePiece(int input) {
		if (input == BLACK) {
			return 'B';
		}
		if (input == WHITE) {
			return 'W';
		}
		return '-';
	}

}
