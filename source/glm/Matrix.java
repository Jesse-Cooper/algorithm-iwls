package glm;


import java.lang.Math;


/**
 * A data structure providing methods to store and manipulate matrices

 * Matrices are immutable and all methods provide new matrices
 * A matrix with a single column is considered a vector
 */
public class Matrix
{
    // * Max iterations used in the QR algorithm to find eigenvalues
    private static final double MAX_EIGN_ITERATIONS = 1e3;

    // * Flag that turns off all validation checks when compiled
    private static final boolean IS_DEBUG = true;


    // * `matrix` is organised by rows then columns
    // * Each element in `matrix` should not be modified after `this` matrix has
    //   been instantiated
    private final double[][] matrix;
    private final int nRows, nCols;


    /**
     * Instantiates a new matrix

     * @param matrix
         * A 2D array to represent the matrix
         * Each sub-array is a row where its elements are the columns
     * @throws Error
         * `matrix` must have at least 1 row and column
     * @throws Error
         * Each sub-array of `matrix` must have the same number of elements
     */
    public Matrix(final double[][] matrix) throws Error
    {
        int r;

        // * `matrix` must have at least 1 row and column
        if (IS_DEBUG && (matrix.length == 0 || matrix[0].length == 0))
        {
            throw new Error(
                "\n\n"
                + "* `matrix` must have at least 1 row and column\n"
                + "* Row 0 has 0 columns\n"
            );
        }

        this.matrix = matrix;
        this.nRows = matrix.length;
        this.nCols = matrix[0].length;

        // * `matrix` must have the same number of columns in each row
        for (r = 0; r < nRows; r += 1)
        {
            if (IS_DEBUG && matrix[r].length != nCols)
            {
                throw new Error(
                    "\n\n"
                    + "* Each sub-array of `matrix` must have the same number "
                        + "of elements\n"
                    + "* Row 0 has " + nCols + " columns\n"
                    + "* Row " + r + " has " + matrix[r].length + " columns\n"
                );
            }
        }
    }


    /**
     * Gets the string representation of `this` matrix

     * Elements are rounded
     * Each row is on a separate line with the columns aligned

     * @return
         * String representation of `this` matrix
     */
    @Override
    public String toString()
    {
        int r, c;
        final StringBuilder matrixStr;
        String elemStr;
        final int[] colSizes;

        // * Find the largest element string size in each column
        colSizes = new int[nCols];
        for (c = 0; c < nCols; c += 1)
        {
            colSizes[c] = 0;
            for (r = 0; r < nRows; r += 1)
            {
                elemStr = String.valueOf(Numerical.round(getElem(r, c)));
                colSizes[c] = Math.max(colSizes[c], elemStr.length());
            }
        }

        // * Build the matrix string with its columns aligned
        matrixStr = new StringBuilder();
        for (r = 0; r < nRows; r += 1)
        {
            matrixStr.append('{');
            for (c = 0; c < nCols; c += 1)
            {
                // * Elements are padded to the left to align in their column
                elemStr = String.valueOf(Numerical.round(getElem(r, c)));
                elemStr = String.format("%" + colSizes[c] + "s", elemStr);
                matrixStr.append(elemStr);

                // * Add a space between each column
                if (c != nCols - 1)
                {
                    matrixStr.append(' ');
                }
            }
            matrixStr.append("}\n");
        }

        return matrixStr.toString();
    }


    /* ---------------------- START:  special matrices ---------------------- *
     * Static methods that instantiates pre-defined matrices
     * ---------------------------------------------------------------------- */


