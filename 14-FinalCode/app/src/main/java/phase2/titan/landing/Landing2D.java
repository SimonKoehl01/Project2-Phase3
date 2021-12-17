package titan.landing;
import java.io.FileInputStream;
import java.io.InputStream;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.util.Duration;
import titan.*;
import titan.math.*;
import titan.solver.*;
public class Landing2D extends Group{

  private final String surfaceImg = "assets/titanSurface.jpg";
  private final String spaceImg = "assets/space.jpg";
  private SolverDriver solver = new SolverDriver("euler");
  private ODEFunctionInterface f;
  private Lander lander;
  private State state;
  private Group titan;
  public static final double STEP_SIZE = 1;
  public Landing2D(Vector3d pos) {
    super();
    this.lander = new Lander(pos.getX(), pos.getY(), pos.getZ());
    this.f = new LandingODE(this.lander);
    this.state = new State(1);
    this.state.positions[0] = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    this.state.velocities[0] = new Vector3d(0, 0, 0);
    initGraphics();
  }
  public void initGraphics() {
    this.titan = new Group();
    //Space background graphics
    Rectangle space = new Rectangle(0, 0, 700, 500);
    Image imag = new Image(spaceImg);
    space.setFill(new ImagePattern(imag));
    getChildren().add(space);
    //Graphics for titan's surface
    Rectangle surface = new Rectangle(-700, 250, 1400, 250);
    Image img = new Image(surfaceImg);
    surface.setFill(new ImagePattern(img));
    this.titan.getChildren().add(surface);
    getChildren().add(this.titan);
    //Graphics for the lander
    this.lander.initGraphics();
    getChildren().add(this.lander);
  }
  public void update() {
    State currentState = (State) this.solver.step(this.f, 0, this.state, STEP_SIZE);
    this.lander.update(currentState);
    currentState.positions[0] = new Vector3d(this.lander.getXCoord(), this.lander.getYCoord(), this.lander.getTheta());
    currentState.velocities[0] = new Vector3d(this.lander.getXVelocity(), this.lander.getYVelocity(), this.lander.getThetaDot());
    this.state = currentState.clone();
  }

  public void show() {
    this.lander.show();
    this.titan.translateXProperty().set(this.lander.getXCoord());
    this.titan.translateYProperty().set(this.lander.getYCoord());
  }


  public void init(Vector3d initialPosition) {
    this.lander.setXCoord(initialPosition.getX());
    this.lander.setYCoord(initialPosition.getY());
    this.lander.setTheta(initialPosition.getZ());
    this.state.positions[0] = new Vector3d(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ());
    this.state.velocities[0] = new Vector3d(0, 0, 0);
  }
}
