package glm;


import java.util.*;


/**
 * A data structure providing methods to store and manipulate matrices.
 *
 * <ul>
 *     <li> Matrices are immutable and all methods provide new matrices.
 *     <li> A matrix with a single column is considered a vector.
 *     <li> Matrices are organised in rows (sub-arrays are rows and row elements are columns).
 * </ul>
 */
public class Matrix {

    private final double[][] matrix;
    private final int nRows, nCols;


    /**
     * Instantiates a new matrix.
     *
     * @param matrix A rectangular (each sub-array equal length) 2D array to represent matrix.
     *               <ul>
     *                   <li> Matrices are organised in rows (sub-arrays are rows and row elements are columns).
     *               </ul>
     * @throws Error A valid matrix has at least one element and the same number of columns in each row.
     */
    public Matrix(double[][] matrix) throws Error {

        // cannot have an empty `matrix`
        if (matrix.length == 0 || matrix[0].length == 0) {
            throw new Error("Matrix must have at least one element.");
        }

        this.matrix = matrix;

        this.nRows = matrix.length;
        this.nCols = matrix[0].length;

        // `matrix` must have the same number of columns in each row
        for (double[] row : matrix) {
            if (row.length != nCols) {
                throw new Error("Matrix must have the same number of columns in each row.");
            }
        }
    }


    /**
     * Gets the string representation of `matrix`.
     *
     * <ul>
     *     <li> Each row is on a separate line.
     * </ul>
     *
     * @return String representation of `matrix`.
     */
    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        // each row of `matrix` is on a separate line
        for (int row = 0; row < nRows; row++) {
            string.append(Arrays.toString(matrix[row]));

            // don't place new line after last row
            if (row != nRows - 1) {
                string.append('\n');
            }
        }

