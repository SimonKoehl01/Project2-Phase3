package titan.probe;
//Project-specific imports
import titan.interfaces.*;
import titan.math.*;
import titan.pathplanner.*;

import titan.*;
//Other imports
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
public class Rocket extends Group implements BodyInterface {
  public double mass;
  private Vector3d position;
  private Vector3d velocity;
  private Vector3d thrust;

  public Vector3d drawPosition;
  public double scalingFactor;
  public double fuel;
  private double dryMass;
  private double desiredVelocity = 12000;

  private double mdot;
  private Vector3d earthPos;
  private Vector3d earthVel;
  public boolean debug = false;
  public double Fmax;
  private double exhaustVelocity = 4000;
  private Vector3d goal;
  private double distance;
  private boolean computed = false;
  public double desiredOrbitalVelocity;
  public double titanMass;
  private double closestI;
  public final double G = 6.67430e-11;
  public double closestDistance;
  private boolean arrived;
  public double timeOfArrival;
  public double deltaT;
  private boolean decelerating;
  private boolean starting;
  private int iteratorDeltaCorrection;
  private Vector3d forceCorrection;
  private double fromTime;
  private final double STEP_SIZE;
  public Rocket(double scale, double timeStep) {
    this.fuel = 7000000;
    this.dryMass = 78000;
    this.mass = this.dryMass + this.fuel;
    this.mdot = 7500;
    this.Fmax = 3e7;
    //position and velocity are relative to earth
    this.position = new Vector3d(0, 0, 0);
    this.velocity = new Vector3d(0, 0, 0);
    this.thrust = new Vector3d(0, 0, 0);
    this.drawPosition = new Vector3d(0, 0, 0);
    this.scalingFactor = scale;
    STEP_SIZE = timeStep;
  }
  public void reset(Vector3d pos, Vector3d vel, double closestDistance, double titanMass, double closestI, Vector3d earthPos, Vector3d earthVel, double desiredStartingVelocity) {
    this.fuel = 4900000;
    this.mass = this.dryMass + this.fuel;
    this.Fmax = 3e7;
    this.setPosition(pos);
    this.setVelocity(vel);
    this.closestDistance = closestDistance;
    this.desiredVelocity = desiredStartingVelocity;
    this.desiredOrbitalVelocity = getOrbitalVelocity();
    this.titanMass = titanMass;
    this.starting = true;
    this.closestI = closestI;
    this.exhaustVelocity = 4000;
    this.mdot = this.Fmax / this.exhaustVelocity;
    this.earthPos = earthPos;
    this.earthVel = earthVel;
  }
  public void scale() {
    this.drawPosition = (Vector3d) this.getAbsolutePosition().mul(1/scalingFactor);
  }
  public void update(Vector3d position, Vector3d velocity, int iterator, Vector3d earthPos, Vector3d earthVel, ProbeSimulator simulator) {
    this.position = toRelativeVector(position, earthPos);
    this.velocity = toRelativeVector(velocity, earthVel);
    this.earthPos = earthPos;
    this.earthVel = earthVel;
    //Acceleration maneuver
    if(iterator == 0) {
      System.out.println("Liftoff!");
      this.fuel -= Tsiolkovsky.fuelNeeded(this.exhaustVelocity, this.desiredVelocity, this.mass);
    }
    //Collision detection
    if(iterator > 0) {
      if(simulator.collisionDetected(iterator-1)) {
        this.position = (Vector3d) this.position.mul(6371e3 / this.position.norm());
        this.velocity = new Vector3d(0, 0, 0);
        if(debug) {
          System.out.println("Distance to earth: " + getAbsolutePosition().dist(this.earthPos));
          System.out.println("Index: " + iterator);
        }
        simulator.states[iterator].positions[11] = getAbsolutePosition();
        simulator.states[iterator].velocities[11] = getAbsoluteVelocity();
        simulator.trajectory[iterator] = getAbsolutePosition();
      }
    }
    //Thrust logic
    if(this.fuel != 0) {
      this.deltaT = getDeltaT();
      if(this.deltaT <= 0) {
        //Start decelerating maneuver
      }
      if(iterator == this.closestI) {
        this.desiredVelocity = getOrbitalVelocity();
        //Entering into titan's atmosphere, please fasten your seatbelts!
        this.fuel -= Tsiolkovsky.fuelNeeded(this.exhaustVelocity, this.desiredVelocity, this.mass);
        this.velocity = (Vector3d) this.velocity.mul(this.desiredVelocity / this.velocity.norm());
        simulator.states[iterator].velocities[11] = getAbsoluteVelocity();
      }
    }
    else {
      this.setThrust(new Vector3d(0, 0, 0));
    }

    //fuel logic (not used currently)
    if(this.getThrust().norm() != 0) {
      if(this.fuel > 0) {
        this.fuel -= this.mdot * STEP_SIZE;
      }
      if(this.fuel < 0) {
        this.fuel = 0;
        this.setThrust(new Vector3d(0, 0, 0));
      }
      this.mass = this.dryMass + this.fuel;
    }
    //Debugging information
    if(debug) {
      System.out.println("Iterator: " + iterator);
      System.out.println("Rocket absolute velocity: " + velocity.norm());
      System.out.println("Rocket relative velocity (to earth): " + this.velocity.norm());
      System.out.println("Rocket thrust: " + this.getThrust());
      System.out.println("Rocket thrust force: " + this.getThrust().norm());
      System.out.println("Rocket mass: " + this.mass);
      System.out.println("Remaining fuel: " + this.fuel);
    }
  }

