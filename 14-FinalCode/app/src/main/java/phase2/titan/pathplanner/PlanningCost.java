package titan.pathplanner;
import titan.math.*;
import titan.probe.*;
public class PlanningCost implements CostFunction{
  ProbeSimulator simulation;

  public PlanningCost(ProbeSimulator simulation) {
    this.simulation = simulation;
  }

  @Override
  public double getCost(Matrix params) {

    if(700000 > this.simulation.closest) {
      return 0;
    }
    double cost = this.simulation.getCost(params);

    return cost;
  }
}
