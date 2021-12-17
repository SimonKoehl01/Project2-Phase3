package titan.solver;

import titan.*;
import titan.math.*;
import java.util.*;
public class RungeKuttaSolver implements ODESolverInterface{
  /*
   * Solve the differential equation by taking multiple steps.
   *
   * @param   f       the function defining the differential equation dy/dt=f(t,y)
   * @param   y0      the starting state
   * @param   ts      the times at which the states should be output, with ts[0] being the initial time
   * @return  an array of size ts.length with all intermediate states along the path
   */
   public StateInterface[] solve(ODEFunctionInterface f, StateInterface y0, double[] ts) {
     State[] result = new State[ts.length];
     double stepSize = ts[0];
     result[0] = (State) y0;

     for(int i = 1; i < ts.length; i++) {
       stepSize = ts[i] - ts[i-1];
       result[i] = (State) step(f, ts[i], result[i-1], stepSize);
     }
     return result;
   }

  /*
   * Solve the differential equation by taking multiple steps of equal size, starting at time 0.
   * The final step may have a smaller size, if the step-size does not exactly divide the solution time range
   *
   * @param   f       the function defining the differential equation dy/dt=f(t,y)
   * @param   y0      the starting state
   * @param   tf      the final time
   * @param   h       the size of step to be taken
   * @return  an array of size round(tf/h)+1 including all intermediate states along the path
   */
   public StateInterface[] solve(ODEFunctionInterface f, StateInterface y0, double tf, double h) {
     State[] result = new State[(int) (Math.ceil(tf/h) + 1)];
     result[0] = (State) y0;
     int i = 1;
     double t = h;
     while(t <= tf) {
       result[i] = (State) step(f, t, result[i-1], h);
       t += h;
       i++;
     }
     if(t - h < tf) {
       result[i] = (State) step(f, t, result[i-1], tf-(t-h));
     }
     return result;
   }

  /*
   * Update rule for one step.
   *
   * @param   f   the function defining the differential equation dy/dt=f(t,y)
   * @param   t   the time
   * @param   y   the state
   * @param   h   the step size
   * @return  the new state after taking one step
   */
  public StateInterface step(ODEFunctionInterface f, double t, StateInterface y, double h) {

    // State previousState = (State) y;
    // Rate change = (Rate) f.call(t, y);
    //
    // //Accelerations to velocities
    // Rate k1 = change.scale(h);
    // Rate k2 = ((Rate) f.call(t + h/2, previousState.addMul(0.5, k1))).scale(h);
    // Rate k3 = ((Rate) f.call(t + h/2, previousState.addMul(0.5, k2))).scale(h);
    // Rate k4 = ((Rate) f.call(t + h, previousState.addMul(1.0, k3))).scale(h);
    // Rate rateSum = (k1.scale(1/2.0).add(k2).add(k3).add(k4.scale(1/2.0)));
    // State result = (State) previousState.updateRK(rateSum.scale(1/3.0), h);
    // //Velocities to positions
    // return result;

    //New Runge Kutta try based on this video : https://www.youtube.com/watch?v=TjZgQa2kec0
    State previousState = (State) y;
    Rate change = (Rate) f.call(t, y);
    Rate k1V = change;
    Rate k1X = new Rate(previousState.velocities);
    State tmp = previousState.update(k1X.scale(h/2.0), k1V.scale(h/2.0));
    Rate k2V = (Rate) f.call(t + h/2, tmp);
    Rate k2X = new Rate(previousState.addAccelerations(h/2.0, k1V).velocities);
    tmp = previousState.update(k2X.scale(h/2.0), k2V.scale(h/2.0));
    Rate k3V = (Rate) f.call(t + h/2, tmp);
    Rate k3X = new Rate(previousState.addAccelerations(h/2.0, k2V).velocities);
    tmp = previousState.update(k3X.scale(h), k3V.scale(h));
    Rate k4V = (Rate) f.call(t + h/2, tmp);
    Rate k4X = new Rate(previousState.addAccelerations(h, k3V).velocities);
    Rate accelerationSum = k1V.add(k2V.scale(2)).add(k3V.scale(2)).add(k4V);
    Rate velocitySum = k1X.add(k2X.scale(2)).add(k3X.scale(2)).add(k4X);
    State result = previousState.update(velocitySum.scale(h/6.0), accelerationSum.scale(h/6.0));
    return result;
  }
}
