package com.ai.data;

import java.util.ArrayList;

public class CheckersData {
	public State M_lastChangedPlayer;

	public State[][] M_board;
	public State[][] M_lastBoard;

	public CheckersData() {
		this.M_board = new State[8][8];
		this.M_lastBoard = new State[8][8];
		this.init();
		this.fill();
	}

	public void init() {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				this.M_board[i][j] = State.EMPTY;
				this.M_lastBoard[i][j] = State.EMPTY;
			}
	}

	public void fill() {
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				if (row % 2 == col % 2) {
					if (row < 3)
						M_board[row][col] = State.BLACK;
					else if (row > 4)
						M_board[row][col] = State.RED;
					else
						M_board[row][col] = State.EMPTY;
				} else
					M_board[row][col] = State.EMPTY;
	}

	private boolean isJump(int fromRow, int fromCol, int toRow, int toCol) {
		return Math.abs(fromRow - toRow) == 2;
	}

	public State valueOf(int row, int col) {
		return this.M_board[row][col];
	}

	public void makeMove(State player, int fromRow, int fromCol, int toRow,
			int toCol) {
		if (this.M_lastChangedPlayer != player) {
			for (int i = 0; i < this.M_board.length; i++)
				System.arraycopy(this.M_board[i], 0, this.M_lastBoard[i], 0,
						this.M_board.length);

			this.M_lastChangedPlayer = player;
		}

		this.M_board[toRow][toCol] = this.M_board[fromRow][fromCol];
		this.M_board[fromRow][fromCol] = State.EMPTY;

		if (isJump(fromRow, fromCol, toRow, toCol)) {
			int jumpRow = (fromRow + toRow) / 2;
			int jumpCol = (fromCol + toCol) / 2;

			this.M_board[jumpRow][jumpCol] = State.EMPTY;
		}

		if (toRow == 0 && this.M_board[toRow][toCol] == State.RED) {
			this.M_board[toRow][toCol] = State.RED_KING;
		}
		if (toRow == 7 && this.M_board[toRow][toCol] == State.BLACK) {
			this.M_board[toRow][toCol] = State.BLACK_KING;
		}
	}

	public boolean undoLastMove(State player) {
		if (this.M_lastChangedPlayer != player) {
			for (int i = 0; i < this.M_lastBoard.length; i++)
				System.arraycopy(this.M_lastBoard[i], 0, this.M_board[i], 0,
						this.M_lastBoard.length);

			return true;
		}

		return false;
	}

	private boolean canJump(State player, int r1, int c1, int r2, int c2,
			int r3, int c3) {

		if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8) // out of range
			return false;

		if (this.M_board[r3][c3] != State.EMPTY) // can not jump because not
													// empty
			return false;

		if (player == State.RED) { // If player is RED, it can go from up to
									// down
			if (this.M_board[r1][c1] == State.RED && r3 > r1)
				return false;

			if (this.M_board[r2][c2] != State.BLACK
					&& this.M_board[r2][c2] != State.BLACK_KING)
				return false;

			return true;
		} else {
			if (this.M_board[r1][c1] == State.BLACK && r3 < r1)
				return false;
			if (this.M_board[r2][c2] != State.RED
					&& this.M_board[r2][c2] != State.RED_KING)
				return false;
			return true;
		}
	}

	private boolean canMove(State player, int r1, int c1, int r2, int c2) {

		if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
			return false;

		if (this.M_board[r2][c2] != State.EMPTY)
			return false;

		if (player == State.RED) {
			if (this.M_board[r1][c1] == State.RED && r2 > r1)
				return false;
			return true;
		} else {
			if (this.M_board[r1][c1] == State.BLACK && r2 < r1)
				return false;
			return true;
		}
	}

	public ArrayList<CheckersMove> getLegalMoves(State M_player) {
		if (M_player != State.RED && M_player != State.BLACK)
			return null;

		State M_playerKing;

		if (M_player == State.RED)
			M_playerKing = State.RED_KING;
		else
			M_playerKing = State.BLACK_KING;

		ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();

		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				if (M_board[row][col] == M_player
						|| M_board[row][col] == M_playerKing) {
					if (canJump(M_player, row, col, row + 1, col + 1, row + 2,
							col + 2))
						moves.add(new CheckersMove(null, row, col, row + 2,
								col + 2));
					if (canJump(M_player, row, col, row - 1, col + 1, row - 2,
							col + 2))
						moves.add(new CheckersMove(null, row, col, row - 2,
								col + 2));
					if (canJump(M_player, row, col, row + 1, col - 1, row + 2,
							col - 2))
						moves.add(new CheckersMove(null, row, col, row + 2,
								col - 2));
					if (canJump(M_player, row, col, row - 1, col - 1, row - 2,
							col - 2))
						moves.add(new CheckersMove(null, row, col, row - 2,
								col - 2));
				}

		if (moves.size() == 0)
			for (int row = 0; row < 8; row++)
				for (int col = 0; col < 8; col++)
					if (M_board[row][col] == M_player
							|| M_board[row][col] == M_playerKing) {
						if (canMove(M_player, row, col, row + 1, col + 1))
							moves.add(new CheckersMove(null, row, col, row + 1,
									col + 1));
						if (canMove(M_player, row, col, row - 1, col + 1))
							moves.add(new CheckersMove(null, row, col, row - 1,
									col + 1));
						if (canMove(M_player, row, col, row + 1, col - 1))
							moves.add(new CheckersMove(null, row, col, row + 1,
									col - 1));
						if (canMove(M_player, row, col, row - 1, col - 1))
							moves.add(new CheckersMove(null, row, col, row - 1,
									col - 1));
					}
		
		return moves;
	}

	public ArrayList<CheckersMove> getLegalJumpsFrom(State M_player, int row,
			int col) {
		if (M_player != State.RED && M_player != State.BLACK)
			return null;
		State M_playerKing;

		if (M_player == State.RED)
			M_playerKing = State.RED_KING;
		else
			M_playerKing = State.BLACK_KING;

		ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();

		if (M_board[row][col] == M_player || M_board[row][col] == M_playerKing) {
			if (canJump(M_player, row, col, row + 1, col + 1, row + 2, col + 2))
				moves.add(new CheckersMove(null, row, col, row + 2, col + 2));
			if (canJump(M_player, row, col, row - 1, col + 1, row - 2, col + 2))
				moves.add(new CheckersMove(null, row, col, row - 2, col + 2));
			if (canJump(M_player, row, col, row + 1, col - 1, row + 2, col - 2))
				moves.add(new CheckersMove(null, row, col, row + 2, col - 2));
			if (canJump(M_player, row, col, row - 1, col - 1, row - 2, col - 2))
				moves.add(new CheckersMove(null, row, col, row - 2, col - 2));
		}

		return moves;
	}

}
