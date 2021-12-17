package titan.controllers;

import titan.math.*;
public class PIDController {
  private double kp, ki, kd, fmax, stepSize, previousError, integralError;

  public PIDController(double kp, double ki, double kd, double stepSize) {
    this.kp = kp;
    this.ki = ki;
    this.kd = kd;
    this.fmax = fmax;
    this.stepSize = stepSize;
  }

  public double compute(double desired, double actual) {
    double error = desired - actual;
    this.integralError += this.stepSize * error;
    double derivativeError = (error - this.previousError) / this.stepSize;
    this.previousError = error;
    //Return the PID output
    double correction = this.kp * error + this.integralError * this.ki + this.kd * derivativeError;

    return correction;
  }
}
