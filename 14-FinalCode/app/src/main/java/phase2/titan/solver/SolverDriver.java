package titan.solver;

import titan.*;

public class SolverDriver implements ODESolverInterface {
  ODESolverInterface solver;
  public SolverDriver(String solverName) {
    //System.out.println(solverName);
    if(solverName.toLowerCase().equals("euler")) {
      this.solver = new EulerSolver();
    }
    else if(solverName.toLowerCase().equals("verlet")){
      this.solver = new VerletSolver();
    }
    else if(solverName.toLowerCase().equals("rk")){
      this.solver = new RungeKuttaSolver();
    }
  }
  /*
   * Solve the differential equation by taking multiple steps.
   *
   * @param   f       the function defining the differential equation dy/dt=f(t,y)
   * @param   y0      the starting state
   * @param   ts      the times at which the states should be output, with ts[0] being the initial time
   * @return  an array of size ts.length with all intermediate states along the path
   */
  public StateInterface[] solve(ODEFunctionInterface f, StateInterface y0, double[] ts) {
    return this.solver.solve(f, y0, ts);
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
    return this.solver.solve(f, y0, tf, h);
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
    return this.solver.step(f, t, y, h);
  }

}
