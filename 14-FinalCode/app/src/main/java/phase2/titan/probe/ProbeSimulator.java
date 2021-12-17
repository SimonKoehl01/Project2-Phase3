package titan.probe;

import titan.math.*;
import titan.*;
import titan.solarsystem.*;

import titan.solver.*;
import javafx.scene.*;
import titan.pathplanner.*;
import titan.bodies.*;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.*;
import javafx.scene.shape.*;
import java.util.*;

import java.util.concurrent.TimeUnit;


public class ProbeSimulator implements ProbeSimulatorInterface {
  public Vector3d[] trajectory;
  State startingState;
  SolverDriver solver;
  Position f;
  public SolarSystem solarSystem;
  public Rocket rocket;
  int size;
  public State[] states;
  int iterator = -1;

  public Group curveGroup;
  public double closest = Double.MAX_VALUE;
  public double atTime;
  public boolean running = false;
  final double TIMESTEP_NUMBER = 3.154e7;
  public final double STEP_SIZE = 300.0;
  private final double scalingFactor = 1e5;
  private final double deltaV = 60000.0;
  private Vector3d prevThrust = new Vector3d(0, 0, 0);
  private Vector3d[] desiredTrajectory;
  private Group actualRocketTrajectoryCurve = new Group();
  public int closestI;
  public Vector3d closestPos;
  private double previousCost = Double.MAX_VALUE;
  /**
  * This class constructor initializes the variables used in the rest of the class.
  * @param system The full solar system that has been created
  */
  public ProbeSimulator() {
    this.solver = new SolverDriver("euler");
    this.solarSystem = new SolarSystem(11, scalingFactor);
    this.rocket = new Rocket(scalingFactor, STEP_SIZE);
    this.size = this.solarSystem.size + 1;
    this.startingState = new State(this.size);
    init();
    this.curveGroup = new Group();
  }

  /**
  * This method is used to store the masses in order to draw the position at which the rocket starts.
  */
  private void init() {

    this.solarSystem.getChildren().add(this.rocket);
    //Initialize the masses
    double[] masses = new double[this.solarSystem.size + 1];
    double[] tmp = this.solarSystem.getMasses();
    for(int i = 0; i < masses.length-1; i++) {
      masses[i] = tmp[i];
    }
    masses[masses.length-1] = this.rocket.mass;
    this.f = new Position(masses, this.rocket);
    //Get the initial state
    getState();
    this.states = new State[(int) (Math.ceil(TIMESTEP_NUMBER / STEP_SIZE) + 1)];
    this.trajectory = new Vector3d[(int) (Math.ceil(TIMESTEP_NUMBER / STEP_SIZE) + 1)];
    for(int i = 0; i < this.states.length; i++) {
      this.states[i] = new State(this.size);
      this.trajectory[i] = new Vector3d(0, 0, 0);
    }
  }

  public void getState() {
    //Get the current state
    for(int i = 0; i < this.size-1; i++) {
      this.startingState.velocities[i] = solarSystem.planets[i].velocity;
      this.startingState.positions[i] = solarSystem.planets[i].position;
    }
  }

  /*
  * Simulate the solar system, including a probe fired from Earth at 00:00h on 1 April 2020.
  *
  * @param   p0      the starting position of the probe, relative to the earth's position.
  * @param   v0      the starting velocity of the probe, relative to the earth's velocity.
  * @param   ts      the times at which the states should be output, with ts[0] being the initial time.
  * @return  an array of size ts.length giving the position of the probe at each time stated,
  *          taken relative to the Solar System barycentre.
  */
  @Override
  public Vector3dInterface[] trajectory(Vector3dInterface p0, Vector3dInterface v0, double[] ts) {
    Vector3d startingPosition = (Vector3d) p0;
    Vector3d startingVelocity = (Vector3d) v0;
    startingPosition = (Vector3d) this.startingState.positions[1].add(startingPosition);
    startingVelocity = (Vector3d) this.startingState.velocities[1].add(startingVelocity);
    this.startingState.velocities[this.size-1] = startingVelocity;
    this.startingState.positions[this.size-1] = startingPosition;
    this.states = (State[]) solver.solve(f, this.startingState, ts);
    Vector3d[] result = new Vector3d[this.states.length];
    for(int i = 0; i < this.states.length; i++) {
      result[i] = this.states[i].positions[this.size-1];
    }
    return result;
  }

