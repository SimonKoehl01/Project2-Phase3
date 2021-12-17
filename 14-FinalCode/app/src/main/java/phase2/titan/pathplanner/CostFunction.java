package titan.pathplanner;
import titan.math.*;

public interface CostFunction {
  public double getCost(Matrix params);
}
