package titan.controllers;
import titan.math.*;
public class FlightController {
  private PIDController[] controllers;
  public FlightController(double[] gains, int numberOfPIDControllers, double timeStep) {

    this.controllers = new PIDController[numberOfPIDControllers];
    for(int k = 0; k < numberOfPIDControllers; k++) {
      this.controllers[k] = new PIDController(gains[k*3], gains[(k*3)+1], gains[(k*3)+2], timeStep);
    }
  }

  public double[] getCorrectionData(double[] targetData, double[] actualData) {
    double results[] = new double[this.controllers.length];
    for(int i = 0; i < this.controllers.length; i++) {
      results[i] = this.controllers[i].compute(targetData[i], actualData[i]);
    }

    return results;
  }
}