  public double getOrbitalVelocity() {
    double velocitySquared = this.G * (this.titanMass / this.closestDistance);
    return Math.sqrt(velocitySquared);
  }

  public double getDeltaT() {
    double deltaT = ((this.desiredVelocity - getVelocity().norm()) * this.mass)/ -this.Fmax;
    return deltaT;
  }

  public void show(Vector3d pos, Vector3d earthPos) {
    this.position = (Vector3d) pos.sub(earthPos);
    this.earthPos = earthPos;
    scale();
    translateXProperty().set(this.drawPosition.getX());
    translateYProperty().set(this.drawPosition.getY());
    translateZProperty().set(this.drawPosition.getZ());
  }

  public void setThrust(Vector3d f) {
    this.thrust = f;
  }

  public Vector3d getThrust() {
    return this.thrust;
  }

  //This method is used to get the CelestialBody's drawing position
  @Override
  public Vector3d getDrawingPosition() {
    return this.drawPosition;
  }

  public Vector3d getPosition() {
    return this.position;
  }


  public Vector3d getVelocity() {
    return this.velocity;
  }

  public double getMass() {
    return this.mass;
  }

  public void setPosition(Vector3d pos) {
    this.position = pos;
  }

  public void setVelocity(Vector3d vel) {
    this.velocity = vel;
  }

  public Vector3d getAbsolutePosition() {
    return (Vector3d) this.position.add(this.earthPos);
  }

  public Vector3d getAbsoluteVelocity() {
    return (Vector3d) this.velocity.add(this.earthVel);
  }

  public Vector3d toRelativeVector(Vector3d in, Vector3d rel) {
    return (Vector3d) in.sub(rel);
  }

  public void setDesiredTrajectory(Vector3dInterface[] desiredTrajectoryVar, int closestI) {
    Vector3d[] desiredTrajectory = new Vector3d[desiredTrajectoryVar.length];
    for(int i = 0; i < desiredTrajectory.length; i++) {
      desiredTrajectory[i] = (Vector3d) desiredTrajectoryVar[i];
    }
    PathCorrection.setDesiredTrajectory(desiredTrajectory);
  }

  public void setGoal(Vector3d goal) {
    this.goal = goal;
  }

  public void initGraphics() {
    //size of rocket may be wrong
    Image rocket = new Image("assets/rocket.jpeg");
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseMap(rocket);
    Cylinder rocketCylinder = new Cylinder();
    rocketCylinder.setRadius(10);
    rocketCylinder.setHeight(20);
    rocketCylinder.setMaterial(material);
    getChildren().add(rocketCylinder);
  }
}
