package com.example.p2papplication;

import java.util.ArrayList;

public class AIMachine {

    private static final int winScore = 100000000;
    public static int row = 15;
    public static int col = 15;
    // input: list matrix , output: position which machine will run
    public static int machineRun(int[][] board) {
        Object[] bestMove = searchWinningMove(board);
        Object[] badMove = searchLoseMove(board);
        int depth = 3;

        int[] move = new int[2];

        if (badMove[1] != null && badMove[2] != null) {

            move[0] = (Integer)(badMove[1]);
            move[1] = (Integer)(badMove[2]);
            return move[0]*col + move[1];
        }

        if(bestMove[1] != null && bestMove[2] != null) {

            move[0] = (Integer)(bestMove[1]);
            move[1] = (Integer)(bestMove[2]);

        } else {

            bestMove = minimaxSearchAB(depth, board, true, -1.0, winScore);
            if(bestMove[1] == null) {
                move = null;
            } else {
                move[0] = (Integer)(bestMove[1]);
                move[1] = (Integer)(bestMove[2]);
            }
        }
        return move[0]*col + move[1];
    }


    private static Object[] searchWinningMove(int[][] matrix) {
        ArrayList<int[]> allPossibleMoves = generateMoves(matrix);


        Object[] winningMove = new Object[3];

        for(int[] move : allPossibleMoves) {
            int[][] dummyBoard = playNextMove(matrix, move, false);

            // If the white player has a winning score in that temporary board, return the move.
            if(getScore(dummyBoard,false,false) >= winScore) {
                winningMove[1] = move[0];
                winningMove[2] = move[1];
                return winningMove;
            }
        }

        return winningMove;
    }
    private static Object[] searchLoseMove(int[][] matrix) {
        ArrayList<int[]> allPossibleMoves = generateMoves(matrix);

        Object[] losingMove = new Object[3];

        for(int[] move : allPossibleMoves) {
            int[][] dummyBoard = playNextMove(matrix, move, true);

            // If the white player has a winning score in that temporary board, return the move.
            if (getScore(dummyBoard, true, false) >= winScore) {
                losingMove[1] = move[0];
                losingMove[2] = move[1];
                return losingMove;
            }
        }

        return losingMove;
    }

