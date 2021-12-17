package titan.pathplanner;
import titan.math.Matrix;
import titan.probe.*;
public class GradientDescent {
  static double delta = 1e-7;
  static final double ACCURACY = 1e-7;
  static final double LEARNING_RATE = 0.1;
  static final double momentum = 0.7;
  public static Matrix run(CostFunction simulation, double[][] initialValues) {
    Matrix x0 = new Matrix(initialValues);
    Matrix gradient = getGradient(x0, simulation);
    Matrix tmp = gradient.mul(LEARNING_RATE);
    Matrix lastChange = tmp;
    Matrix x1 = x0.sub(tmp);
    double error = 0;
    for(int i = 0; i < x0.columns(); i++) {
      error += Math.pow((x0.get(0, i) - x1.get(0, i)), 2);
    }
    error = Math.sqrt(error);
    System.out.println("Gradient-Descent error: " + error);
    while(error > ACCURACY) {
      x0 = x1;
      gradient = getGradient(x0, simulation);
      tmp = gradient.mul(LEARNING_RATE);
      tmp = tmp.add(lastChange.mul(momentum));
      x1 = x0.sub(tmp);
      error = 0;
      for(int i = 0; i < x0.rows(); i++) {
        error += Math.pow((x0.get(0, i) - x1.get(0, i)), 2);
      }
      error = Math.sqrt(error);
      System.out.print("Gradient-Descent error: " + error);
      System.out.print(" Gradient-Descent cost: " + simulation.getCost(x1));
      System.out.println();
    }
    return x1;
  }

  /**
  * This method is used to compute the gradient of the cost function
  * @param x The current parameter Matrix
  * @param simulation The simulation of type ProbeSimulator to get the cost function from
  * @return The gradient of the cost function
  */
  public static Matrix getGradient(Matrix x, CostFunction simulation) {
    double[][] resultingValues = new double[x.rows()][x.columns()];
    for(int i = 0; i < x.rows(); i++) {
      for(int j = 0; j < x.columns(); j++) {
        //partial derivative w.r.t the j'th element
        Matrix deltaMatrix = new Matrix(x.rows(), x.columns());
        deltaMatrix.set(delta, 0, j);
        Matrix x1 = x.add(deltaMatrix);
        Matrix x2 = x.sub(deltaMatrix);
        double w1 = simulation.getCost(x1);
        double w2 = simulation.getCost(x2);
        resultingValues[i][j] = (simulation.getCost(x1) - simulation.getCost(x2))/(2 * delta);
      }
    }
    return new Matrix(resultingValues);
  }




  //Returns a matrix of dimension 1xn with random entries to account for collisions
  public static Matrix getRandomJacobian(int n) {
    double[][] values = new double[1][n];
    for(int i = 0; i < n; i++) {
      values[0][i] = Math.random() * 10000 - 100000;
    }
    return new Matrix(values);
  }
}
