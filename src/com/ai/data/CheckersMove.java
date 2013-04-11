package com.ai.data;

import com.ai.data.State;

public class CheckersMove {
	public State player;
	public int fromRow;
	public int fromCol;
	public int toRow;
	public int toCol;

	public CheckersMove(State player, int fromRow, int fromCol, int toRow,
			int toCol) {
		this.player = player;
		this.fromRow = fromRow;
		this.fromCol = fromCol;
		this.toRow = toRow;
		this.toCol = toCol;
	}

	public boolean isJump() {
		return (fromRow - toRow == 2 || fromRow - toRow == -2);
	}
}