  /*
  * Simulate the solar system with steps of an equal size.
  * The final step may have a smaller size, if the step-size does not exactly divide the solution time range.
  *
  * @param   tf      the final time of the evolution.
  * @param   h       the size of step to be taken
  * @return  an array of size round(tf/h)+1 giving the position of the probe at each time stated,
  *          taken relative to the Solar System barycentre
  */
  @Override
  public Vector3dInterface[] trajectory(Vector3dInterface p0, Vector3dInterface v0, double tf, double h) {
    Vector3d startingPosition = (Vector3d) p0;
    Vector3d startingVelocity = (Vector3d) v0;
    startingPosition = (Vector3d) this.startingState.positions[1].add(startingPosition);
    startingVelocity = (Vector3d) this.startingState.velocities[1].add(startingVelocity);
    this.startingState.velocities[this.size-1] = startingVelocity;
    this.startingState.positions[this.size-1] = startingPosition;
    this.states = (State[]) solver.solve(f, this.startingState, tf, h);
    Vector3d[] result = new Vector3d[this.states.length];
    for(int i = 0; i < this.states.length; i++) {
      result[i] = this.states[i].positions[this.size-1];
    }
    return result;
  }
  //This method is used to actually draw the movement of the solar system and space probe.
  public void update() {
    if(this.iterator >= this.states.length) {
      System.out.println("The simulation finished, exiting now...");
      running = false;
      return;
    }
    this.solarSystem.show(this.states[this.iterator]);
    this.rocket.show(this.trajectory[this.iterator], this.states[this.iterator].positions[1]);
    this.iterator++;
  }

