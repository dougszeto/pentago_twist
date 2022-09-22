package student_player;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;

public class MyTools {
    private static PentagoBoardState.Piece myColor = null;
    private static PentagoBoardState.Piece opColor = null;


    public static void setColors(PentagoBoardState boardState){
        // initialize myColor and opponent color
        myColor = boardState.getTurnPlayer() == 0 ? PentagoBoardState.Piece.WHITE : PentagoBoardState.Piece.BLACK;
        opColor = myColor == PentagoBoardState.Piece.WHITE ? PentagoBoardState.Piece.BLACK : PentagoBoardState.Piece.WHITE;
    }

    public static PentagoBoardState.Piece getOpColor(){
        return opColor;
    }

    /**
     * alphabeta pruning with a depth cutoff determined by the method that calls. Calculates utility
     * of a boardstate if the maximum depth has been reached, or a terminal node has been reaches
     * @param boardState
     * @param depth
     * @param alpha
     * @param beta
     * @param piece
     * @return
     */
    public static int alphabeta(PentagoBoardState boardState, int depth, int alpha, int beta, PentagoBoardState.Piece piece){
        if(depth==0 || boardState.gameOver()){
            return getUtility(boardState, piece);
        }

        ArrayList<PentagoBoardState> children = getChildren(boardState);
        if(piece == myColor) { // MAXIMIZING PLAYER
            int value = Integer.MIN_VALUE;
            for(PentagoBoardState child : children) {
                value = Math.max(value, alphabeta(child, depth-1, alpha, beta, opColor));
                alpha = Math.max(value, alpha);
                if(alpha >= beta) break;
            }
            return value;
        }
        else{
            int value = Integer.MAX_VALUE;
            for(PentagoBoardState child : children){
                value = Math.min(value, alphabeta(child, depth-1, alpha, beta, myColor));
                beta = Math.min(beta, value);
                if(beta <= alpha) break;
            }
            return value;
        }
    }

