package titan.math;
public class Matrix {

  public double vals[][];
  /**
   *Construstor to initalize x,y and z coordinates
   *
   * @param   xValue    x coordinate
   * @param   yValue    y coordinate
   * @param   zValue    z coordinate
   */

  public Matrix(int rows, int cols) {
    this.vals = new double[rows][cols];
  }

  public Matrix(double[][] vals) {

    this.vals = new double[vals.length][vals[0].length];
    for(int i = 0; i < vals.length; i++) {
      for(int j = 0; j < vals[i].length; j++) {
        this.vals[i][j] = vals[i][j];
      }
    }
  }

  /**
   * method to get the entry at row i and column j of the matrix object
   * @return entry row i, column j of type double
   */

  public double get(int i, int j) {
    return this.vals[i][j];
  }

  /**
   * method to set the entry at row i and column j of the matrix object
   * @param  x  value to insert
   * @param i the row to insert x at
   * @param j the column to insert x at
   */

  public void set(double x, int i, int j) {
    this.vals[i][j] = x;
  }

  /**
   * Method to add 2 Matrix objects
   * @param   other    Matrix object to be added
   * @return  new Matrix object which is the sum of the original 2 Matrix objects
   */

  public Matrix add(Matrix other) {
    if(other.vals.length != this.vals.length || this.vals[0].length != other.vals[0].length) {
      throw new RuntimeException("Matrix dimension mismatch!");
    }
    double[][] resultingValues = new double[this.vals.length][this.vals[0].length];
    for(int i = 0; i < this.vals.length; i++) {
      for(int j = 0; j < this.vals[0].length; j++) {
        resultingValues[i][j] = this.vals[i][j] + other.vals[i][j];
      }
    }
    return new Matrix(resultingValues);
  }

  /**
   * Method to subtract 2 Matrix objects
   * @param Matrix object to be subtracted
   * @return new Matrix object which is the difference of the 2 original Matrix objects
   */

  public Matrix sub(Matrix other) {
    if(other.vals.length != this.vals.length || this.vals[0].length != other.vals[0].length) {
      throw new RuntimeException("Matrix dimension mismatch!");
    }
    double[][] resultingValues = new double[this.vals.length][this.vals[0].length];
    for(int i = 0; i < this.vals.length; i++) {
      for(int j = 0; j < this.vals[0].length; j++) {
        resultingValues[i][j] = this.vals[i][j] - other.vals[i][j];
      }
    }
    return new Matrix(resultingValues);
  }

  /**
   * Method to return a scalar multiple of a Matrix object
   * @param   scalar    the scalar to be multiplied to the Matrix object
   * @return a new Matrix object whose values are the scalar multiple of the original values
   */

  public Matrix mul(double scalar) {
    double[][] resultingValues = new double[this.vals.length][this.vals[0].length];
    for(int i = 0; i < this.vals.length; i++) {
      for(int j = 0; j < this.vals[0].length; j++) {
        resultingValues[i][j] = this.vals[i][j] * scalar;
      }
    }
    return new Matrix(resultingValues);
  }

  /**
   * Method to add scalar multiple of Matrix 2 to Matrix 1
   * @param   scalar    a scalar value to be multiplied to Matrix 2
   * @param   other     Matrix 2
   * @return  result of M1+(s*M2)
   */

  public Matrix addMul(double scalar, Matrix other) {
    Matrix v2 = (Matrix) other;
    return add(v2.mul(scalar));
  }

  public int rows() {
    return this.vals.length;
  }

  public int columns() {
    return this.vals[0].length;
  }

  /*
  * @return A string in this format:
  * Matrix[-1.0, 2, -3.0] should print out [-1.0,2.0,-3.0]
  */
  public String toString() {
    String result = "[ ";
    for(int i = 0; i < this.vals.length; i++) {
      for(int j = 0; j < this.vals[0].length; j++) {
        result += "\t" + this.vals[i][j];
      }
      result += "\n";
    }
    result += "]";
    return result;
  }
}
