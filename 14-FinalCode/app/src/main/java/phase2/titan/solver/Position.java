package titan.solver;

import titan.*;
import titan.math.*;
import java.util.*;

import titan.probe.*;

public class Position implements ODEFunctionInterface {
  double G;
  double[] masses;
  Rocket rocket;

  public Position(double[] masses, Rocket r) {
    this.masses = masses;
    this.G = 6.67430e-11;
    this.rocket = r;
  }
  /*
  * This is an interface for the function f that represents the
  * differential equation dy/dt = f(t,y).
  * You need to implement this function to represent to the laws of physics.
  *
  * For example, consider the differential equation
  *   dy[0]/dt = y[1];  dy[1]/dt=cos(t)-sin(y[0])
  * Then this function would be
  *   f(t,y) = (y[1],cos(t)-sin(y[0])).
  *
  * @param   t   the time at which to evaluate the function
  * @param   y   the state at which to evaluate the function
  * @return  The average rate-of-change over the time-step. Has dimensions of [state]/[time].
  */
  public RateInterface call(double t, StateInterface y) {
    State yState = (State) y;
    Rate result = new Rate(yState.size);
    for(int i = 0; i < yState.size; i++) {
      result.changes[i] = new Vector3d(0, 0, 0);
      for(int j = 0; j < yState.size; j++) {
        if(i != j) {
          Vector3d direction = (Vector3d) yState.positions[j].sub(yState.positions[i]);
          double norm = yState.positions[i].dist(yState.positions[j]);
          double normCubed = norm*norm*norm;
          Vector3d tmp = (Vector3d) direction.mul(this.masses[j]/normCubed);
          result.changes[i] = (Vector3d) result.changes[i].add(tmp);
        }
      }
      result.changes[i] = (Vector3d) result.changes[i].mul(this.G);
    }
    Vector3d rocketAcceleration = result.changes[result.changes.length-1];
    result.changes[result.changes.length-1] = (Vector3d) rocketAcceleration.add(this.rocket.getThrust().mul(1/this.rocket.getMass()));
    return result;
  }
}