        return string.toString();
    }


    /**
     * Gets the number of rows in `matrix`.
     *
     * @return Number of rows in `matrix`.
     */
    public int getNRows() {
        return nRows;
    }


    /**
     * Gets the number of columns in `matrix`.
     *
     * @return Number of columns in `matrix`.
     */
    public int getNCols() {
        return nCols;
    }


    /**
     * Gets the zip of `matrix` and `other` with `operator`.
     *
     * @param other Matrix to zip into `matrix`.
     * @param operator Method to apply to corresponding elements of `matrix` and `other`.
     * @return Zipped result of `matrix` and `other` with `operator`.
     * @throws Error `matrix` and `other` must have the same dimensions.
     */
    public Matrix getZip(Matrix other, BinaryOperator<Double, Double, Double> operator) throws Error {

        // `matrix` and `other` must be the same size
        if (nRows != other.nRows || nCols != other.nCols) {
            throw new Error("Matrices must be of the same dimensions.");
        }

        double[][] zip = new double[nRows][nCols];

        // each element of `zip` is the result of the `operator` on corresponding elements of `matrix` and `other`
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                zip[row][col] = operator.apply(matrix[row][col], other.matrix[row][col]);
            }
        }

        return new Matrix(zip);
    }


    /**
     * Gets the mapping of `operator` on each element of `matrix`.
     *
     * @param operator Method to apply to each element of `matrix`.
     * @return Mapped result of `operator` on `matrix`.
     */
    public Matrix getMap(UnaryOperation<Double, Double> operator) {
        double[][] map = new double[nRows][nCols];

        // apply `operator` to each element of `matrix`
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                map[row][col] = operator.apply(matrix[row][col]);
            }
        }

        return new Matrix(map);
    }


    /**
     * Gets the generic fold of a `matrix` vector using `operator` to collapse it.
     *
     * @param operator Method to combine accumulative results.
     * @param init The initial value of the fold.
     * @param <A> Generic type to fold vector into.
     * @return Accumulative result of `matrix` vector folded by `operator`.
     * @throws Error `matrix` must be a vector (single column).
     */
    public <A> A getFoldVec(BinaryOperator<A, Double, A> operator, A init) throws Error {

        if (nCols != 1) {
            throw new Error("Matrix must be a vector (single column) to be folded.");
        }

        A fold = init;

        // combine each previous `fold` with the next value in the `matrix` vector by applying `operator`
        for (int row = 0; row < nRows; row++) {
            fold = operator.apply(fold, matrix[row][0]);
        }

        return fold;
    }


    /**
     * Gets the matrix multiplication of `matrix` (left) and `other` (right).
     *
     * @param other Right matrix to multiply with `matrix.`
     * @return Multiplication result of `matrix` and `other`.
     * @throws ArithmeticException `matrix` must have the same number of columns as `other` has rows.
     */
    public Matrix getMultiplication(Matrix other) throws ArithmeticException {

        // can only be multiplied if the number of columns in `matrix` equals the number of rows in `other`
        if (nCols != other.nRows) {
            throw new ArithmeticException("Left matrix must have same number of columns as right matrix has rows.");
        }

        // result has same the number of rows as `matrix` and columns as `other`
        double[][] multiplication = new double[nRows][other.nCols];

        // for each row of `matrix` perform a dot product for each column of `other`
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < other.nCols; col++) {

                multiplication[row][col] = 0;

                // dot product current row of `matrix` with current column of `other`
                for (int elem = 0; elem < nCols; elem++) {
                    multiplication[row][col] += matrix[row][elem] * other.matrix[elem][col];
                }
            }
        }

        return new Matrix(multiplication);
    }


    /**
     * Gets the square diagonalisation matrix of the elements of `matrix`.
     *
     * @return Diagonalised matrix of `matrix`.
     * @throws Error `matrix` must be a vector (single column).
     */
    public Matrix getDiagonalisation() throws Error {

        // only vectors can be diagonalised
        if (nCols != 1) {
            throw new Error("Matrix must be a vector (single column) to be diagonalised.");
        }

        // square matrix with as many rows and columns as `matrix` (vector) has rows
        double[][] diagonalisation = new double[nRows][nRows];

        // place `matrix` elements on the diagonal with every other element as zero
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nRows; col++) {
                if (row == col) {
                    diagonalisation[row][col] = matrix[row][0];
                } else {
                    diagonalisation[row][col] = 0;
                }
            }
        }

        return new Matrix(diagonalisation);
    }


    /**
     * Gets the transpose of `matrix`.
     *
     * @return Transpose matrix of `matrix`.
     */
    public Matrix getTranspose() {

        // a transposed matrix flips the dimensions of `matrix`
        double[][] transpose = new double[nCols][nRows];

        // flip axis of elements of `matrix`
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                transpose[col][row] = matrix[row][col];
            }
        }

        return new Matrix(transpose);
    }


    /**
     * Gets the inverse of `matrix`.
     *
     * @return Inverse matrix of `matrix`.
     * @throws ArithmeticException `matrix` must be square and its determinate non-zero.
     */
    public Matrix getInverse() throws ArithmeticException {

        // only square matrices are invertible
        if (nRows != nCols) {
            throw new ArithmeticException("Matrix must be square.");
        }

        double det = getDeterminate();

        // `matrix` not invertible when determinate is zero (compare float to zero as only error if exactly zero)
        if (det == 0) {
            throw new ArithmeticException("Matrix not invertible (determinate = 0).");
        }

        Matrix adjugate = getAdjugate();

        // convert `adjugate` to the inverse by dividing each element by `det`
        return adjugate.getMap(x -> x / det);
    }


    /*
     * Gets the determinate of `matrix.`
     *
     * Method uses recursion to obtain the determinate.
     *
     * @return Determinate of `matrix`.
     * @throws ArithmeticException `matrix` must be square.
     */
    private double getDeterminate() throws ArithmeticException {

        // can only get determinate of square matrices
        if (nRows != nCols) {
            throw new ArithmeticException("Matrix must be square.");
        }

        // single element matrix (base case)
        if (nRows == 1) {
            return matrix[0][0];
        }

        double det = 0;
        int sign = 1;

        // determinate is the alternating positive/negative sum of each cofactor determinate of the first row
        for (int col = 0; col < nCols; col++) {
            Matrix cofactor = getCofactor(0, col);
            det += sign * matrix[0][col] * cofactor.getDeterminate();
            sign = -sign;
        }

        return det;
    }


    /*
     * Gets the adjugate of `matrix`.
     *
     * @return Adjugate of `matrix`.
     * @throws ArithmeticException `matrix` must be square.
     */
    private Matrix getAdjugate() throws ArithmeticException {

        // although the code will work with non-square matrices, mathematically only square matrices have adjugates
        if (nRows != nCols) {
            throw new ArithmeticException("Matrix must be square.");
        }

        double[][] adjugate = new double[nRows][nCols];

        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                Matrix cofactor = getCofactor(row, col);

                // positive and negative signs are like a checkerboard - starting positive in the top-left conner
                int sign = 1;
                if ((row + col) % 2 != 0) {
                    sign = -1;
                }

                // flip row and column as the adjugate is the transpose of the cofactor matrix
                adjugate[col][row] = sign * cofactor.getDeterminate();
            }
        }

        return new Matrix(adjugate);
    }


    /*
     * Get the cofactor of `matrix`.
     *
     * @param row Row of `matrix` to exclude from cofactor.
     * @param col Column of `matrix` to exclude from cofactor.
     * @return Cofactor of `matrix`.
     * @throws ArrayIndexOutOfBoundsException `row` and `col` must be within `matrix` dimensions.
     */
    private Matrix getCofactor(int row, int col) throws  ArrayIndexOutOfBoundsException {

        // can only exclude `row`/`col` that are within the dimensions of `matrix`
        if (row < 0 || col < 0 || row >= nRows || col >= nCols) {
            throw new ArrayIndexOutOfBoundsException("Excluded row and column must be within the matrix dimensions.");
        }

        double[][] cofactor = new double[nRows - 1][nCols - 1];

        // copy each element from `matrix` to `cofactor` excluding `row` and `col`
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {

                int y = i;
                int x = j;

                // have excluded `row` need to adjust index to place in correct row of `cofactor`
                if (i > row) {
                    y--;
                }

                // have excluded `col` need to adjust index to place in correct column of `cofactor`
                if (j > col) {
                    x--;
                }

                // skip over `row` and `col` to exclude
                if (i != row && j != col) {
                    cofactor[y][x] = matrix[i][j];
                }
            }
        }

        return new Matrix(cofactor);
    }
}