    /**
     * Calculate the utility of a board state by finding number of my pieces
     * in a row and subtracting the number of pieces the opponent has in a row.
     * Having a longer streak gives a "streak bonus" and having a piece next
     * to an opponent piece gives a "block bonus"
     * @param boardState
     * @param piece
     * @return
     */
    public static int getUtility(PentagoBoardState boardState, PentagoBoardState.Piece piece){

        // initialize my utility and the opponents utility as 0
        // streak bonuses start as 1 (so that a streak of length 2 has a +1 bonus)
        int myUtility = 0;
        int myStreakBonus = 1;

        int opUtility = 0;
        int opStreakBonus = 1;

        // Check if my piece won the game... if so +500 utility
        if(boardState.getWinner()==0 && MyTools.myColor == PentagoBoardState.Piece.WHITE
        || boardState.getWinner()==1 && MyTools.myColor == PentagoBoardState.Piece.BLACK){
            myUtility += 500;
        }

        // If the opponent won the game -500 utility
        else if(boardState.getWinner()==0 && MyTools.myColor == PentagoBoardState.Piece.BLACK
                || boardState.getWinner()==1 && MyTools.myColor == PentagoBoardState.Piece.WHITE){
            myUtility -= 500;
        }


        // using getBoard instead of getPieceAt will cover us in case the board size changes
        PentagoBoardState.Piece[][] board = boardState.getBoard();


        // check number of pieces in a row horizontally
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length-1; j++){
                // if there are two pieces of same color next to each other (horizontally)
                if(board[i][j] == myColor && board[i][j+1] == myColor){
                    myUtility += 1 + myStreakBonus;
                    myStreakBonus += 10;   // streak bonus increments by 5 to incentives longer streaks
                } else myStreakBonus = 1;

                if(board[i][j] == opColor && board[i][j+1] == opColor){
                    opUtility += 1 +opStreakBonus;
                    opStreakBonus += 15;
                } else opStreakBonus = 1;
            }
            myStreakBonus = opStreakBonus = 0;
        }

        // check number of pieces in a row vertically
        for(int i=0; i<board.length-1; i++){
            for(int j=0; j<board[i].length; j++){
                if(board[i][j] == myColor && board[i+1][j] == myColor){
                    myUtility += 1 + myStreakBonus;
                    myStreakBonus+=10;
                } else myStreakBonus = 1;


                if(board[i][j] == opColor && board[i+1][j] == opColor){
                    opUtility += 1 +opStreakBonus;
                    opStreakBonus+=15;
                } else opStreakBonus = 1;
            }
            myStreakBonus = opStreakBonus = 1;
        }

        // check number of pieces in a row diagonally from left to right (MAIN DIAGONAL)
        for(int i=0; i<board.length-1; i++){
            if(board[i][i] == myColor && board[i+1][i+1] == myColor){
                myUtility+= 1 + myStreakBonus;
                myStreakBonus+=10;
            }else myStreakBonus=1;


            if(board[i][i] == opColor && board[i+1][i+1]==opColor){
                opUtility += 1 +opStreakBonus;
                opStreakBonus+=15;
            } else opStreakBonus=1;
        }
        myStreakBonus = opStreakBonus = 1;

        // check number of pieces in a row diagnoally from left to right (starts at [0,1])
        for(int i=0; i< board.length-2; i++){
            if(board[i][i+1]==myColor && board[i+1][i+2]==myColor){
                myUtility+= 1 + myStreakBonus;
                myStreakBonus+=10;
            }else myStreakBonus = 1;


            if(board[i][i+1]==opColor && board[i+1][i+2]==opColor){
                opUtility+= 1 + opStreakBonus;
                opStreakBonus+=15;
            }else opStreakBonus = 1;

        }
        myStreakBonus = opStreakBonus = 1;

        // check number of peices in a row diagonally from left to right (starts at [1,0])
        for(int i=1; i<board.length-1; i++){
            if(board[i][i-1]==myColor && board[i+1][i] == myColor){
                myUtility += 1 + myStreakBonus;
                myStreakBonus +=10;
            }else myStreakBonus = 1;


            if(board[i][i-1]==opColor && board[i+1][i] == opColor){
                opUtility += 1 + opStreakBonus;
                opStreakBonus += 15;
            } else opStreakBonus = 1;
        }
        myStreakBonus = opStreakBonus = 1;

        // check number of pieces in a row diagonally from right to left (MAIN DIAGONAL)
        for(int i=0; i<board.length-1; i++){
            if(board[i][5-i] == myColor && board[i+1][4-i] == myColor){
                myUtility+= 1 + myStreakBonus;
                myStreakBonus+=10;
            }else myStreakBonus=1;


            if(board[i][5-i] == opColor && board[i+1][4-i]==opColor){
                opUtility += 1 +opStreakBonus;
                opStreakBonus+=15;
            } else opStreakBonus=1;
        }
        myStreakBonus = opStreakBonus = 1;

        // check number of pieces in a row diagonally from right to left (starts at [0,4])
        for(int i=0; i<board.length-2; i++){
            if(board[i][4-i]==myColor && board[i+1][3-i] == myColor) {
                myUtility += 1 + myStreakBonus;
                myStreakBonus += 10;
            } else myStreakBonus = 1;


            if(board[i][4-i]==opColor && board[i+1][3-i] == opColor){
                opUtility += 1+opStreakBonus;
                opStreakBonus += 15;
            } else opStreakBonus =1;
        }
        myStreakBonus = opStreakBonus = 1;

        // check number of pieces in a row diagonally from left to right (starts at [1,5])
        for(int i=1; i< board.length-1; i++){
            if(board[i][6-i] == myColor && board[i+1][5-i] == myColor){
                myUtility+=1+myStreakBonus;
                myStreakBonus += 10;
            }else myStreakBonus = 1;

            if(board[i][6-i] == opColor && board[i+1][5-i] == opColor) {
                opUtility += 1 + opStreakBonus;
                opStreakBonus += 15;
            }else opStreakBonus=1;
        }

        if(piece == myColor){
            myUtility -= opUtility;
            return myUtility;
        }
        else{
            opUtility -=myUtility;
            opUtility *= -1;
            return opUtility;
        }
    }

    /**
     * Given a PentagoBoardState returns an ArrayList of all children PentagoBoardStates
     * @param boardState
     * @return an arraylist of all legal child states
     */
    public static ArrayList<PentagoBoardState> getChildren(PentagoBoardState boardState){
        ArrayList<PentagoMove> legalMoves = boardState.getAllLegalMoves();
        ArrayList<PentagoBoardState> children = new ArrayList<>(legalMoves.size());

        for(PentagoMove move : legalMoves){
            PentagoBoardState cloneState = (PentagoBoardState) boardState.clone();
            cloneState.processMove(move);
            children.add(cloneState);
        }
        return children;
    }
}