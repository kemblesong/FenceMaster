package fencemaster;

import java.io.PrintStream;

public class Kembles implements Player, Piece {
	
	Board board;
	int colour, enemy;
	int nummoves;
	

	@Override
	public int getWinner() {
		return testWin(board, nummoves);
	}
	
	private int testWin(Board input, int nummoves) {
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
		if (n < 1 || !(p == BLACK || p == WHITE)) {
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
	Move choice = new Move(colour, false, -1, -1);
	// Set search depth depending on how many moves have been made
	// The fewer possible moves, the deeper we can search
	int depth = 0;
	if (nummoves > 5) {
		depth = 1;
	}
	if (board.numHexes - nummoves < 20) {
		depth = 2;
	}
	if (board.numHexes - nummoves < 15) {
		depth = 3;
	}
	if (board.numHexes - nummoves < 10) {
		depth = 4;
	}
	for (i = 0; i < testboard.rows.length; i++) {
		for (j = 0; j < testboard.rows[i].length; j++) {
			if (testboard.rows[i][j].colour == EMPTY) {
				current = minimax(testboard.applyMove(i, j, colour),false, this.nummoves, -1000, 1000, depth);
				if (current > best) {
                    choice.Row = i;
                    choice.Col = j;
                    best = current;
                }
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
		if (nummoves != 1 && m.IsSwap) {
			return -1;
		}
		/* Deliberately not checking to see if the opponent is using pieces
		 * of his assigned colour; if he wants to make moves that help me,
		 * that's fine by me.
		 */
		if (board.rows[m.Row][m.Col].colour != EMPTY && !m.IsSwap) {
			return -1;
		}
		board.rows[m.Row][m.Col] = new Hex (m.Row,m.Col,m.P,board);
		if (!m.IsSwap)this.nummoves += 1;
		return 0;
	}

	@Override
	public void printBoard(PrintStream output) {
		int i, j, q;
		// Add spaces to make the board line up, then print the board itself
		for (i = 0; i < board.rows.length; i++) {
			// If before the middle of the board
			if (i < board.dimension - 1) {
				// Add this many spaces
				for (q = 0; q < board.dimension - i - 1; q++) {
					output.print(" ");
				}
			}
			else {
				for (q = 0; q < i - board.dimension + 1; q++) {
					output.print(" ");
				}
			}
			// Print the board itself
			for (j = 0; j < board.rows[i].length; j++) {
				output.print(board.rows[i][j] + " ");
			}
			output.println();
		}
	}

    /**
     * Minimax search tree. Does it's thing. You know, the things.
     * @param testboard the board state in which the minimax search is being applied to
     * @param max if the current level is MAX or not
     * @param nummoves number of moves?
     * @param alpha best value for MAX found so far, initially -infinity
     * @param beta best value for MIN found so far, initially +infinity
     * @param depth set the maximum depth to explore in minimax tree in the initial method call
     * @return returns a score based on utiilty
     */
	private float minimax(Board testboard, boolean max, int nummoves, float alpha, float beta, int depth) {
        // Here we want to keep exploring deeper until we either hit a terminal state or reach a certain depth level
		int state = testWin(testboard, nummoves);
		if (state == EMPTY) {
			return 0;
		}
		else if (state == colour) {
			return 1000;
		}
		else if (state == enemy) {
			return -1000;
		}
		else if (depth == 0) {
			return utility(testboard);
		}
		int i, j;
		Board nextboard = testboard.clone();

		if (max) {
            loop:
			for (i = 0; i < testboard.rows.length; i++) {
				for (j = 0; j < testboard.rows[i].length; j++) {
					if (testboard.rows[i][j].colour == EMPTY) {
						alpha = minimax(nextboard.applyMove(i, j, colour), false, nummoves +1, alpha, beta, depth-1);
						if (alpha >= beta) {
							break loop;
						}
						nextboard = testboard;
					}
				}
			}
			return alpha;
        } else {
            loop:
            for (i = 0; i < testboard.rows.length; i++) {
                for (j = 0; j < testboard.rows[i].length; j++) {
                    if (testboard.rows[i][j].colour == EMPTY) {
                        beta = minimax(nextboard.applyMove(i, j, enemy), true, nummoves +1, alpha, beta, depth-1);
                        if (alpha >= beta) {
                            break loop;
                        }
                        nextboard = testboard.clone();
                    }
                }
            }
            return beta;
        }
	}
	
	private float utility(Board board) {

        float a = countOccupiedEdges(board);
        float b = countAdjacentPieces(board);
        float c = cornerSucks(board);
        return 4*a+b+3*c;
	}

    /*
     * The following are a bunch of methods used by the utility method to evaluate the board state
     */

    // Counts how many of the 6 edges on the board are occupied by one of our pieces, excluding corners.
    private float countOccupiedEdges(Board board) {
        int count = 0;
        Hex currentHex;
        for (int i=0; i<board.rows[0].length; i++) {
            currentHex = board.rows[0][i];
            if ((currentHex.colour == this.colour) && !board.isCorner(currentHex,board.dimension)) {
                count += 1;
                break;
            }
        }
        for (int i=0; i<(board.dimension); i++) {
            currentHex = board.rows[i][0];
            if ((currentHex.colour == this.colour) && !board.isCorner(currentHex,board.dimension)) {
                count += 1;
                break;
            }
        }
        for (int i=board.dimension; i<board.rows.length; i++) {
            currentHex = board.rows[i][0];
            if ((currentHex.colour == this.colour) && !board.isCorner(currentHex,board.dimension)) {
                count += 1;
                break;
            }
        }
        for (int i=0; i<board.dimension; i++) {
            currentHex = board.rows[i][board.rows[i].length-1];
            if ((currentHex.colour == this.colour) && !board.isCorner(currentHex,board.dimension)) {
                count += 1;
                break;
            }
        }
        for (int i=board.dimension; i<board.rows.length; i++) {
            currentHex = board.rows[i][board.rows[i].length-1];
            if ((currentHex.colour == this.colour) && !board.isCorner(currentHex,board.dimension)) {
                count += 1;
                break;
            }
        }
        for (int i=0; i<board.rows[0].length; i++) {
            currentHex = board.rows[board.rows.length-1][i];
            if ((currentHex.colour == this.colour) && !board.isCorner(currentHex,board.dimension)) {
                count += 1;
                break;
            }
        }
        return count;
    }

    // Counts how many adjacent pieces of our colour are next to pieces of our colour
    // +1 score if our hex has adjacent pieces
    // -1 score if an enemy hex has adjacent pieces
    private float countAdjacentPieces(Board board) {
        int count = 0;
        int i, j, k;
        int numAdj;
        for (i = 0; i < board.rows.length; i++) {
            for (j = 0; j < board.rows[i].length; j++) {
                if (board.rows[i][j].colour == this.colour) {
                    Hex[] adjHexes = board.rows[i][j].getAdjacent();
                    numAdj = 0;
                    for (k=0; k<adjHexes.length; k++) {
                        if (adjHexes[k] != null) {
                            if (adjHexes[k].colour == this.colour) {
                                numAdj += 1;
                            }
                        }
                    }
                    if (numAdj > 0) {
                        count += 1;
                    }
                } else if (board.rows[i][j].colour == this.enemy) {
                    Hex[] adjHexes = board.rows[i][j].getAdjacent();
                    numAdj = 0;
                    for (k=0; k<adjHexes.length; k++) {
                        if (adjHexes[k] != null) {
                            if (adjHexes[k].colour == this.enemy) {
                                numAdj += 1;
                            }
                        }
                    }
                    if (numAdj > 0) {
                        count -= 1;
                    }
                }
            }
        }
        return count;
    }

    // Method for discouraging corner moves; they kind of suck for the most part
    private float cornerSucks(Board board) {
        if (board.rows[0][0].colour == this.colour ||
            board.rows[board.dimension-1][0].colour == this.colour ||
            board.rows[0][board.dimension-1].colour == this.colour ||
            board.rows[board.rows.length-1][0].colour == this.colour ||
            board.rows[board.dimension-1][board.rows.length-1].colour == this.colour ||
            board.rows[board.rows.length-1][board.dimension-1].colour == this.colour ) {
            return -1;
        }
        return 0;
    }
    
}
