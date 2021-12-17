package titan.landing;
import titan.math.*;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Rotate;
import titan.solver.*;
import titan.*;
import titan.controllers.*;
public class Lander extends ImageView {
  private double torqueThrust;
  private double mainThrust;
  private double x;
  private double y;
  private double xVel;
  private double yVel;
  private double theta;
  private double theta_dot;
  private double drawingX;
  private double drawingY;
  private final double scaleFactor = -1;
  private Image image = new Image("assets/RRR.png");
  private final boolean debug = true;
  private double mass = 777;
  private FlightController innerLoopController;
  private PIDController outerLoopControllerV;
  private PIDController outerLoopControllerU;
  private boolean controllersActive = true;
  public Lander(double x, double y, double theta) {
    super();
    setImage(image);
    this.x = x;
    this.y = y;
    this.theta = theta;
    this.torqueThrust = 0;
    this.mainThrust = 0;
    //Initializing flight computer
    this.outerLoopControllerV = new PIDController(0.0007779, 0, 0.07, Landing2D.STEP_SIZE);
    this.outerLoopControllerU = new PIDController(0.01, 0, 0, Landing2D.STEP_SIZE);
    //0.0025, 0.000019, 0.0235
    double[] innerControllerGains = {1.4, 0, 77, 1561.34, 0, 0};
    this.innerLoopController = new FlightController(innerControllerGains, 2, Landing2D.STEP_SIZE);
  }
  public void update(State currentState) {
    this.x = currentState.positions[0].getX();
    this.y = currentState.positions[0].getY();
    this.xVel = currentState.velocities[0].getX();
    this.yVel = currentState.velocities[0].getY();
    this.theta = currentState.positions[0].getZ();
    this.theta_dot = currentState.velocities[0].getZ();
    if(this.y <= 0) {
      this.y = 0;
      System.out.println("Lander final x position: " + this.x);
      System.out.println("Lander final x velocity: " + this.xVel);
      System.out.println("Lander final y position: " + this.y);
      System.out.println("Lander final y velocity: " + this.yVel);
      System.out.println("Lander final theta: " + this.theta);
      System.out.println("Lander final angular velocity: " + this.theta_dot);
      this.xVel = 0;
      this.yVel = 0;
      this.theta_dot = 0;
      this.controllersActive = false;
      System.exit(0);
    }
    //Controller logic
    double desiredTheta = 0;
    double desiredYVel = 0;
    if(this.controllersActive) {
      desiredTheta = this.outerLoopControllerV.compute(0, this.x);
      if(Math.abs(desiredTheta) > Math.PI / 4) {
        desiredTheta = (desiredTheta / (Math.abs(desiredTheta))) * Math.PI/4;
      }
      desiredYVel = this.outerLoopControllerU.compute(57.7, this.y);
      double[] targetData = {desiredTheta, desiredYVel};
      double[] actualData = {this.theta, this.yVel};
      double[] results = this.innerLoopController.getCorrectionData(targetData, actualData);
      if(Math.abs(results[0]) > 25) {
        results[0] = (results[0] / (Math.abs(results[0]))) * 25;
      }
      if(results[1] < 0) {
        results[1] = 0;
      }
      if(results[1] > 3e7) {
        results[1] = 3e7;
      }
      this.setTorqueThrust(results[0]);
      this.setMainThrust(results[1]);
    }
    else{
      this.setMainThrust(0);
      this.setTorqueThrust(0);
    }
    //Debugging output
    if(debug) {
      System.out.println("Lander x position: " + this.x);
      System.out.println("Lander y position: " + this.y);
      System.out.println("Lander x velocity: " + this.xVel);
      System.out.println("Desired y vel: " + desiredYVel);
      System.out.println("Lander actual y velocity: " + this.yVel);
      System.out.println("Desired theta: " + desiredTheta);
      System.out.println("Lander actual theta: " + this.theta);
      System.out.println("Lander angular velocity: " + this.theta_dot);
      System.out.println("Lander main thrust: " + this.mainThrust);
      System.out.println("Lander side thruster force: " + this.torqueThrust);
    }

  }
  public void initGraphics() {
    setFitHeight(100);
    setFitWidth(100);
  }
  public void setTorqueThrust(double v) {
    this.torqueThrust = v;
  }
  public void setMainThrust(double u) {
    this.mainThrust = u;
  }

  public double getTorqueThrust() {
    return this.torqueThrust;
  }

  public double getMainThrust() {
    return this.mainThrust;
  }

  public double getTheta() {
    return this.theta;
  }

  public double getXCoord() {
    return this.x;
  }

  public double getYCoord() {
    return this.y;
  }

  public double getXVelocity() {
    return this.xVel;
  }

  public double getYVelocity() {
    return this.yVel;
  }

  public double getThetaDot() {
    return this.theta_dot;
  }

  public void setXCoord(double x) {
    this.x = x;
  }

  public void setYCoord(double y) {
    this.y = y;
  }

  public double getMass() {
    return this.mass;
  }

  public void setTheta(double theta) {
    this.theta = theta;
    scale();
    Rotate rotate = new Rotate(Math.toDegrees(-this.theta));
    rotate.setPivotX(this.drawingX + 50);
    rotate.setPivotY(this.drawingY + 50);
    getTransforms().addAll(rotate);
  }

  public void scale() {
    double scaledX = this.x / this.scaleFactor;
    double scaledY = this.y / this.scaleFactor;
    //Setting the drawing coordinates to be at the center of the rectangle
    this.drawingX = (700 / 2.0) - 50;
    this.drawingY = (500 / 2.0) - 50;
  }

  public void show() {
    scale();
    Translate translate = new Translate();
    setX(this.drawingX);
    setY(this.drawingY);
    Rotate rotate = new Rotate(Math.toDegrees(-this.theta_dot));
    rotate.setPivotX(this.drawingX + 50);
    rotate.setPivotY(this.drawingY + 50);
    getTransforms().addAll(rotate);
  }
}
