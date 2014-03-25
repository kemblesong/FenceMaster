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

	Hex[][] rows;

    // Just to test if board was successfully made
	public static void main(String[] args) {
		int n = 4;
        Board thisBoard = new Board(n);
		for (int i=0; i<thisBoard.rows.length; i++) {
            for (int j=0; j<thisBoard.rows[i].length; j++) {
                System.out.printf("%c ", thisBoard.rows[i][j].getColour());
            }
            System.out.println();
        }
		
		thisBoard.rows[0][0].setColour('B');
		System.out.println(thisBoard.rows[1][0].getAdjacent()[1].getColour());
	}

    // Constructor for making a Board with n rows
    // Max length of any row is 2*n-1
	public Board(int n) {
		rows = new Hex[2*n-1][];
		int i, j;
		for (i=0; i<n; i++) {
			rows[i] = new Hex[n+i];
            rows[2*n-(i+2)] = new Hex[n+i];
            for (j=0; j < this.rows[i].length; j++) {
            	rows[i][j] = new Hex(i, j, '-', this);
            	rows[2*n-(i+2)][j] = new Hex(i, j, '-', this);
            }
		}
	}

    // Method for filling in a boardstate into an empty board
    public void fillBoard(String file) {

    }

    // Method for checking if there is a winner
    public int testWin(Board board) {
		return 0;
    }
}