  public boolean computeSimulation(boolean run) {
    double prevProgress = -1;
    for(int i = 0; i < this.states.length-1; i++) {
      this.solarSystem.update(this.states[i]);
      if(i >= this.closestI) {
        this.rocket.update(this.states[i].positions[this.size-1], this.states[i].velocities[this.size-1], i, this.states[i].positions[8], this.states[i].velocities[8], this);
      }
      else {
        this.rocket.update(this.states[i].positions[this.size-1], this.states[i].velocities[this.size-1], i, this.states[i].positions[1], this.states[i].velocities[1], this);
      }
      this.states[i+1] = (State) this.solver.step(this.f, i * STEP_SIZE, this.states[i], STEP_SIZE);
      this.trajectory[i+1] = this.states[i+1].positions[this.size-1];
      if(run) {
        int progress = (int) (i * 100 / (double) this.states.length);
        if(progress % 10 == 0) {
          if(progress != prevProgress) {
            System.out.print("..." + progress + "%");
          }
        }
        prevProgress = progress;
      }
    }
    if(run) {
      this.iterator = 0;
      for(int i = 3; i >= 1; i--) {
        System.out.println(i + "!");
        try{
          TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e) {
          e.printStackTrace();
        }
      }
      System.out.println("Launch!");
      System.out.println("NOTE: please keep your hands and feet inside the vehicle at all times during the ride, etc...");
      System.out.println("The closest approach for the planned trajectory will be " + this.closest + "m at T+ " + this.atTime + ".");
      //Graphics
      drawTrajectories();
    }
    return false;
  }
  //This method is used to draw the trajectories
  private void drawTrajectories() {
    this.curveGroup.getChildren().clear();
    Vector3d[][] trajectories = new Vector3d[this.states[0].positions.length][this.states.length];
    for(int i = 0; i < this.states[0].positions.length; i++) {
      for(int j = 0; j < this.states.length; j++) {
        trajectories[i][j] = this.states[j].positions[i];
      }
    }
    //make trajectory divisible by the pointFactor
    //The pointFactor is chosen such that there is always a total of 700 points per trajectory.
    //This is done to decrease the computational workload when rendering the solar system.
    int pointFactor = (int) (TIMESTEP_NUMBER / STEP_SIZE) / 300;
    Group[] curves = new Group[this.size];
    int length = (int) (this.trajectory.length / pointFactor);
    length *= pointFactor;
    for(int k = 0; k < trajectories.length; k++) {
      curves[k] = new Group();
      for(int i = 0; i < length-1; i += pointFactor) {
        Sphere tmp = new Sphere(400);
        Vector3d point1 = trajectories[k][i];
        Vector3d point2 = trajectories[k][i+(int)pointFactor];
        point1 = (Vector3d) point1.mul(1/this.solarSystem.scalingFactor);
        point2 = (Vector3d) point2.mul(1/this.solarSystem.scalingFactor);
        Vector3d direction = (Vector3d) point2.sub(point1);
        Vector3d point3 = (Vector3d) point1.addMul(1/2, direction);
        tmp.translateXProperty().set(point3.getX());
        tmp.translateYProperty().set(point3.getY());
        tmp.translateZProperty().set(point3.getZ());
        curves[k].getChildren().add(tmp);
      }
      this.curveGroup.getChildren().add(curves[k]);
    }
    if(this.solarSystem.getChildren().contains(this.curveGroup)) {
      this.solarSystem.getChildren().remove(this.curveGroup);
    }
    this.solarSystem.getChildren().add(this.curveGroup);
  }
  //This method tries to find an optimal trajectory using newton raphson
  public void getBestTrajectory() {
    Matrix result = NewtonRaphsonSecondVersion.run(new PlanningCost(this), 4);
    Vector3d pos = sphericalToCartesianCoordinates(result.get(0, 0), result.get(0, 1), this.solarSystem.planets[1].r);
    Vector3d vel = sphericalToCartesianCoordinates(result.get(0, 2), result.get(0, 3), deltaV);
    System.out.println(this.closestI);
    // Best values using euler solver with stepsize of 300 (for testing purposes to not compute the same values all over again)
    // Vector3d pos = new Vector3d(-64789.0360043644,96.94389461413473,6370670.559008329);
    // Vector3d vel = new Vector3d(35684.784151656546,-48169.37703485658,2511.433081631637);
    // this.closestI = 64753;
    // this.closest = 2665050.7307701064;
    launch(pos, vel);
  }
  //This method is used to calculate the cost of a trajectory according to its
  //closest approach to titan and the time of this closest approach.
  public double getCost(Matrix params) {
    //Convert the latitudes and longitudes from spherical to cartesian coordinates
    Vector3d pos = sphericalToCartesianCoordinates(params.get(0, 0), params.get(0, 1), this.solarSystem.planets[1].r);
    Vector3d vel = sphericalToCartesianCoordinates(params.get(0, 2), params.get(0, 3), deltaV);
    this.trajectory = (Vector3d[]) trajectory(pos, vel, TIMESTEP_NUMBER, STEP_SIZE);
    double closestApproach = Double.MAX_VALUE;
    double atTime = 0;
    for(int i = 0; i < this.trajectory.length; i++) {
      double distance = this.trajectory[i].dist(this.states[i].positions[8]);
      if(distance < closestApproach) {
        closestApproach = distance;
        atTime = i * STEP_SIZE;
      }
      if(collisionDetected(i)) {
        return 1.0;
      }
    }
    double cost = Math.abs((closestApproach) / this.startingState.positions[8].dist(this.startingState.positions[1]));
    if(closestApproach - 2575.5e3 < this.closest) {
      this.closest = closestApproach - 2575.5e3;
      this.closestI = (int) (atTime / STEP_SIZE);
      this.closestPos = this.trajectory[this.closestI];
      this.atTime = atTime;
    }
    return cost;
  }
  private Vector3d sphericalToCartesianCoordinates(double lat, double lon, double scalar) {
    double x = scalar * Math.sin(lat) * Math.cos(lon);
    double y = scalar * Math.sin(lat) * Math.sin(lon);
    double z = scalar * Math.cos(lat);
    return new Vector3d(x, y, z);
  }
  private double[] cartesianToSphericalCoordinates(Vector3d pos) {
    double lon = Math.atan(pos.getY() / pos.getX());
    double lat = Math.atan(Math.sqrt(pos.getX()*pos.getX() + pos.getY() * pos.getY()) / pos.getZ());
    double[] result = {lat, lon};
    return result;
  }
  //This method is used to launch the probe with the given initial position and velocity vectors.
  private void launch(Vector3d pos, Vector3d vel) {
    System.out.println("Found the best possible trajectory!");
    this.rocket.reset(pos, vel, this.closest, this.solarSystem.planets[8].mass, this.closestI, this.startingState.positions[1], this.startingState.velocities[1], this.deltaV);
    this.states[0].positions[11] = this.rocket.getAbsolutePosition();
    this.states[0].velocities[11] = this.rocket.getAbsoluteVelocity();
    this.trajectory[0] = this.rocket.getAbsolutePosition();
    this.rocket.debug = false;
    System.out.println("Launching at " + pos + " as the initial position relative to earth with an initial heading vector of " + vel + ".");
    running = true;
  }
  public boolean collisionDetected(int i) {
      if(i < this.trajectory.length-1) {
        Vector3d rocketCurrentPosition = this.trajectory[i];
        Vector3d rocketNextPosition = this.trajectory[i+1];
        Vector3d direction = (Vector3d) rocketNextPosition.sub(rocketCurrentPosition);
        Vector3d earthPos = this.states[i].positions[1];
        double radius = this.solarSystem.planets[1].r-100;
        double step = 0.001;
        double scale = 0;
        while(scale <= 1) {
          Vector3d pointBetweenPositions = (Vector3d) rocketCurrentPosition.add(direction.mul(scale));
          if(pointBetweenPositions.dist(earthPos) < radius) {
            return true;
          }
          scale += step;
        }
      }
    return false;
  }

  /**
  For testing purposes, the creation of graphics must be seperated from the rest of the program, because
  otherwise, javafx will throw a RuntimeException because no screens were initialized to draw on.
  */
  public void initGraphics() {
    this.solarSystem.initGraphics();
    this.rocket.initGraphics();
  }
}
