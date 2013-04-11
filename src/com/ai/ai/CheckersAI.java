package com.ai.ai;

import java.util.ArrayList;

import com.ai.config.MinimaxConfiguration;
import com.ai.data.CheckersData;
import com.ai.data.CheckersMove;
import com.ai.data.State;

public class CheckersAI {
	private State M_player; // me
	private CheckersData M_checkerData;
	private CheckersData M_tempCheckerData;

	public CheckersAI(CheckersData M_checkerData) {
		this.M_checkerData = M_checkerData;
		this.M_player = State.BLACK;
	}

	private boolean isGameOver(State player) {
		if (this.M_checkerData.getLegalMoves(player).size() == 0) {
			return true;
		}

		return false;
	}

	public CheckersMove alphabeta() {
		ArrayList<CheckersMove> legalMoves = this.M_checkerData
				.getLegalMoves(M_player);

		if (isGameOver(M_player))
			return null;

		CheckersMove bestMove = null;
		int best_val = Integer.MIN_VALUE;

		for (CheckersMove CheckersMove : legalMoves) {
			M_tempCheckerData = new CheckersData();

			for (int i = 0; i < this.M_checkerData.M_board.length; i++)
				System.arraycopy(this.M_checkerData.M_board[i], 0,
						M_tempCheckerData.M_board[i], 0,
						this.M_checkerData.M_board[i].length);

			M_tempCheckerData.makeMove(M_player, CheckersMove.fromRow,
					CheckersMove.fromCol, CheckersMove.toRow,
					CheckersMove.toCol);

			int val = minMove(0, new AlphaBeta(Integer.MIN_VALUE,
					Integer.MAX_VALUE));

			if (val > best_val) {
				bestMove = CheckersMove;
				best_val = val;
			}

			M_tempCheckerData = null;
		}

		return bestMove;
	}

	private int minMove(int count, AlphaBeta ab) {
		count++;

		if (count > MinimaxConfiguration.MinimaxCheckLevel
				|| this.isGameOver(M_player == State.BLACK ? State.RED
						: State.BLACK))
			return this.getBoardValue();

		ArrayList<CheckersMove> legalMoves = this.M_checkerData
				.getLegalMoves(M_player == State.BLACK ? State.RED
						: State.BLACK);

		int score = Integer.MAX_VALUE;

		for (CheckersMove CheckersMove : legalMoves) {
			M_tempCheckerData.makeMove(M_player == State.BLACK ? State.RED
					: State.BLACK, CheckersMove.fromRow, CheckersMove.fromCol,
					CheckersMove.toRow, CheckersMove.toCol);

			score = Math.min(maxMove(count, ab), score);

			if (score <= ab.alpha())
				return score;

			ab.setBeta(Math.min(ab.beta(), score));
		}

		return score;
	}

	private int maxMove(int count, AlphaBeta ab) {
		count++;

		if (count > MinimaxConfiguration.MinimaxCheckLevel
				|| this.isGameOver(M_player))
			return this.getBoardValue();

		ArrayList<CheckersMove> legalMoves = this.M_checkerData
				.getLegalMoves(M_player);

		int score = Integer.MIN_VALUE;

		for (CheckersMove CheckersMove : legalMoves) {
			M_tempCheckerData.makeMove(M_player, CheckersMove.fromRow,
					CheckersMove.fromCol, CheckersMove.toRow,
					CheckersMove.toCol);

			score = Math.max(score, minMove(count, ab));

			if (score <= ab.beta())
				return score;

			ab.setAlpha(Math.max(ab.alpha(), score));
		}

		return score;
	}

	private int getBoardValue() {
		int val = 0;
		int enemyval = 0;

		for (int x = 0; x < M_tempCheckerData.M_board.length; x++) {
			for (int y = 0; y < M_tempCheckerData.M_board[x].length; y++) {
				State cellState = M_tempCheckerData.M_board[x][y];
				State cellColor = State.RED;

				if (cellState == State.BLACK || cellState == State.BLACK_KING)
					cellColor = State.BLACK;

				if (cellState != State.EMPTY) {
					int factor = (cellColor == State.RED) ? (7 - y) : (y);

					if (cellColor == State.RED) {
						if (cellState == State.RED) {
							val += 100 * (factor * factor);
						} else {
							val += 200 * (factor * factor);

							if (y == 0) {
								if (x == 0)
									val -= 40;
								else
									val -= 20;
							} else {
								if (x == 7)
									val -= 40;
								else
									val -= 20;
							}
						}
					} else {
						if (cellState == State.BLACK) {
							enemyval += 100 * (factor * factor);
						} else {
							enemyval += 200 * (factor * factor);

							if (y == 0) {
								if (x == 0)
									enemyval -= 40;
								else
									enemyval -= 20;
							} else {
								if (x == 7)
									enemyval -= 40;
								else
									enemyval -= 20;
							}
						}
					}
				}
			}
		}

		if (enemyval == 0)
			return 100000 + MinimaxConfiguration.MinimaxCheckLevel
					* MinimaxConfiguration.MinimaxCheckLevel;
		else if (val == 0)
			return -100000 - MinimaxConfiguration.MinimaxCheckLevel
					* MinimaxConfiguration.MinimaxCheckLevel;

		return val - enemyval;

	}
}
