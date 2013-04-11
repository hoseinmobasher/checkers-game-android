package com.ai.graphic;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.ai.ai.CheckersAI;
import com.ai.checker.R;
import com.ai.checkers.CheckersActivity;
import com.ai.config.Configuration;
import com.ai.data.CheckersData;
import com.ai.data.CheckersMove;
import com.ai.data.State;

public class CheckersBoard extends View implements OnTouchListener {
	private int diffX = 0;
	private int diffY = 0;
	private int squareSize = 0;

	private CheckersData checkers = null;
	private ArrayList<CheckersMove> legalMoves = null;
	private CheckersAI checkerMinimax = null;

	private int selectedRow = -1;
	private int selectedCol = -1;

	private State M_player;

	private Context M_context;

	public CheckersBoard(Context context) {
		super(context);

		squareSize = ((CheckersActivity) context).getWindowManager()
				.getDefaultDisplay().getWidth() / 8;

		diffY = (((CheckersActivity) context).getWindowManager()
				.getDefaultDisplay().getHeight() - ((CheckersActivity) context)
				.getWindowManager().getDefaultDisplay().getWidth()) / 2;

		this.M_context = context;
		this.setBackgroundColor(Configuration.LIGHT);
		this.checkers = new CheckersData();
		this.checkerMinimax = new CheckersAI(checkers);
		this.setOnTouchListener(this);

		doNewGame();
	}

	public void doNewGame() {
		this.checkers.init();
		this.checkers.fill();
		this.M_player = State.RED;
		this.legalMoves = this.checkers.getLegalMoves(State.RED);
		this.selectedRow = this.selectedCol = -1;
		this.invalidate();
	}

	public void doClickSquare(int row, int col) {
		for (CheckersMove cm : legalMoves)
			if (cm.fromRow == row && cm.fromCol == col) {
				selectedRow = row;
				selectedCol = col;
				this.invalidate();
				return;
			}

		if (selectedRow < 0) // no object selected to move, return nothing
			return;

		for (int i = 0; i < legalMoves.size(); i++)
			if (legalMoves.get(i).fromRow == selectedRow
					&& legalMoves.get(i).fromCol == selectedCol
					&& legalMoves.get(i).toRow == row
					&& legalMoves.get(i).toCol == col) {
				doMakeMove(legalMoves.get(i));
				return;
			}
	}

	private void doMakeMove(CheckersMove move) {
		this.checkers.makeMove(M_player, move.fromRow, move.fromCol,
				move.toRow, move.toCol);

		if (move.isJump()) {
			legalMoves = this.checkers.getLegalJumpsFrom(M_player, move.toRow,
					move.toCol);

			if (legalMoves.size() != 0) {
				selectedRow = move.toRow;
				selectedCol = move.toCol;

				if (M_player == State.BLACK) {
					CheckersMove check = checkerMinimax.alphabeta();
					doMakeMove(check);
				}

				this.invalidate();
				return;
			}
		}

		if (M_player == State.RED) {
			M_player = State.BLACK;
			legalMoves = this.checkers.getLegalMoves(M_player);
			if (legalMoves.size() == 0) {
				this.invalidate();

				Toast.makeText(this.M_context, "BLACK has no moves. RED wins",
						Toast.LENGTH_LONG).show();

				doNewGame();
				return;
			}

			CheckersMove check = this.checkerMinimax.alphabeta();
			doMakeMove(check);
		} else {
			M_player = State.RED;
			legalMoves = this.checkers.getLegalMoves(M_player);
			if (legalMoves.size() == 0) {
				this.invalidate();
				Toast.makeText(this.M_context, "RED has no moves. BLACK wins",
						Toast.LENGTH_LONG).show();
				doNewGame();

				return;
			}
		}

		selectedRow = -1;

		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int X1 = col * squareSize + diffX / 2;
				int Y1 = row * squareSize + diffY / 2;

				Paint paint = new Paint();
				paint.setStyle(Paint.Style.FILL);

				if (row % 2 == col % 2)
					paint.setColor(Configuration.LIGHT);
				else
					paint.setColor(Configuration.DARK);

				canvas.drawRect(X1, Y1, X1 + squareSize, Y1 + squareSize, paint);

				switch (this.checkers.valueOf(row, col)) {
				case RED:
					canvas.drawBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.red), null, new Rect(X1,
							Y1, X1 + squareSize, Y1 + squareSize), null);
					break;
				case BLACK:
					canvas.drawBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.black), null, new Rect(
							X1, Y1, X1 + squareSize, Y1 + squareSize), null);
					break;
				case RED_KING:
					canvas.drawBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.red_king), null,
							new Rect(X1, Y1, X1 + squareSize, Y1 + squareSize),
							null);
					break;
				case BLACK_KING:
					canvas.drawBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.black_king), null,
							new Rect(X1, Y1, X1 + squareSize, Y1 + squareSize),
							null);
					break;
				}
			}
		}

		if (legalMoves.size() != 0) {
			for (int i = 0; i < legalMoves.size(); i++) {
				int X = legalMoves.get(i).fromCol * squareSize + diffX / 2;
				int Y = legalMoves.get(i).fromRow * squareSize + diffY / 2;

				Paint paint = new Paint();
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(Color.BLACK);
				paint.setStrokeWidth(2.0f);
				canvas.drawRect(new Rect(X, Y, X + squareSize, Y + squareSize),
						paint);
			}
		}

		if (selectedRow != -1) {
			int X = selectedCol * squareSize + diffX / 2;
			int Y = selectedRow * squareSize + diffY / 2;

			Paint paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(2.0f);
			canvas.drawRect(new Rect(X, Y, X + squareSize, Y + squareSize),
					paint);

			for (int i = 0; legalMoves.size() != 0 && i < legalMoves.size(); i++) {
				if (legalMoves.get(i).fromCol == selectedCol
						&& legalMoves.get(i).fromRow == selectedRow) {
					canvas.drawBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.sign), null, new Rect(
							legalMoves.get(i).toCol * squareSize
									+ (22 * squareSize) / 64 + diffX / 2,
							legalMoves.get(i).toRow * squareSize
									+ (22 * squareSize) / 64 + diffY / 2,
							legalMoves.get(i).toCol * squareSize
									+ (22 * squareSize) / 64 + diffX / 2
									+ (22 * squareSize) / 64,
							legalMoves.get(i).toRow * squareSize
									+ (22 * squareSize) / 64 + diffY / 2
									+ (22 * squareSize) / 64), null);
				}
			}
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Rect rect = new Rect(col * squareSize + diffX / 2, row
						* squareSize + diffY / 2, col * squareSize + diffX / 2
						+ squareSize, row * squareSize + diffY / 2 + squareSize);

				if (rect.contains((int) event.getX(), (int) event.getY())) {
					doClickSquare(row, col);
					break;
				}
			}
		}
		return false;
	}
}
