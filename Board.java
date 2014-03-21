/**
 * This is board.
 * Class for generating a Board with n dimension.
 * Has methods for:
 * - filling in a board state from input
 * - checking if given board state is winning
 * @author kemble
 */



public class Board {

    /* A board has rows. Each row has a variable length based on
       hexagon pattern.
     */

	char[][] rows;

    // Just to test if board was successfully made
	public static void main(String[] args) {
		int n = 4;
        Board thisBoard = new Board(n);
		for (int i=0; i<thisBoard.rows.length; i++) {
            for (int j=0; j<thisBoard.rows[i].length; j++) {
                thisBoard.rows[i][j] = '-';
                System.out.printf("%c ", thisBoard.rows[i][j]);
            }
            System.out.println();
        }
	}

    // Constructor for making a Board with n rows
    // Max length of any row is 2*n-1
	public Board(int n) {
		rows = new char[2*n-1][];
		int i;
		for (i=0; i<n; i++) {
			rows[i] = new char[n+i];
            rows[2*n-(i+2)] = new char[n+i];
		}
	}

    // Method for filling in a boardstate into an empty board
    public void fillBoard(String file) {

    }

    // Method for checking if there is a winner
    public int testWin(Board board) {

    }
}
