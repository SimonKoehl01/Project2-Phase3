package titan.probe;
/**
 * This class uses Tsiolkovsky's equation. This equation tells us how much fule is required to change
 * speed a certain amount based on the spacecrafts weight and efficiency.
 * ∆v = ∆ve ln(m0/mf)
 * */
public class Tsiolkovsky {
    /**
     *
     * @param ve The effective exhaust velocity
     * @param m0 The initial mass (rocket and propellants)
     * @param mf The final mass (rocket without propellants)
     * @return
     */
   public static double deltav(double ve, double m0, double mf){
       return ve * Math.log(m0/mf);
   }
   /**
    *
    * @param ve The effective exhaust velocity
    * @param v delta v - change in velocity
    * @param mass The mass of the rocket
    * @return The amount of fuel needed
    */
   public static double fuelNeeded(double ve, double v, double mass){
        return mass * (Math.exp(v/ve)-1);
   }
}