    public static ArrayList<int[]> generateMoves(int[][] boardMatrix) {
        ArrayList<int[]> moveList = new ArrayList<int[]>();

        // Tìm những tất cả những ô trống nhưng có đánh XO liền kề
        for(int i=0; i<row; i++) {
            for(int j=0; j<col; j++) {

                if(boardMatrix[i][j] != 0) continue;

                if(i > 0) {
                    if(j > 0) {
                        if(boardMatrix[i-1][j-1]  != 0 ||
                                boardMatrix[i][j-1] != 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(j < col-1) {
                        if(boardMatrix[i-1][j+1] != 0 ||
                                boardMatrix[i][j+1] != 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(boardMatrix[i-1][j] != 0) {
                        int[] move = {i,j};
                        moveList.add(move);
                        continue;
                    }
                }
                if( i < row-1) {
                    if(j > 0) {
                        if(boardMatrix[i+1][j-1] != 0 ||
                                boardMatrix[i][j-1] != 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(j < col-1) {
                        if(boardMatrix[i+1][j+1] != 0 ||
                                boardMatrix[i][j+1] != 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(boardMatrix[i+1][j] != 0) {
                        int[] move = {i,j};
                        moveList.add(move);
                        continue;
                    }
                }

            }
        }
        return moveList;
    }
    public static int[][] playNextMove(int[][] board, int[] move, boolean isUserTurn) {
        int i = move[0], j = move[1];
        int [][] newBoard = new int[row][col];
        for (int h = 0; h < row; h++) {
            for (int k = 0; k < col; k++) {
                newBoard[h][k] = board[h][k];
            }
        }
        newBoard[i][j] = isUserTurn ? -1 : 1;
        return newBoard;
    }

    public static int getScore(int[][] board, boolean forX, boolean blacksTurn) {

        return evaluateHorizontal(board, forX, blacksTurn) +
                evaluateVertical(board, forX, blacksTurn) +
                evaluateDiagonal(board, forX, blacksTurn);
    }

    public static int evaluateHorizontal(int[][] boardMatrix, boolean forX, boolean playersTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;

        for(int i=0; i<row; i++) {
            for(int j=0; j<col; j++) {

                if(boardMatrix[i][j] == (forX ? -1 : 1)) {
                    //2. Đếm...
                    consecutive++;
                }
                // gặp ô trống
                else if(boardMatrix[i][j] == 0) {
                    if(consecutive > 0) {
                        // Ra: Ô trống ở cuối sau khi đếm. Giảm block rồi bắt đầu tính điểm sau đó reset lại ban đầu
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        // 1. Vào reset lại blocks = 1 rồi bắt đầu đếm
                        blocks = 1;
                    }
                }
                //gặp quân địch
                else if(consecutive > 0) {
                    // 2.Ra:  Ô bị chặn sau khi đếm. Tính điểm sau đó reset lại.
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    //1. Vào: reset lại blocks = 2 rồi bắt đầu đếm
                    blocks = 2;
                }
            }

            // 3. Ra: nhưng lúc này đang ở cuối. Nếu liên tục thì vẫn tính cho đến hết dòng
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            }
            // reset lại để tiếp tục chạy cho dòng tiếp theo
            consecutive = 0;
            blocks = 2;

        }
        return score;
    }
    // hàm tính toán đường dọc tương tự như đường ngan
    public static  int evaluateVertical(int[][] boardMatrix, boolean forX, boolean playersTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;

        for(int j=0; j<col; j++) {
            for(int i=0; i<row; i++) {
                if(boardMatrix[i][j] == (forX ? -1 : 1)) {
                    consecutive++;
                }
                else if(boardMatrix[i][j] == 0) {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }
            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            }
            consecutive = 0;
            blocks = 2;

        }
        return score;
    }
    // Hàm tính toán 2 đường chéo tương tự như hàng ngan
    public static  int evaluateDiagonal(int[][] boardMatrix, boolean forX, boolean playersTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;
        // Đường chéo /
        for (int k = 0; k <= 2 * (row - 1); k++) {
            int iStart = Math.max(0, k - row + 1);
            int iEnd = Math.min(row - 1, k);
            for (int i = iStart; i <= iEnd; ++i) {
                int j = k - i;

                if(boardMatrix[i][j] == (forX ? -1 : 1)) {
                    consecutive++;
                }
                else if(boardMatrix[i][j] == 0) {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }

            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            }
            consecutive = 0;
            blocks = 2;
        }
        // Đường chéo \
        for (int k = 1-row; k < row; k++) {
            int iStart = Math.max(0, k);
            int iEnd = Math.min(row + k - 1, row-1);
            for (int i = iStart; i <= iEnd; ++i) {
                int j = i - k;

                if(boardMatrix[i][j] == (forX ? -1 : 1)) {
                    consecutive++;
                }
                else if(boardMatrix[i][j] == 0) {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }

            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            }
            consecutive = 0;
            blocks = 2;
        }
        return score;
    }
    public static  int getConsecutiveSetScore(int count, int blocks, boolean currentTurn) {
        final int winGuarantee = 1000000;
        if(blocks == 2 && count <= 5) return 0;
        switch(count) {
            // Ăn 5 -> Cho điểm cao nhất
            case 5: {
                return winScore;
            }
            case 4: {
                // Đang 4 -> Tuỳ theo lược và bị chặn: winGuarantee, winGuarantee/4, 200
                if(currentTurn) return winGuarantee;
                else {
                    if(blocks == 0) return winGuarantee/4;
                    else return 200;
                }
            }
            case 3: {
                // Đang 3: Block = 0
                if(blocks == 0) {
                    // Nếu lược của currentTurn thì ăn 3 + 1 = 4 (không bị block) -> 50000 -> Khả năng thắng cao.
                    // Ngược lại không phải lược của currentTurn thì khả năng bị blocks cao
                    if(currentTurn) return 50000;
                    else return 200;
                }
                else {
                    // Block == 1 hoặc Blocks == 2
                    if(currentTurn) return 10;
                    else return 5;
                }
            }
            case 2: {
                // Tương tự với 2
                if(blocks == 0) {
                    if(currentTurn) return 7;
                    else return 5;
                }
                else {
                    return 3;
                }
            }
            case 1: {
                return 1;
            }
        }
        return winScore*2;
    }


    public static Object[] minimaxSearchAB(int depth, int[][] board, boolean max, double alpha, double beta) {
        if(depth == 0) {
            Object[] x = {evaluateBoardForWhite(board, !max), null, null};
            return x;
        }


        ArrayList<int[]> allPossibleMoves = generateMoves(board);

        if(allPossibleMoves.size() == 0) {

            Object[] x = {evaluateBoardForWhite(board, !max), null, null};

            return x;
        }

        Object[] bestMove = new Object[3];


        if(max) {
            bestMove[0] = -1.0;

            for(int[] move : allPossibleMoves) {
                // Chơi thử với move hiện tại
                int[][] dummyBoard = playNextMove(board, move, false);

                Object[] tempMove = minimaxSearchAB(depth-1, dummyBoard, !max, alpha, beta);

                // Cập nhật alpha
                if((Double)(tempMove[0]) > alpha) {
                    alpha = (Double)(tempMove[0]);
                }
                // Cắt tỉa beta
                if((Double)(tempMove[0]) >= beta) {
                    return tempMove;
                }
                if((Double)tempMove[0] > (Double)bestMove[0]) {
                    bestMove = tempMove;
                    bestMove[1] = move[0];
                    bestMove[2] = move[1];
                }
            }

        }
        else {
            bestMove[0] = 100000000.0;
            bestMove[1] = allPossibleMoves.get(0)[0];
            bestMove[2] = allPossibleMoves.get(0)[1];
            for(int[] move : allPossibleMoves) {
                int[][] dummyBoard = playNextMove(board, move, true);

                Object[] tempMove = minimaxSearchAB(depth-1, dummyBoard, !max, alpha, beta);

                // Cập nhật beta
                if(((Double)tempMove[0]) < beta) {
                    beta = (Double)(tempMove[0]);
                }
                // Cắt tỉa alpha
                if((Double)(tempMove[0]) <= alpha) {
                    return tempMove;
                }
                if((Double)tempMove[0] < (Double)bestMove[0]) {
                    bestMove = tempMove;
                    bestMove[1] = move[0];
                    bestMove[2] = move[1];
                }
            }
        }
        return bestMove;
    }

    public static double evaluateBoardForWhite(int[][] board, boolean userTurn) {


        double blackScore = getScore(board, true, userTurn);
        double whiteScore = getScore(board, false, userTurn);

        if(blackScore == 0) blackScore = 1.0;

        return whiteScore / blackScore;

    }

}