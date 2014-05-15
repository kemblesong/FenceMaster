package fencemaster;

import java.io.PrintStream;

public class Kembles implements Player, Piece {
	
	Board board;
	int colour;

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

	
	public int init(int n, int p) {
		if (n < 1 || !(p == BLACK || p == WHITE)) 
		{
			return -1;
		}
		board = new Board(n);
		this.colour = p;
		return 0;
	}

	@Override
	public Move makeMove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int opponentMove(Move m) {
		if (!board.isLegal(m.Row, m.Col)) {
			return -1;
		}
		if (!(m.P == BLACK || m.P == WHITE)) {
			return -1;
		}
		if (translateHex(board.rows[m.Row][m.Col]) != EMPTY && !m.IsSwap) {
			return -1;
		}
		board.rows[m.Row][m.Col] = new Hex (m.Row,m.Col,translateMove(m.P),board);
		return 0;
	}

	@Override
	public void printBoard(PrintStream output) {
		int i, j;
		for (i = 0; i < board.rows.length; i++) {
			for (j = 0; j < board.rows[i].length; j++) {
				output.println(board.rows[i][j]);
			}
		}
	}
	
	private int translateHex(Hex input) {
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
	
	private char translateMove(int input) {
		if (input == BLACK) {
			return 'B';
		}
		if (input == WHITE) {
			return 'W';
		}
		return '-';
	}

}
