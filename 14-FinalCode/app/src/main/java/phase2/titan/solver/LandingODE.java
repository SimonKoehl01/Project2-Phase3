package titan.solver;
import titan.windsimulation.*;
import titan.landing.*;
import titan.math.*;
import titan.*;
public class LandingODE implements ODEFunctionInterface {
  Lander lander;
  double g = 1.352;
  boolean applyWind = true;
  public LandingODE(Lander lander) {
    this.lander = lander;
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
  @Override
  public RateInterface call(double t, StateInterface y) {
    double accelerationX = (this.lander.getMainThrust() / this.lander.getMass()) * Math.sin(this.lander.getTheta());
    double accelerationY = (this.lander.getMainThrust() / this.lander.getMass()) * Math.cos(this.lander.getTheta()) - g;
    double accelerationTheta = this.lander.getTorqueThrust() / this.lander.getMass();
    if(applyWind) {
      accelerationX += (StochasticWindModel.getWind(this.lander.getYCoord()) / this.lander.getMass());
    }
    Vector3d[] data = new Vector3d[1];
    data[0] = new Vector3d(accelerationX, accelerationY, accelerationTheta);
    Rate result = new Rate(data);
    return result;
  }
}