    /**
     * Instantiates an identity matrix of size `n * n`

     * An identity matrix is a square matrix with `1` on its diagonals and `0`
       on the off-diagonals

     * @param n
         * Number of rows and columns in the matrix
     * @return
         * Identity matrix of size `n * n`
     * @throws Error
         * Matrices must have at least 1 row and column
         * `n >= 1`
     */
    public static Matrix identity(final int n) throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && n <= 0)
        {
            throw new Error(
                "\n\n"
                + "* Matrices must have at least 1 row and column\n"
                + "* `n` must be `>= 1`\n"
                + "* `n = " + n + "`\n"
            );
        }

        // * All diagonals are `1` and all off-diagonals are `0`
        newMatrix = new double[n][n];
        for (r = 0; r < n; r += 1)
        {
            for (c = 0; c < n; c += 1)
            {
                if (r == c)
                {
                    newMatrix[r][c] = 1;
                }
                else
                {
                    newMatrix[r][c] = 0;
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Instantiates a matrix of size `nRows * nCols` with all `0` value elements

     * @param nRows
         * Number of rows in the matrix
     * @param nCols
         * Number of columns in the matrix
     * @return
         * Matrix of size `nRows * nCols` with all `0` value elements
     * @throws Error
         * Matrices must have at least 1 row and column
         * `nRows >= 1`
         * `nCols >= 1`
     */
    public static Matrix zeros(
        final int nRows,
        final int nCols)
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (nRows < 1 || nCols < 1))
        {
            throw new Error(
                "\n\n"
                + "* Matrices must have at least 1 row and column\n"
                + "* Both `nRows` and `nCols` must be `>= 1`\n"
                + "* `nRows = " + nRows + "`\n"
                + "* `nCols = " + nCols + "`\n"
            );
        }

        // * All elements are `0`
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                newMatrix[r][c] = 0;
            }
        }

        return new Matrix(newMatrix);
    }


    /* ----------------------- END:  special matrices ----------------------- */
    /* --------------------------- START: getters --------------------------- *
     * Methods that get existing values from the matrix
     * ---------------------------------------------------------------------- */


    /**
     * Gets the number of rows in `this` matrix

     * @return
         * Number of rows in `this` matrix
     */
    public int getNRows()
    {
        return nRows;
    }


    /**
     * Gets the number of columns in `this` matrix

     * @return
         * Number of columns in `this` matrix
     */
    public int getNCols()
    {
        return nCols;
    }


    /**
     * Gets the element at `(iRow, iCol)` in `this` matrix

     * @param iRow
         * Row index of the element to get
     * @param iCol
         * Column index of the element to get
     * @return
         * Element at `(iRow, iCol)` in `this` matrix
     * @throws Error
         * Cannot get an element outside of `this` matrix
         * `0 <= iRow < (rows in this matrix)`
         * `0 <= iCol < (columns in this matrix)`
     */
    public double getElem(
        final int iRow,
        final int iCol)
    throws Error
    {
        if (IS_DEBUG
            && (iRow < 0 || iRow >= nRows || iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* Cannot get an element outside of `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "`(iRow, iCol) = (" + iRow + ", " + iCol + ")`\n"
            );
        }

        return matrix[iRow][iCol];
    }


    /**
     * Gets the row at row `iRow` in `this` matrix

     * Row is returned as a vector

     * @param iRow
         * Index of row to get
     * @return
         * Row at row `iRow` in `this` matrix
     * @throws Error
         * Cannot get a row outside of `this` matrix
         * `0 <= iRow < (rows in this matrix)`
     */
    public Matrix getRow(final int iRow) throws Error
    {
        int c;
        final double[][] newMatrix;

        if (IS_DEBUG && (iRow < 0 || iRow >= nRows))
        {
            throw new Error(
                "\n\n"
                + "* Cannot get a row outside of `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `iRow = " + iRow + "`\n"
            );
        }

        // * Copy row `iRow` to a new vector
        newMatrix = new double[nCols][1];
        for (c = 0; c < nCols; c += 1)
        {
            newMatrix[c][0] = getElem(iRow, c);
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the column at column `iCol` in `this` matrix

     * Column is returned as a vector

     * @param iCol
         * Index of column to get
     * @return
         * Column at column `iCol` in `this` matrix
     * @throws Error
         * Cannot get a column outside of `this` matrix
         * `0 <= iCol < (columns in this matrix)`
     */
    public Matrix getCol(final int iCol) throws Error
    {
        int r;
        final double[][] newMatrix;

        if (IS_DEBUG && (iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* Cannot get a column outside of `this` matrix\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "* `iCol = " + iCol + "`\n"
            );
        }

        // * Copy column `iCol` to a new vector
        newMatrix = new double[nRows][1];
        for (r = 0; r < nRows; r += 1)
        {
            newMatrix[r][0] = getElem(r, iCol);
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the diagonal elements top-left to bottom-right in `this` matrix as a
       vector

     * For non-square matrices the number of diagonals is minimum length of
       either the rows or columns

     * @returns
         * Diagonal elements in `this` matrix as a vector
     */
    public Matrix getDiag()
    {
        int d;
        final int n;
        final double[][] newMatrix;

        // * Copy elements on the diagonal to a new vector
        n = Math.min(nRows, nCols);
        newMatrix = new double[n][1];
        for (d = 0; d < n; d += 1)
        {
            newMatrix[d][0] = getElem(d, d);
        }

        return new Matrix(newMatrix);
    }


    /* ---------------------------- END: getters ---------------------------- */
    /* --------------------------- START: setters --------------------------- *
     * Methods that set elements of the original matrix
     * As all matrices are immutable these setters do not change the original
       matrix, but instead return a new matrix
     * ---------------------------------------------------------------------- */


    /**
     * Gets the matrix result of setting setting the sub-matrix area to
       `subMatrix` of `this` matrix starting at `(iRow, iCol)`

     * @param subMatrix
        * Matrix to set sub-matrix area of `this` matrix to
     * @param iRow
        * Index of row to place top-left element of `subMatrix` on `this` matrix
     * @param iCol
        * Index of column to place top-left element of `subMatrix` on `this`
          matrix
     * @return
        * The matrix result of setting setting the sub-matrix area to
          `subMatrix` of `this` matrix starting at `(iRow, iCol)`
     * @throws
        * `subMatrix` starting at `(iRow, iCol)` must fit within `this` matrix
     */
    public Matrix setSubMatrix(
        final Matrix subMatrix,
        final int    iRow     ,
        final int    iCol     )
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG
            && (iRow < 0 || subMatrix.nRows + iRow > nRows
                || iCol < 0 || subMatrix.nCols + iCol > nCols))
        {
            throw new Error(
                "\n\n"
                + "* `subMatrix` starting at `(iRow, iCol)` must fit within "
                    + "`this` matrix\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
                + "* `subMatrix` is size " + subMatrix.nRows + "x"
                    + subMatrix.nCols + "\n"
                + "* `(iRow, iCol) = (" + iRow + ", " + iCol +")`\n"
            );
        }

        // * Copy `this` matrix outside the sub-matrix setting area and copy
        //   `subMatrix` inside the area
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (r >= iRow && r < iRow + subMatrix.nRows
                    && c >= iCol && c < iCol + subMatrix.nCols)
                {
                    newMatrix[r][c] = subMatrix.getElem(r - iRow, c - iCol);
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /* ---------------------------- END: setters ---------------------------- */
    /* --------------------------- START:  checks --------------------------- *
     * Boolean checks of a matrix's properties
     * ---------------------------------------------------------------------- */


    /**
     * Determines whether `this` matrix is square

     * Square matrices have an equal number of rows and columns

     * @returns
         * Indication whether `this` matrix is square
     */
    public boolean isSquare()
    {
        return nRows == nCols;
    }


    /**
     * Determines whether `this` matrix is a vector

     * Vectors are matrices with only 1 column

     * @returns
         * Indication whether `this` matrix is a vector
     */
    public boolean isVector()
    {
        return nCols == 1;
    }


    /**
     * Determines whether `this` matrix is symmetric

     * Symmetric matrices are square matrices that have equal elements across
       the diagonal where indices of the rows and columns are flipped

     * @returns
         * Indication whether `this` matrix is symmetric
     */
    public boolean isSymmetric()
    {
        int r, c;

        if (!isSquare())
        {
            return false;
        }

        // * Compare each correspond (row and column index flipped) off-diagonal
        for (r = 1; r < nRows; r += 1)
        {
            for (c = 0; c < r; c += 1)
            {
                if (!Numerical.isEqual(getElem(r, c), getElem(c, r)))
                {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Determines whether `this` matrix is in lower triangular form

     * Lower triangular matrices are square matrices with all element above the
       diagonal equal to zero
     * A matrix is in lower triangular form even if all elements are zero

     * @returns
         * Indication whether `this` matrix is in lower triangular form
     */
    public boolean isLowerTri()
    {
        int r, c;

        if (!isSquare())
        {
            return false;
        }

        // * Check all elements above the diagonal are zero
        for (r = 0; r < nRows; r += 1)
        {
            for (c = r + 1; c < nCols; c += 1)
            {
                if (!Numerical.isEqual(getElem(r, c), 0))
                {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Determines whether `this` matrix is in upper triangular form

     * Upper triangular matrices are square matrices with all element below the
       diagonal equal to zero
     * A matrix is in upper triangular form even if all elements are zero

     * @returns
         * Indication whether `this` matrix is in upper triangular form
     */
    public boolean isUpperTri()
    {
        int r, c;

        if (!isSquare())
        {
            return false;
        }

        // * Check all elements below the diagonal are zero
        for (r = 1; r < nRows; r += 1)
        {
            for (c = 0; c < r; c += 1)
            {
                if (!Numerical.isEqual(getElem(r, c), 0))
                {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Determines whether `this` matrix is in row echelon form

     * A matrix is in row echelon form if from top-to-bottom each row's
       left-most non-zero element (pivot) comes after (to the right of) the
       pivots of rows above

     * @return
         * Indication whether `this` matrix is in row echelon form
     */
    public boolean isRowEchelon()
    {
        int r, c;
        int iPrevPivot;

        // * From bottom-to-top check each row's pivot is before the pivot of
        //   each row below
        iPrevPivot = nCols;
        for (r = nRows - 1; r >= 0; r -= 1)
        {
            // * From left-to-right find first non-zero in the row (pivot)
            c = 0;
            while (c < iPrevPivot && Numerical.isEqual(getElem(r, c), 0))
            {
                c += 1;
            }

            // * Not in row echelon form if the current pivot is before any
            //   previous pivot
            if (c == iPrevPivot && c != nCols)
            {
                return false;
            }

            iPrevPivot = c;
        }

        return true;
    }


    /**
     * Determines whether `this` matrix is in column echelon form

     * A matrix is in column echelon form if from top-to-bottom each row's
       right-most non-zero element (pivot) comes before (to the left of) the
       pivots of rows below

     * @return
         * Indication whether `this` matrix is in column echelon form
     */
    public boolean isColEchelon()
    {
        int r, c;
        int iPrevPivot;

        // * From top-to-bottom check each row's pivot is after the pivot of
        //   each row above
        iPrevPivot = -1;
        for (r = 0; r < nRows; r += 1)
        {
            // * From right-to-left find first non-zero in the row (pivot)
            c = nCols - 1;
            while (c > iPrevPivot && Numerical.isEqual(getElem(r, c), 0))
            {
                c -= 1;
            }

            // * Not in column echelon form if the current pivot is after any
            //   previous pivot
            if (c == iPrevPivot && c != -1)
            {
                return false;
            }

            iPrevPivot = c;
        }

        return true;
    }


    /**
     * Determines whether `this` matrix is invertible

     * Invertible matrices are square matrices with a non-zero determinate

     * @return
        * Indication whether `this` matrix is invertible
     */
    public boolean isInvertible()
    {
        return isSquare() && !Numerical.isEqual(determinate(), 0);
    }


    /**
     * Determines whether `this` matrix is equal to the `other` matrix

     * Matrices are equal when they have the same dimensions and each
       corresponding element is of equal value

     * @param other
         * Matrix to compare equality to
     * @return
         * Indication whether `this` matrix is equal to the `other` matrix
     */
    public boolean isEqual(final Matrix other)
    {
        int r, c;

        // * Check if both matrices have equal dimensions
        if (nRows != other.nRows || nCols != other.nCols)
        {
            return false;
        }

        // * Check if each corresponding element is equal value
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (!Numerical.isEqual(getElem(r, c), other.getElem(r, c)))
                {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Determines whether `eigenvalue` is an eigenvalue of `this` matrix

     * Any value that satisfies `det(A - (x * I)) = 0` is an eigenvalue of the
       matrix where `A` is the matrix, `x` is the eigenvalue and `I` is the
       identity matrix

     * @param eigenvalue
        * Eigenvalue to check
     * @return
        * Indication whether `eigenvalue` belongs to `this` matrix
     * @throws Error
        * Only square matrices have eigenvalues
        * `this` matrix must be square
     */
    public boolean isEigenvalue(final double eigenvalue) throws Error
    {
        if (IS_DEBUG && !isSquare())
        {
            throw new Error(
                "\n\n"
                + "* Only square matrices have eigenvalues\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        return Numerical.isEqual(mapDiag(x -> x - eigenvalue).determinate(), 0);
    }


    /* ---------------------------- END:  checks ---------------------------- */
    /* ------------------------- START: functionals ------------------------- *
     * Functional methods that apply lambda functions to matrices
     * As all matrices are immutable these methods do not change the original
       matrix, but instead return a new matrix
     * ---------------------------------------------------------------------- */


    /**
     * Gets the matrix result of mapping the single element at `(iRow, iCol)` in
       `this` matrix with the function `f`

     * @param f
         * Lambda function to apply
     * @param iRow
         * Row index of the element to map
     * @param iCol
         * Column index of the element to map
     * @return
         * Matrix result of mapping the single element at `(iRow, iCol)` in
           `this` matrix with the function `f`
     * @throws Error
        * Cannot map an element outside of `this` matrix
        * `0 <= iRow < (rows in this matrix)`
        * `0 <= iCol < (columns in this matrix)`
     */
    public Matrix mapElem(
        final LambdaOperatorUnary<Double, Double> f   ,
        final int                                 iRow,
        final int                                 iCol)
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG
            && (iRow < 0 || iRow >= nRows || iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* Cannot map an element outside of `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "* `iRow = " + iRow + "`\n"
                + "* `iCol = " + iCol + "`\n"
            );
        }

        // * Copy `this` matrix
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                newMatrix[r][c] = getElem(r, c);
            }
        }

        // * Apply `f` to the element at `(iRow, iCol)`
        newMatrix[iRow][iCol] = f.apply(getElem(iRow, iCol));

        return new Matrix(newMatrix);
    }


    /**
     * Gets the matrix result of mapping all elements in `this` matrix with the
       function `f`

     * @param f
         * Lambda function to apply
     * @return
         * Matrix result of mapping all elements in `this` matrix with the
           function `f`
     */
    public Matrix mapMatrix(final LambdaOperatorUnary<Double, Double> f)
    {
        int r, c;
        final double[][] newMatrix;

        // * Apply `f` to each element
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                newMatrix[r][c] = f.apply(getElem(r, c));
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the matrix result of mapping all elements within row `iRow` in
       `this` matrix with the function `f`

     * @param f
         * Lambda function to apply
     * @param iRow
         * Row index of the row to map
     * @return
         * Matrix result of mapping all elements within row `iRow` in `this`
           matrix with the function `f`
     * @throws Error
         * Cannot map a row outside of `this` matrix
         * `0 <= iRow < (rows in this matrix)`
     */
    public Matrix mapRow(
        final LambdaOperatorUnary<Double, Double> f   ,
        final int                                 iRow)
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (iRow < 0 || iRow >= nRows))
        {
            throw new Error(
                "\n\n"
                + "* Cannot map a row outside of `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `iRow = " + iRow + "`\n"
            );
        }

        // * Apply `f` to the elements in row `iRow` and copy the other rows
        //   from `this` matrix
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (r == iRow)
                {
                    newMatrix[r][c] = f.apply(getElem(r, c));
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the matrix result of mapping all elements within column `iCol` in
       `this` matrix with the function `f`

     * @param f
         * Lambda function to apply
     * @param iCol
         * Column index of the column to map
     * @return
         * Matrix result of mapping all elements within column `iCol` in `this`
           matrix with the function `f`
     * @throws Error
         * Cannot map a row outside of `this` matrix
         * `0 <= iCol < (columns in this matrix)`
     */
    public Matrix mapCol(
        final LambdaOperatorUnary<Double, Double> f   ,
        final int                                 iCol)
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* Cannot map a column outside of `this` matrix\n"
                + "* `this` matrix has column indices [0, " + (nRows - 1)
                    + "]\n"
                + "* `iCol = " + iCol + "`\n"
            );
        }

        // * Apply `f` to the elements in column `iCol` and copy the other
        //   columns in `this` matrix
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (c == iCol)
                {
                    newMatrix[r][c] = f.apply(getElem(r, c));
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the matrix result of mapping all elements on the diagonal in `this`
       matrix with the function `f`

     * For non-square matrices the number of diagonals is minimum length of
       either the rows or columns

     * @param f
        * Lambda function to apply
     * @return
        * Matrix result of mapping all elements on the diagonal in `this`
          matrix with the function `f`
     */
    public Matrix mapDiag(final LambdaOperatorUnary<Double, Double> f)
    {
        int r, c;
        final double[][] newMatrix;

        // * Apply `f` to all elements on the diagonal and copy the
        //   off-diagonals from `this` matrix
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (r == c)
                {
                    newMatrix[r][c] = f.apply(getElem(r, c));
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the matrix result of mapping a sub-matrix from `(x, y)` to
       `(x + w, y + h)` in `this` matrix with the function `f`

     * @param f
         * Lambda function to apply
     * @param x
         * Index of row to start mapping
     * @param y
         * Index of column to start mapping
     * @param w
         * Number of columns to map starting from `x`
     * @param h
         * Number of row to map starting from `y`
     * @return
         * Matrix result of mapping a square area from `(x, y)` to
           `(x + w, y + h)` in `this` matrix with the function `f`
     * @throws Error
         * Cannot map a sub-matrix with a non-positive width or height
         * `w > 0`
         * `h > 0`
     * @throws Error
         * Sub-matrix from `(x, y)` to `(x + w, y + h)` to map must be within
           `this` matrix
         * `x >= 0`
         * `y >= 0`
         * `x + h < (columns in this matrix)`
         * `y + w < (rows in this matrix)`
     */
    public Matrix mapSubMatrix(
        final LambdaOperatorUnary<Double, Double> f,
        final int                                 x,
        final int                                 y,
        final int                                 w,
        final int                                 h)
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (w <= 0 || h <= 0))
        {
            throw new Error(
                "\n\n"
                + "* Cannot map a sub-matrix with a non-positive width or "
                    + "height\n"
                + "* `w = " + w + "`\n"
                + "* `h = " + h + "`\n"
            );
        }

        if (IS_DEBUG && (x < 0 || y < 0 || x + w > nCols || y + h > nRows))
        {
            throw new Error(
                "\n\n"
                + "* Sub-matrix from `(x, y)` to `(x + w, y + h)` to map must "
                    + "be within `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "* `(x, y) = (" + x + ", " + y + ")`\n"
                + "* `(x + w, y + h) = (" + (x + w) + ", " + (y + h) + ")`\n"
            );
        }

        // * Copy `this` matrix outside the mapping area and map inside the area
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (r >= y && r < y + h && c >= x && c < x + w)
                {
                    newMatrix[r][c] = f.apply(getElem(r, c));
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the matrix result of zipping the `other` matrix onto `this` matrix
       with the function `f`

     * @param other
         * Matrix to zip onto `this` matrix
         * Elements are the right operands of `f`
     * @param f
         * Lambda function to apply
     * @return
         * Matrix result of zipping the `other` matrix onto `this` matrix with
           the function `f`
     * @throws Error
         * `this` matrix and the `other` matrix must have the same dimensions
     */
    public Matrix zipMatrix(
        final Matrix                                       other,
        final LambdaOperatorBinary<Double, Double, Double> f    )
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (nRows != other.nRows || nCols != other.nCols))
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix and the `other` matrix must have the same "
                    + "dimensions\n"
                + "* `this` matrix size is " + nRows + "x" + nCols + "\n"
                + "* The `other` matrix size is " + other.nRows + "x"
                    + other.nCols + "\n"
            );
        }

        // * Zip corresponding elements together with `f`
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                newMatrix[r][c] = f.apply(getElem(r, c), other.getElem(r, c));
            }
        }

        return new Matrix(newMatrix);
    }


    /**
    * Gets the matrix result of zipping the `other` vector onto row `iRow` of
      `this` matrix with the function `f`

     * @param other
         * Vector to zip onto row `iRow` of `this` matrix
         * Elements are the right operands of `f`
     * @param f
         * Lambda function to apply
     * @param iRow
         * Row index of the row to zip onto
     * @return
         * Matrix result of zipping the `other` vector onto row `iRow` of `this`
           matrix with the function `f
     * @throws Error
         * The `other` matrix to zip onto `this` matrix must be a vector with as
           many elements as `this` matrix has columns
     * @throws Error
         * The row to zip must be within the matrix
         * `0 <= iRow < (rows in this matrix)`
     */
    public Matrix zipRow(
        final Matrix                                       other,
        final LambdaOperatorBinary<Double, Double, Double> f    ,
        final int                                          iRow )
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (!other.isVector() || other.nRows != nCols))
        {
            throw new Error(
                "\n\n"
                + "* The `other` matrix to zip onto `this` matrix must be a "
                    + "vector with as many elements as `this` matrix has "
                    + "columns\n"
                + "* `this` matrix has " + nCols + " columns\n"
                + "* The `other` matrix is size " + other.nRows + "x"
                    + other.nCols + "\n"
            );
        }

        if (IS_DEBUG && (iRow < 0 || iRow >= nRows))
        {
            throw new Error(
                "\n\n"
                + "* The row to zip must be within `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `iRow = " + iRow + "`\n"
            );
        }

        // * Zip the `other` vector onto row `iRow` of `this` matrix and copy
        //   the other rows
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (r == iRow)
                {
                    newMatrix[r][c] = f.apply(
                        getElem(r, c),
                        other.getElem(c, 0)
                    );
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
    * Gets the matrix result of zipping the `other` vector onto column `iCol` of
      `this` matrix with the function `f`

     * @param other
         * Vector to zip onto column `iCol` of `this` matrix
         * Elements are the right operands of `f`
     * @param f
         * Lambda function to apply
     * @param iCol
         * Column index of the column to zip onto
     * @return
         * Matrix result of zipping the `other` vector onto column `iCol` of
           `this` matrix with the function `f
     * @throws Error
         * The `other` matrix to zip onto `this` matrix must be a vector with as
           many elements as `this` matrix has rows
     * @throws Error
         * The column to zip must be within the matrix
         * `0 <= iCol < (columns in this matrix)`
     */
    public Matrix zipCol(
        final Matrix                                       other,
        final LambdaOperatorBinary<Double, Double, Double> f    ,
        final int                                          iCol )
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (!other.isVector() || other.nRows != nRows))
        {
            throw new Error(
                "\n\n"
                + "* The `other` matrix to zip onto `this` matrix must be a "
                    + "vector with as many elements as `this` matrix has rows\n"
                + "* `this` matrix has " + nRows + " rows\n"
                + "* The `other` matrix is size " + other.nRows + "x"
                    + other.nCols + "\n"
            );
        }

        if (IS_DEBUG && (iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* The column to zip must be within `this` matrix\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "* `iCol = " + iCol + "`\n"
            );
        }

        // * Zip the `other` vector onto column `iCol` of `this` matrix and copy
        //   the other columns
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (c == iCol)
                {
                    newMatrix[r][c] = f.apply(
                        getElem(r, c),
                        other.getElem(c, 0)
                    );
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the generic fold of `this` vector from top-to-bottom using the
       function `f` starting with value `init`

     * @param f
         * Lambda function to combine accumulative results
     * @param init
         * The initial value of the fold
     * @param <A>
         * Type to fold `this` vector into
         * Left operand and output of `f`
     * @return
         * Fold of `this` vector top-to-bottom with `f`
     * @throws Error
         * `this` matrix must be a vector
     */
    public <A> A foldVec(
        final LambdaOperatorBinary<A, Double, A> f   ,
        final A                                  init)
    throws Error
    {
        int r;
        A fold;

        if (IS_DEBUG && !isVector())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be a vector\n"
                + "* `this` matrix has " + nCols + " columns\n"
            );
        }

        // * Accumulate `fold` with the next value of `this` vector with` f`
        fold = init;
        for (r = 0; r < nRows; r += 1)
        {
            fold = f.apply(fold, getElem(r, 0));
        }

        return fold;
    }


    /* -------------------------- END: functionals -------------------------- */
    /* ------------------------- START:  operations ------------------------- *
     * Matrix and vector operations between 2 matrices or vectors
     * ---------------------------------------------------------------------- */


    /**
     * Gets the matrix product of `this` matrix and the `other` matrix

     * The matrix product has as many rows as the left matrix and as many
       columns as the right matrix
     * The element at `(i, j)` of the matrix product is the dot product of row
       `i` of the left matrix and column `j` of the right matrix

     * @param other
         * Right matrix to multiply with `this` matrix
     * @return
         * Product of `this` matrix and the `other` matrix
     * @throws ArithmeticException
         * `this` matrix must have as many columns as the `other` matrix has
           rows
     */
    public Matrix matrixProduct(final Matrix other) throws ArithmeticException
    {
        int r, c, e;
        final double[][] newMatrix;

        if (IS_DEBUG && nCols != other.nRows)
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `this` matrix must have as many columns as the `other` "
                    + "matrix has rows\n"
                + "* `this` matrix has " + nCols + " columns\n"
                + "* The `other` matrix has " + other.nRows + " rows\n"
            );
        }

        // * For each row of `this` matrix dot product each column of the
        //   `other` matrix
        newMatrix = new double[nRows][other.nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < other.nCols; c += 1)
            {
                // * Dot product row `r` of `this` matrix with column `c` of the
                //   `other` matrix
                newMatrix[r][c] = 0;
                for (e = 0; e < nCols; e += 1)
                {
                    newMatrix[r][c] += getElem(r, e) * other.getElem(e, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the inner product of `this` vector and the `other` vector

     * @param other
         * Vector to get inner product with `this` matrix
     * @return
         * Inner product of `this` vector and the `other` vector
     * @throws Error
         * `this` and the `other` matrix must both be vectors with the same
           number of elements
     */
    public double innerProduct(final Matrix other) throws Error
    {
        int r;
        double result;

        if (IS_DEBUG &&
            (!isVector() || !other.isVector() || nRows != other.nRows))
        {
            throw new Error(
                "\n\n"
                + "* `this` and the `other` matrix must both be vectors with "
                    + "the same number of elements\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
                + "* The `other` matrix is size " + other.nRows + "x"
                    + other.nCols + "\n"
            );
        }

        // * Sum the product of each corresponding component
        result = 0;
        for (r = 0; r < nRows; r += 1)
        {
            result += getElem(r, 0) * other.getElem(r, 0);
        }
        return result;
    }


    /**
     * Gets the outer product of `this` vector and the `other` vector

     * The outer product has as many rows as elements in the left vector and as
       many columns as elements in the right vector
     * The element at `(i, j)` of the outer product is the product of element
       `i` of the left vector and element `j` of the right vector

     * @param other
         * Right vector to get outer product with `this` vector
     * @return
         * Product of `this` vector and the `other` vector
     * @throws
         * `this` matrix and the `other` matrix must both be vectors
     */
    public Matrix outerProduct(final Matrix other) throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && (!isVector() || !other.isVector()))
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix and the `other` matrix must both be vectors "
                    + "\n"
                + "* `this` matrix has " + nCols + " columns\n"
                + "* The `other` matrix has " + other.nCols + " columns\n"
            );
        }

        // * Multiply each element of `this` vector by each element of the
        //   `other` vector
        newMatrix = new double[nRows][other.nRows];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < other.nRows; c += 1)
            {
                newMatrix[r][c] = getElem(r, 0) * getElem(c, 0);
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the solution vector (`xs`) of a linear system of equations in the
       form `this * xs = bs`

     * Decomposes `this` matrix into `this = L * U` and solves `L * ys = Pbs`
       then `U * xs = ys` where `xs` is the solution vector

     * @param bs
         * Coefficient vector (right-hand-side) of the linear system of
           equations
     * @return
         * Solution vector that solves the linear system of equations
         * Solution vector has as many elements as `this` matrix has rows
         * If there are multiple solutions only 1 is given
     * @throws Error
         * `this` matrix must be a square matrix
     * @throws Error
         * `bs` must be a vector with as many elements as `this` matrix has rows
     */
    public Matrix solve(final Matrix bs) throws Error
    {
        final Tuple3<Matrix, Matrix, Matrix> lupResult;
        final Matrix L, U, P;
        final Matrix Pbs;
        final Matrix xs, ys;

        if (IS_DEBUG && !isSquare())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be a square matrix\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        if (IS_DEBUG && (!bs.isVector() || bs.nRows != nRows))
        {
            throw new Error(
                "\n\n"
                + "* `bs` must be a vector with as many elements as `this` "
                    + "matrix has rows\n"
                + "* `this` matrix has " + nRows + " rows\n"
                + "* `bs` is size " + bs.nRows + "x" + bs.nCols + "\n"
            );
        }

        // * Decompose `this` matrix into LU(P)
        lupResult = lupDecomposition();
        L = lupResult.getItem0();
        U = lupResult.getItem1();
        P = lupResult.getItem2();

        // * Perpetuate `bs` to match permutations of `L` and `U`
        Pbs = P.matrixProduct(bs);

        // * Solve: `L * ys = Pbs` then `U * xs = ys`
        ys = L.forwardSubstitution(Pbs);
        xs = U.backwardSubstitution(ys);

        return xs;
    }


    /**
     * Gets the solution vector (`xs`) of a linear system of equations in the
       form `this * xs = bs` where `this` matrix is in column Echelon form

     * From top-to-bottom find the current row's pivot (first non-zero from
       right-to-left staring at the diagonal)
         * For each row without a pivot (all zeros) there is an additional
           infinite solution
         * For each pivot the corresponding unknown is calculated:
             * `knownInfluence = sum(known * knownCoefficient)`
                 * Values in `this` matrix left of the pivot multiplied by its
                   known value
                 * All values left of pivot are known due to column Echelon form
             * `unknownInfluence = correspondingCoefficient - knownInfluence`
             * `unknown = unknownInfluence / pivot`

     * @param bs
         * Coefficient vector (right-hand-side) of the linear system of
           equations
     * @return
         * Solution vector that solves the linear system of equations
         * Solution vector has as many elements as `this` matrix has rows
         * If there are multiple solution vectors only 1 is given
     * @throws Error
         * `this` matrix must be in column Echelon form
     * @throws Error
         * `bs` must be a vector with as many elements as `this` matrix has rows
     * @throws Error
         * System has no solution
         * A row of `this` matrix is all zeros, but the corresponding value in
           `bs` is non-zero
     */
    public Matrix forwardSubstitution(final Matrix bs) throws Error
    {
        int r, c;
        final double[][] xs;
        int iPivot;
        double pivot, knownInfluence, unknownInfluence;

        // * When in column echelon form each unknown variable has 0 or 1 pivots
        // * Unknowns with a pivot have a value while
        // * Unknowns with no pivots have infinite value
        if (IS_DEBUG && !isColEchelon())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be in column Echelon form\n"
            );
        }

        if (IS_DEBUG && (!bs.isVector() || bs.nRows != nRows))
        {
            throw new Error(
                "\n\n"
                + "* `bs` must be a vector with as many elements as `this` "
                    + "matrix has rows\n"
                + "* `this` matrix has " + nRows + " rows\n"
                + "* `bs` is size " + bs.nRows + "x" + bs.nCols + "\n"
            );
        }

        // * All unknowns are `1` by default
        // * Defaults cannot be `0` as although it is a solution many solutions
        //   cannot be zero vectors like eigenvectors
        // * If an unknown has infinite solutions it stays as `1`
        xs = new double[nRows][1];
        for (r = 0; r < nRows; r += 1)
        {
            xs[r][0] = 1;
        }

        // * From top-to-bottom find each row's pivot and calculates its value
        for (r = 0; r < nRows; r += 1)
        {
            // * From right-to-left from the diagonal find the first non-zero
            //   element (the row's pivot)
            iPivot = r;
            pivot = getElem(r, iPivot);
            while (iPivot > 0 && Numerical.isEqual(pivot, 0))
            {
                iPivot -= 1;
                pivot = getElem(r, iPivot);
            }

            // * All elements in the row = 0 move on to next row
            if (Numerical.isEqual(pivot, 0))
            {
                // * No solution if row = 0, but the row's coefficient != 0
                if (IS_DEBUG && !Numerical.isEqual(bs.getElem(r, 0), 0))
                {
                    throw new Error(
                        "\n\n"
                        + "* System does not have a solution\n"
                        + "* Equation (row) " + r + "of `this` matrix is all "
                            + "zeros, but its corresponding value in `bs` is "
                            + "non-zero\n"
                    );
                }

                // * System has infinite solutions - infinite variables are `1`
                continue;
            }

            // * Find row's weighted sum of known variables (left of `pivot`)
            knownInfluence = 0;
            for (c = 0; c < iPivot; c += 1)
            {
                knownInfluence += getElem(r, c) * xs[c][0];
            }

            // * Unknown variable is the unknown influence divided by the
            //   `pivot`
            unknownInfluence = bs.getElem(r, 0) - knownInfluence;
            xs[iPivot][0] = unknownInfluence / pivot;
        }

        return new Matrix(xs);
    }


    /**
     * Gets the solution vector (`xs`) of a linear system of equations in the
       form `this * xs = bs` where `this` matrix is in row Echelon form

     * From bottom-to-top find the current row's pivot (first non-zero from
       left-to-right staring at the diagonal)
         * For each row without a pivot (all zeros) there is an additional
           infinite solution
         * For each pivot the corresponding unknown is calculated:
             * `knownInfluence = sum(known * knownCoefficient)`
                 * Values in `this` matrix right of the pivot multiplied by its
                   known value
                 * All values right of pivot are known due to row Echelon form
             * `unknownInfluence = correspondingCoefficient - knownInfluence`
             * `unknown = unknownInfluence / pivot`

     * @param bs
         * Coefficient vector (right-hand-side) of the linear system of
           equations
     * @return
         * Solution vector that solves the linear system of equations
         * Solution vector has as many elements as `this` matrix has rows
         * If there are multiple solution vectors only 1 is given
     * @throws Error
         * `this` matrix must be in row Echelon form
     * @throws Error
         * `bs` must be a vector with as many elements as `this` matrix has rows
     * @throws Error
         * System has no solution
         * A row of `this` matrix is all zeros, but the corresponding value in
           `bs` is non-zero
     */
    public Matrix backwardSubstitution(final Matrix bs) throws Error
    {
        int r, c;
        final double[][] xs;
        int iPivot;
        double pivot, knownInfluence, unknownInfluence;

        // * When in row echelon form each unknown variable has 0 or 1 pivots
        // * Unknowns with a pivot have a value while
        // * Unknowns with no pivots have infinite value
        if (IS_DEBUG && !isRowEchelon())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be in row Echelon form\n"
            );
        }

        if (IS_DEBUG && (!bs.isVector() || bs.nRows != nRows))
        {
            throw new Error(
                "\n\n"
                + "* `bs` must be a vector with as many elements as `this` "
                    + "matrix has rows\n"
                + "* `this` matrix has " + nRows + " rows\n"
                + "* `bs` is size " + bs.nRows + "x" + bs.nCols + "\n"
            );
        }

        // * All unknowns are `1` by default
        // * Defaults cannot be `0` as although it is a solution many solutions
        //   cannot be zero vectors like eigenvectors
        // * If an unknown has infinite solutions it stays as `1`
        xs = new double[nRows][1];
        for (r = 0; r < nRows; r += 1)
        {
            xs[r][0] = 1;
        }

        // * From bottom-to-top find each row's pivot and calculates its value
        for (r = nRows - 1; r >= 0; r -= 1)
        {
            // * From left-to-right from the diagonal find the first non-zero
            //   element (the row's pivot)
            iPivot = r;
            pivot = getElem(r, iPivot);
            while (iPivot < nCols - 1 && Numerical.isEqual(pivot, 0))
            {
                iPivot += 1;
                pivot = getElem(r, iPivot);
            }

            // * All elements in the row = 0 move on to next row
            if (Numerical.isEqual(pivot, 0))
            {
                // * No solution if row = 0, but the row's coefficient != 0
                if (IS_DEBUG && !Numerical.isEqual(bs.getElem(r, 0), 0))
                {
                    throw new Error(
                        "\n\n"
                        + "* System does not have a solution\n"
                        + "* Equation (row) " + r + "of `this` matrix is all "
                            + "zeros, but its corresponding value in `bs` is "
                            + "non-zero\n"
                    );
                }

                // * System has infinite solutions - infinite variables are `1`
                continue;
            }

            // * Find row's weighted sum of known variables (right of `pivot`)
            knownInfluence = 0;
            for (c = iPivot + 1; c < nCols; c += 1)
            {
                knownInfluence += getElem(r, c) * xs[c][0];
            }

            // * Unknown variable is the unknown influence divided by the
            //   `pivot`
            unknownInfluence = bs.getElem(r, 0) - knownInfluence;
            xs[iPivot][0] = unknownInfluence / pivot;
        }

        return new Matrix(xs);
    }


    /* -------------------------- END:  operations -------------------------- */
    /* -------------------- START: derived single values -------------------- *
     * Single value results calculated from a matrix or vector
     * ---------------------------------------------------------------------- */


    /**
     * Gets the trace of `this` matrix

     * The trace of a matrix is the sum of its diagonals

     * @return
         * The trace of `this` matrix
     * @throws Error
         * `this` matrix must be square
     */
    public double trace() throws Error
    {
        int d;
        double trace;

        if (IS_DEBUG && !isSquare())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        // * Sum the diagonals
        trace = 0;
        for (d = 0; d < nRows; d += 1)
        {
            trace += getElem(d, d);
        }

        return trace;
    }


    /**
     * Gets the determinate of `this` matrix

     * The determinate of a matrix is the sum from `[0, j]` of each cofactor at
       `(i, j)` multiplied the element at `(i, j)` of row `i`
         * This method sums over row `0`

     * @return
         * Determinate of `this` matrix
     * @throws ArithmeticException
         * `this` matrix must be square
     */
    public double determinate() throws ArithmeticException
    {
        int c;
        double det;

        if (IS_DEBUG && !isSquare())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        // * Single element matrix (base case)
        if (nRows == 1)
        {
            return getElem(0, 0);
        }

        // * Sum the first row's cofactors multiplied their corresponding
        //   element
        det = 0;
        for (c = 0; c < nCols; c += 1)
        {
            det += getElem(0, c) * cofactor(0, c);
        }

        return det;
    }


    /**
     * Gets the cofactor of `this` matrix at element `(iRow, iCol)`

     * The cofactor at element `(iRow, iCol)` is calculated:
         * `cofactor = (-1)^(i + j) * det(minor at i and j)`

     * @param iRow
         * Index of row to get cofactor of
     * @param iCol
         * Index of column to get cofactor of
     * @return
         * Cofactor of `this` matrix at element `(iRow, iCol)`
     * @throws Error
         * Cannot get a cofactor outside of `this` matrix
         * `0 <= iRow < (rows in this matrix)`
         * `0 <= iCol < (columns in this matrix)`
     */
    public double cofactor(
        final int iRow,
        final int iCol)
    throws Error
    {
        final int sign;

        if (IS_DEBUG
            && (iRow < 0 || iRow >= nRows || iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* Cannot get a cofactor outside of `this` matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "* `(iRow, iCol) = (" + iRow + ", " + iCol + ")`\n"
            );
        }

        // * `cofactor = (-1)^(iRow + iCol) * det(minor at iRow and iCol)`
        sign = (iRow + iCol) % 2 == 0 ? 1 : -1;
        return sign * minor(iRow, iCol).determinate();
    }


    /**
     * Gets a vector of all the eigenvalues of `this` matrix
         * If `this` matrix has less eigenvalues than its number of rows (less
           than full rank) then the eigenvalues vector is padded with `0`

     * Uses the QR algorithm to converge to the eigenvalues
     * Let, `A = this`
         * Decompose `A` into the QR decomposition: `A = Q * R`
         * Find the next `A` with `R * Q = A`
         * Repeat until `A` converges to an upper triangular matrix
         * Once `A` converges the eigenvalues are on the diagonal of `A` in
           descending order top-left to bottom-right

     * @return
         * Vector of all the eigenvalues of `this` matrix
     * @throws Error
         * `this` matrix must be symmetric
         * The QR algorithm can only guarantee the convergence to real
           eigenvalues which symmetric matrices are guaranteed to have
     */
    public Matrix eigenvalues() throws Error
    {
        int i;
        Matrix A;
        Tuple2<Matrix, Matrix> qrResult;
        Matrix Q, R;

        if (IS_DEBUG && !isSymmetric())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be symmetric\n"
                + "* `this` matrix size is " + nRows + "x" + nCols + "\n"
            );
        }

        // * Perform QR algorithm until `A` converges to a upper triangular
        A = this;
        for (i = 0; i < MAX_EIGN_ITERATIONS && !A.isUpperTri(); i += 1)
        {
            // * Get `A = Q * R` of current `A`
            qrResult = A.qrDecomposition();
            Q = qrResult.getItem0();
            R = qrResult.getItem1();

            // * Multiply `R * Q` to get next `A`
            A = R.matrixProduct(Q);
        }

        // * Eigenvalues are on the diagonal in descending order top-left to
        //   bottom-right
        return A.getDiag();
    }


    /**
     * Gets the index of the row with the largest absolute value in column
       `iPivot` under and including row `iPivot` of `this` matrix

     * @param iPivot
         * Index of diagonal to find the pivot of
     * @return
         * The index of the row with the largest absolute value in column
           `iPivot` under and including row `iPivot`
     * @throws
         * The diagonal to pivot on must be within `this` matrix
         * `0 <= iPivot < min(columns or rows in this matrix)`
     */
    public int partialPivot(final int iPivot) throws Error
    {
        int r;
        int iLargest;
        double largest, current;

        if (IS_DEBUG && (iPivot < 0 || iPivot >= nCols || iPivot >= nRows))
        {
            throw new Error(
                "\n\n"
                + "* The diagonal to pivot on must be within `this` matrix\n"
                + "* `this` matrix has diagonal indices [0, "
                    + (Math.min(nRows, nCols) - 1) + "]\n"
                + "* `iPivot = " + iPivot + "`\n"
            );
        }

        // * Find the largest absolute value in column `iPivot` under and
        //   including row `iPivot`
        iLargest = iPivot;
        largest = Math.abs(getElem(iLargest, iPivot));
        for (r = iLargest + 1; r < nRows; r += 1)
        {
            current = Math.abs(getElem(r, iPivot));
            if (current > largest)
            {
                iLargest = r;
                largest = current;
            }
        }

        return iLargest;
    }


    /**
     * Gets the Euclidean distance of `this` vector

     * Euclidean distance of a vector is the square root the sum of squares each
       of its elements

     * @return
         * The Euclidean distance of `this` vector
     * @throws Error
         * `this` matrix must be a vector
     */
    public double distEuclidean() throws Error
    {
        if (IS_DEBUG && !isVector())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be a vector\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        // * Square root the sum of squares of each element
        return Math.sqrt(foldVec((x, y) -> x + (y * y), 0.0));
    }


    /* --------------------- END: derived single values --------------------- */
    /* ---------------------- START:  derived matrices ---------------------- *
     * Matrices that are calculated from a matrix or vector
     * ---------------------------------------------------------------------- */


    /**
     * Gets the transpose of `this` matrix

     * The transpose flips a matrix across its diagonal where element `(i, j)`
       becomes `(j, i)` and element `(j, i)` becomes `(i, j)`

     * @return
         * Transpose matrix of `this` matrix
     */
    public Matrix transpose()
    {
        int r, c;
        final double[][] newMatrix;

        // * Flip the dimension and axis of elements of `this` matrix
        newMatrix = new double[nCols][nRows];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                newMatrix[c][r] = getElem(r, c);
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the minor of `this` matrix at `(iRow, iCols)`

     * The minor of a matrix at `(iRow, iCols)` is original matrix with row `i`
       and column `j` removed

     * @param iRow
         * Index of row to get minor of
     * @param iCol
         * Index of column to get minor of
     * @return
         * The minor of `this` matrix at `(iRow, iCols)`
     * @throws Error
         * The minor to get must be within this matrix
         * `0 <= iRow < (rows in this matrix)`
         * `0 <= iCol < (columns in this matrix)`
     */
    public Matrix minor(
        final int iRow,
        final int iCol)
    throws Error
    {
        int r, c, rMinor, cMinor;
        final double[][] newMatrix;

        if (IS_DEBUG
            && (iRow < 0 || iRow >= nRows || iCol < 0 || iCol >= nCols))
        {
            throw new Error(
                "\n\n"
                + "* The minor to get must be within this matrix\n"
                + "* `this` matrix has row indices [0, " + (nRows - 1) + "]\n"
                + "* `this` matrix has column indices [0, " + (nCols - 1)
                    + "]\n"
                + "* `(iRow, iCol) = (" + iRow + ", " + iCol + ")`"
            );
        }

        // * Copy `this` matrix excluding row `iRow` and column `iCol`
        newMatrix = new double[nRows - 1][nCols - 1];
        for (r = 0, rMinor = 0; r < nRows; r += 1)
        {
            if (r == iRow)
            {
                continue;
            }

            for (c = 0, cMinor = 0; c < nCols; c += 1)
            {
                if (c == iCol)
                {
                    continue;
                }

                newMatrix[rMinor][cMinor] = getElem(r, c);

                // * Not incremented when skipping column `iCol`
                cMinor += 1;
            }

            // * Not incremented when skipping row `iRow`
            rMinor += 1;
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the adjugate of `this` matrix

     * The adjugate of a matrix is the cofactors of the matrix flipped across
       its diagonal where the element at `(i, j)` is the cofactor at `(j, i)`

     * @return
        * The adjugate of `this` matrix
     * @throws ArithmeticException
         * `this` matrix must be square
     */
    public Matrix adjugate() throws ArithmeticException
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && !isSquare())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size" + nRows + "x" + nCols + "\n"
            );
        }

        // * Adjugate is the transpose of the cofactor matrix
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                // * Flip each row and column to get transpose of the cofactor
                //   matrix
                newMatrix[c][r] = cofactor(r, c);
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the cofactor matrix of `this` matrix

     * The cofactor matrix is a matrix of all a matrix's cofactors where where
       the element at `(i, j)` is the cofactor at `(i, j)`

     * @return
         * The cofactor matrix of `this` matrix
     * @throws ArithmeticException
         * `this` matrix must be square
     */
    public Matrix cofactorMatrix()
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && !isSquare())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size" + nRows + "x" + nCols + "\n"
            );
        }

        // * Each element is the cofactor of `this` matrix at that element
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                newMatrix[r][c] = cofactor(r, c);
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Gets the inverse of `this` matrix

     * The inverse of a matrix is its adjugate divided by its determinate

     * @return
         * The inverse of `this` matrix
     * @throws ArithmeticException
         * `this` matrix must be square
     * @throws ArithmeticException
         * `this` matrix must have a non-zero determinate
     */
    public Matrix inverse() throws ArithmeticException
    {
        final double det;

        if (IS_DEBUG && !isSquare())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        det = determinate();

        if (IS_DEBUG && Numerical.isEqual(det, 0))
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `this` matrix is not invertible\n"
                + "* `this` matrix's determinate = 0\n"
            );
        }

        // * Convert adjugate to the inverse by dividing each element by `det`
        return adjugate().mapMatrix(x -> x / det);
    }


    /**
     * Gets an eigenvector of `this` matrix for the `eigenvalue`
         * The eigenvector is normalised

     * An eigenvector for a matrix is the vector solution to:
         * `(A - lambda * I) * vs = bs`
             * `A`      is the matrix
             * `lambda` is the eigenvalue
             * `I`      is the identity
             * `vs`     is the eigenvector
             * `bs`     is a vector of zeros

     * @param eigenvalue
         * An eigenvalue of `this` matrix to get an eigenvector of
         * This method does not check `eigenvalue` is an eigenvalue of `this`
           matrix
     * @return
         * An eigenvector of `this` matrix for the `eigenvalue`
     * @throws Error
         * `this` matrix must be square
     */
    public Matrix eigenvector(final double eigenvalue) throws Error
    {
        if (IS_DEBUG && !isSquare())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        // * Solve: `(A - lambda * I) * vs = bs` then normalise
        return mapDiag(x -> x - eigenvalue).solve(zeros(nRows, 1)).unitVec();
    }


    /**
     * Gets both the singular values and right-singular vectors of `this` matrix

     * The singular-pairs of a matrix are found using the eigen-pairs of the
       Gram matrix
         * `gram = A^T * A`
             * `A` is the matrix
     * The right-singular vectors are the eigenvectors of the Gram matrix
     * The singular values are the square roots of the eigenvalues of the Gram
       matrix

     * @return
         * The singular values and right-singular vectors of `this` matrix
         * The singular values are in descending order
         * The singular vectors are normalised and are in column order of their
           corresponding singular value
     */
    public Tuple2<Matrix, Matrix> singular()
    {
        int i;
        final Matrix gram;
        final Matrix eigenvalues, singularValues;
        Matrix eigenvector, eigenvectors;
        double eigenvalue;

        // * A Gram matrix is symmetric so its eigenvalues are real and
        //   non-negative
        gram = transpose().matrixProduct(this);

        eigenvalues = gram.eigenvalues();
        eigenvectors = zeros(gram.nRows, gram.nCols);

        // * Find the eigenvector of each `eigenvalues`
        for (i = 0; i < eigenvectors.nRows; i += 1)
        {
            eigenvalue = eigenvalues.getElem(i, 0);

            // * As `eigenvalues` are in descending order and are non-negative
            //   once a `0` `eigenvalue` is found all of the eigenvectors have
            //   been found as `0` is just the padding for non-existent
            //   eigenvalues
            if (Numerical.isEqual(eigenvalue, 0))
            {
                break;
            }

            eigenvector = gram.eigenvector(eigenvalue);
            eigenvectors = eigenvectors.setSubMatrix(eigenvector, 0, i);
        }

        singularValues = eigenvalues.mapMatrix(x -> Math.sqrt(x));

        return new Tuple2<>(singularValues, eigenvectors);
    }


    /**
     * Gets a pseudoinverse of `this` matrix
         * All matrices have pseudoinverses:
             * `A * A^+ * A = A`
                 * `A`   is the matrix
                 * `A^+` is a pseudoinverse
             * Pseudoinverses are not unique unless the matrix is invertible
               then the pseudoinverse is the matrix inverse

     * A pseudoinverse of a matrix can be found using the SVD of the matrix
         * `pseudoinverse = (U * S^-1 * V^T)^T`
             * As `S` is a diagonal matrix it can be inverted by taking the
               reciprocals of each of the non-zero diagonals

     * @return
         * A pseudoinverse of `this` matrix
     */
    public Matrix pseudoinverse()
    {
        final Tuple3<Matrix, Matrix, Matrix> svdResult;
        final Matrix U, S, Vt;
        final Matrix SInv;

        // * Decompose `this` matrix into SVD
        svdResult = svd();
        U = svdResult.getItem0();
        S = svdResult.getItem1();
        Vt = svdResult.getItem2();

        // * Take the reciprocal of each non-zero diagonals to invert
        SInv = S.mapDiag(x -> Numerical.isEqual(x, 0) ? 0 : 1 / x);

        // * `pseudoinverse = (U * S^(-1) * V^T)^T`
        return U.matrixProduct(SInv).matrixProduct(Vt).transpose();
    }


    /**
     * Gets the matrix result of swapping row `iRow0` and row `iRow1` of `this`
       matrix

     * @param iRow0
         * Index of row to swap with row `iRow1`
     * @param iRow1
         * Index of row to swap with row `iRow0`
     * @return
         * Matrix result of row `iRow0` and `iRow1` swapped
     * @throws Error
         * Both rows to swap must be within `this` matrix
         * `0 <= iRow0 < (this matrix rows)`
         * `0 <= iRow1 < (this matrix rows)`
     */
    public Matrix rowSwap(
        final int iRow0,
        final int iRow1)
    throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG &&
            (iRow0 < 0 || iRow0 >= nRows || iRow1 < 0 || iRow1 >= nRows))
        {
            throw new Error(
                "\n\n"
                + "* Both rows to swap must be within this matrix\n"
                + "* This matrix has row indices [0, " + (nRows - 1) +"]\n"
                + "* `iRow0 = " + iRow0 + "`\n"
                + "* `iRow1 = " + iRow1 + "`\n"
            );
        }

        // * When at an element in row `iRow0` copy the element in `iRow1`
        // * When at an element in row `iRow1` copy the element in `iRow0`
        // * When at an element not in row `iRow0` or `iRow0` copy that element
        newMatrix = new double[nRows][nCols];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nCols; c += 1)
            {
                if (r == iRow0)
                {
                    newMatrix[r][c] = getElem(iRow1, c);
                }
                else if (r == iRow1)
                {
                    newMatrix[r][c] = getElem(iRow0, c);
                }
                else
                {
                    newMatrix[r][c] = getElem(r, c);
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /**
     * Get the unit (normalised) vector of `this` vector

     * The unit vector is the vector divided by its length

     * @return
         * The unit vector of `this` vector
     * @throws Error
         * `this` matrix must be a vector
     * @throws Error
         * Cannot get the unit vector of a zero length vector
     */
    public Matrix unitVec() throws Error
    {
        final double dist;

        if (IS_DEBUG && !isVector())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be a vector\n"
                + "* `this` Matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        dist = distEuclidean();
        if (IS_DEBUG && Numerical.isEqual(dist, 0))
        {
            throw new Error(
                "\n\n"
                + "* Cannot get the unit vector of a zero length vector\n"
                + "* `this` vector's length is " + dist + "\n"
            );
        }

        // * The unit vector is each element divided by `this` vector's length
        return mapMatrix(x -> x / dist);
    }


    /**
     * Gets the Householder reflection of `this` vector in the `i` direction

     * The Householder reflection matrix is the matrix such that:
         * `H * xs = ||xs|| * es`
             * `H`  is the Householder reflection
             * `xs` is the vector
             * `es` is a unit vector of the direction, `i, reflect in

     * The Householder reflection matrix is found by:
         * `vs = xs - ||xs|| * es`
             * To avoid an undefined case when `||xs||` is equal to element `i`
               of `xs`
                 * element `i` of `es` is `1` when element `i` of `xs` is `>= 0`
                 * element `i` of `es` is `-1` when element `i` of `xs` is `< 0`
         * `us = normalised vs`
         * `H = I - 2 * us * us^T`

     * @param i
         * The index of the direction to reflect in
     * @return
         * The Householder reflection of `this` vector in the `i` direction
     * @throws Error
         * `this` matrix must be a vector
     * @throws Error
         * Householder reflection direction must be within `this` vector
         * `0 <= i < (elements in this vector)`
     * @throws Error
         * Cannot get the Householder reflection of a zero length vector
     */
    public Matrix householderReflection(final int i) throws Error
    {
        final double dist;
        final int sign;
        final Matrix v, u, projection;

        if (IS_DEBUG && !isVector())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be a vector\n"
                + "* `this` matrix size is " + nRows + "x" + nCols + "\n"
            );
        }

        if (IS_DEBUG && (i < 0 || i >= nRows))
        {
            throw new Error(
                "\n\n"
                + "* Householder reflection direction must be within `this` "
                    + "vector\n"
                + "* `this` vector has element indices  [0, " + nRows + "]\n"
                + "* `i = " + i + "`\n"
            );
        }

        dist = distEuclidean();
        if (IS_DEBUG && Numerical.isEqual(dist, 0))
        {
            throw new Error(
                "\n\n"
                + "* Cannot get the Householder reflection of a zero length "
                    + "vector\n"
                + "* `this` vector's length is 0\n"
            );
        }

        // * Use the sign of element `i` so equation is defined in a special
        //   case when only element `i` has value and all other elements are `0`
        sign = getElem(i, 0) >= 0 ? 1 : -1;

        // * Find the reflection hyperplane in direction `i`
        v = mapElem(x -> x + (sign * dist), i, 0);
        u = v.unitVec();
        projection = u.outerProduct(u);

        // * Find Householder matrix: `H = I - 2 * u * u^T`
        return projection.mapMatrix(x -> -2 * x).mapDiag(x -> 1 + x);
    }


    /**
     * Gets the matrix of `this` vectors's elements on the diagonals with `0`
       elsewhere

     * @return
         * Diagonalised matrix of `this` vector
         * Diagonalised matrix is a square matrix size equal to the number of
           elements in `this` vector
     * @throws Error
         * `this` matrix must be a vector
     */
    public Matrix diagonaliseVec() throws Error
    {
        int r, c;
        final double[][] newMatrix;

        if (IS_DEBUG && !isVector())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be a vector\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        // * Place elements on the diagonal with off-diagonals as `0`
        newMatrix = new double[nRows][nRows];
        for (r = 0; r < nRows; r += 1)
        {
            for (c = 0; c < nRows; c += 1)
            {
                if (r == c)
                {
                    newMatrix[r][c] = getElem(r, 0);
                }
                else
                {
                    newMatrix[r][c] = 0;
                }
            }
        }

        return new Matrix(newMatrix);
    }


    /* ----------------------- END:  derived matrices ----------------------- */
    /* ----------------------- START:  Decompositions ----------------------- *
     * Methods that decompose a matrix into 1 or more other matrices
     * ---------------------------------------------------------------------- */


    /**
     * Gets the QR decomposition (`this = Q * R`) of `this` matrix

     * `this = Q * R` is found by:
         * `Q` starts as the identity
         * `R` starts as `this` matrix
         * For each column `i` along the diagonal of `this` matrix:
             * Get the Householder reflection, `H`, of the vector on column `i`
               of `this` matrix with elements from `[0, i]` set to `0`
             * Get next the `Q` by right multiplying `H` by the current `Q`
             * Get next the `R` by left multiplying `H` by the current `R`

     * @return
         * QR decomposition of `this` matrix
     */
    public Tuple2<Matrix, Matrix> qrDecomposition()
    {
        int d;
        final int n;
        Matrix Q, R;
        Matrix xs;
        Matrix H;

        Q = identity(nRows);
        R = this;

        n = Math.min(nRows, nCols);
        for (d = 0; d < n; d += 1)
        {
            // * Get column `d` and replace elements from `[0, d - 1]` with `0`
            xs = R.getCol(d);
            if (d > 0)
            {
                xs = xs.mapSubMatrix(x -> 0.0, 0, 0, 1, d);
            }

            // * Column `d` already has all zeros from `[d, nRows - 1]`
            // * Zero length vectors do not have a Householder reflection
            if (Numerical.isEqual(xs.distEuclidean(), 0))
            {
                continue;
            }

            // * Find a Householder reflection for current column
            H = xs.householderReflection(d);

            // * Get next `Q` and `R` from `Q = Q * H` and `R = H * R`
            Q = Q.matrixProduct(H);
            R = H.matrixProduct(R);
        }

        return new Tuple2<>(Q, R);
    }


    /**
     * Gets the LU(P) decomposition (`P * this = L * U`) of `this` matrix

     * `P * this = L * U` found by:
         * `L` starts as a zero matrix
         * `U` starts as `this` matrix
         * `P` starts as an identity matrix
         * For each column, `i`, of `U`:
             * Find the partial pivot at the diagonal `i`
             * Swap rows of `L`, `U` and `P` so the partial pivot is at row `i`
             * For each row, `j`, below row `i` of `U`:
                 * Find `l = (element (i, j) of U) / (element (i, i) of U)`
                 * Element `(i, j)` of `L` is equal to `l`
                 * Subtract row `i` scaled by `l` from row `j` of `U`
         * Place `1` along the diagonals of `L`

     * @return
         * LU(P) decomposition of `this` matrix
     * @throws Error
         * `this` matrix must be square
     */
    public Tuple3<Matrix, Matrix, Matrix> lupDecomposition() throws Error
    {
        int r, d;
        Matrix L, U, P;
        int iPivot;
        double pivot;
        final LambdaValue<Double> l;
        Matrix pivotRow;

        if (IS_DEBUG && !isSquare())
        {
            throw new Error(
                "\n\n"
                + "* `this` matrix must be square\n"
                + "* `this` matrix is size " + nRows + "x" + nCols + "\n"
            );
        }

        L = zeros(nRows, nRows);
        U = this;
        P = identity(nRows);

        l = new LambdaValue<>(0.0);

        // * Convert `U` to row echelon form and update `L` so `P * A = L * U`
        for (d = 0; d < nCols - 1; d += 1)
        {
            // * Swap the rows under diagonal `d` so largest absolute value of
            //   `U` is on its diagonal
            // * `L`, `U` and `P` all perform the same swap so their
            //   permutations match
            iPivot = U.partialPivot(d);
            L = L.rowSwap(d, iPivot);
            U = U.rowSwap(d, iPivot);
            P = P.rowSwap(d, iPivot);

            // * If `pivot = 0` then this column is already in row echelon form
            //   so skip this column
            pivot = U.getElem(d, d);
            if (Numerical.isEqual(pivot, 0))
            {
                continue;
            }

            // * In column `d` reduce each element under row `d` to `0`
            pivotRow = U.getRow(d);
            for (r = d + 1; r < nRows; r += 1)
            {
                // * Element at `(d, r)` of `L` is how much `pivotRow` is scaled
                //   by when reducing row `r` of `U`
                l.setValue(U.getElem(r, d) / pivot);
                L = L.mapElem(x -> l.getValue(), r, d);

                // * Subtract `l * pivotRow` from row `i` to reduce element
                //   `(i, j)` of `U` to `0`
                U = U.zipRow(pivotRow, (x, y) -> x - (l.getValue() * y), r);
            }

        }

        // * `L` has `0` on its diagonal - change to `1`
        L = L.mapDiag(x -> 1.0);

        return new Tuple3<>(L, U, P);
    }


    /**
     * Gets the Singular Value Decomposition (SVD) (`this = U * S * V^T`) of
       `this` matrix

     * `this = U * S * V^T` found by:
         * `V` is the left-singular vectors of `this` matrix
         * `S` is a diagonal matrix with the square roots of the singular values
           of `this` matrix
             * As `S` is a diagonal matrix `S^(-1)` is found by taking the
               reciprocals of its non-zero diagonals
         * `U = this * V * S^(-1)`
             * `U` is the right-singular vectors of `this` matrix

     * @return
         * SVD `this` matrix
     */
    public Tuple3<Matrix, Matrix, Matrix> svd()
    {
        final Tuple2<Matrix, Matrix> singularResult;
        final Matrix singularValues, singularVectors;
        final Matrix U, S, Vt;
        final Matrix SInv;

        // * Get the singular values and left-singular vectors
        singularResult = singular();
        singularValues = singularResult.getItem0();
        singularVectors = singularResult.getItem1();

        // * `Vt` is the transpose of the right-singular vectors and `S` is a
        //   diagonal matrix of the singular value in descending order
        Vt = singularVectors.transpose();
        S = singularValues.diagonaliseVec();

        // * `S` is a diagonal matrix take reciprocals of its non-zero diagonals
        //   to invert
        SInv = S.mapDiag(x -> Numerical.isEqual(x, 0) ? 0 : 1 / x);

        // * `U = this * V * S^(-1)`
        U = matrixProduct(singularVectors).matrixProduct(SInv);

        return new Tuple3<>(U, S, Vt);
    }


    /* ------------------------ END:  Decompositions ------------------------ */
}
