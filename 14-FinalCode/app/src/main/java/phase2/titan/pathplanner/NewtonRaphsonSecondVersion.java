package titan.pathplanner;
import titan.math.Matrix;
import titan.probe.*;
public class NewtonRaphsonSecondVersion {
  static double delta = 1e-10;
  static final double ACCURACY = 1e-14;
  public static Matrix run(CostFunction simulation, int numberOfParams) {
    Matrix x0 = new Matrix(1, numberOfParams);
    double w0 = simulation.getCost(x0);
    Matrix jacobianAtx0 = computeJacobian(x0, simulation);
    Matrix tmp = jacobianAtx0.mul(w0);
    Matrix x1 = x0.sub(tmp);
    double w1 = simulation.getCost(x1);
    double error = 0;
    for(int i = 0; i < x0.columns(); i++) {
      error += Math.pow((x0.get(0, i) - x1.get(0, i)), 2);
    }
    error = Math.sqrt(error);
    System.out.println("Newton-Raphson error: " + error);
    while(error > ACCURACY) {
      x0 = x1;
      w0 = w1;
      jacobianAtx0 = computeJacobian(x0, simulation);
      tmp = jacobianAtx0.mul(w0);
      x1 = x0.sub(tmp);
      w1 = simulation.getCost(x1);
      if(w1 == 0) {
        System.out.println("Found a sufficiently close trajectory!");
        break;
      }
      error = 0;
      for(int i = 0; i < x0.rows(); i++) {
        error += Math.pow((x0.get(0, i) - x1.get(0, i)), 2);
      }
      error = Math.sqrt(error);
      System.out.println("Newton-Raphson error: " + error);
    }
    return x1;
  }
  /**
  * This method is used to compute the Jacobian matrix of partial derivatives
  * @param x The current parameter Matrix
  * @param simulation The simulation of type ProbeSimulator to get the cost function from
  */
  public static Matrix computeJacobian(Matrix x, CostFunction simulation) {
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
        if(w1 == 1 && w2 == 1) {
          System.out.println("Collision detected");
          return getRandomJacobian(x.columns());
        }
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
