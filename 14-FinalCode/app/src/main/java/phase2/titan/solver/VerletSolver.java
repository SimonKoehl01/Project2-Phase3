package titan.solver;
import titan.*;
import titan.math.*;
public class VerletSolver implements ODESolverInterface {


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
    State state = (State) y;
    Rate accelerations = (Rate) f.call(t, y);
    Rate velocities = new Rate(state.velocities);
    Rate dx = velocities.scale(h).add(accelerations.scale((h*h)/2.0));
    State tmp = state.addToPositions(dx);
    Rate accelerations2 = (Rate) f.call(t + h, tmp);
    Rate dv = (accelerations.add(accelerations2)).scale(h / 2.0);
    return tmp.addAccelerations(1.0, dv);


    // //Calculate nextV
    // State state = (State) y;
    // Rate change = (Rate) f.call(t, y);
    // Rate vel = new Rate(state.velocities);
    // Rate nextHalfV = vel.add(change.scale(h / 2.0));
    // //Calculate nextX
    // Rate pos = new Rate(state.positions);
    // Rate nextX = pos.add(nextHalfV.scale(h));
    // State result = new State(state.size);
    // //Calculate nextA
    // for(int i = 0; i < state.size; i++) {
    //   result.positions[i] = nextX.changes[i];
    // }
    // Rate nextA = (Rate) f.call(t + h, result);
    // Rate nextV = nextHalfV.add(nextA.scale(h / 2.0));
    // for(int i = 0; i < state.size; i++) {
    //   result.velocities[i] = nextV.changes[i];
    // }
    // return result;
  }
}
