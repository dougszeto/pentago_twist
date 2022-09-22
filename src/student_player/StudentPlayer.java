package student_player;

import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

import java.util.ArrayList;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260827484");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // initializing variables
        MyTools.setColors(boardState);
        PentagoBoardState.Piece opColor = MyTools.getOpColor();

        // MINIMAX DECISION ALGORITHM
        ArrayList<PentagoMove> legalMoves = boardState.getAllLegalMoves();

        PentagoMove bestMove = legalMoves.get(0);
        int bestVal = Integer.MIN_VALUE;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for(PentagoMove move : legalMoves){
            PentagoBoardState cloneState = (PentagoBoardState) boardState.clone();
            cloneState.processMove(move);
            int alphabetaVal = MyTools.alphabeta(cloneState, 1, alpha, beta, opColor);
            if(alphabetaVal > bestVal){
                bestVal = alphabetaVal;
                bestMove = move;
            }
        }
        // Return your move to be processed by the server.
        return bestMove;
    }
}