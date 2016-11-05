package primary;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * @author Ken
 *
 */
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 50;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	private static int WALL_DISTANCE = 40; //35
	private static int NOISE_MARGIN = 10; //10
	
	// ultrasonic sensor' s filter
	private float filterControl;
	private float lastValue;
	
	
	public USLocalizer(Odometer odo, SampleProvider usSensor, float[] usData, LocalizationType locType, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		// initial values of the filter
		filterControl = 0;
		lastValue = 60;
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		double theta;
		
		
		Navigation navigator = new Navigation(this.odo);
		
		// set the motors' speed
		leftMotor.setSpeed((int) ROTATION_SPEED);
		rightMotor.setSpeed((int) ROTATION_SPEED);
		
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			// rotate the robot until it sees no wall
			while(getFilteredData() < WALL_DISTANCE + NOISE_MARGIN){
				// turn clockwise
				leftMotor.forward();
				rightMotor.backward();
			}
			

			// keep rotating until the robot sees a wall, then latch the angle			
			while(getFilteredData() > WALL_DISTANCE - NOISE_MARGIN){
				// turn clockwise
				leftMotor.forward();
				rightMotor.backward();
			}
			Sound.beep();
			angleA = odo.getAng();

				
			// switch direction and wait until it sees no wall
			while(getFilteredData() < WALL_DISTANCE + NOISE_MARGIN){
				// turn counterclockwise
				leftMotor.backward();
				rightMotor.forward();
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > WALL_DISTANCE - NOISE_MARGIN){
				// turn counterclockwise
				leftMotor.backward();
				rightMotor.forward();
			}
			Sound.beep();
			angleB = odo.getAng();
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// calculate theta
			theta = computeAngle(angleA, angleB);
			
			// turn to the 0-axis
			navigator.turnTo(theta, true);
			
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			while(getFilteredData() > WALL_DISTANCE - NOISE_MARGIN ){
				// turn clockwise
				leftMotor.forward();
				rightMotor.backward();
			}
			
			while(getFilteredData() < WALL_DISTANCE){
				// turn counterclockwise
				leftMotor.backward();
				rightMotor.forward();
			}
			
			Sound.beep();
			angleA = odo.getAng();
			
			while(getFilteredData() < WALL_DISTANCE + NOISE_MARGIN){
				// turn clockwise
				leftMotor.forward();
				rightMotor.backward();
			}
			
			while(getFilteredData() > WALL_DISTANCE){
				// turn counterclockwise
				leftMotor.backward();
				rightMotor.forward();
			}
			
			Sound.beep();
			angleB = odo.getAng();
			
			// calculate theta
			theta = computeAngle(angleA, angleB);
			
			// turn to the 0-axis
			navigator.turnTo(theta, true);
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		}
	}

	private float getFilteredData() {
		
		int FILTER_OUT = 20;
		float result = 0;
		
		usSensor.fetchSample(usData, 0);
		float distance = (int)(usData[0]*100.0);
		
		if(distance > WALL_DISTANCE + NOISE_MARGIN && filterControl < FILTER_OUT){
			filterControl = filterControl + 1;
			result = lastValue;
		}
		
		if(distance > WALL_DISTANCE + NOISE_MARGIN){
			result = distance;
		}
		
		else{
			// reset filterControl
			filterControl = 0;
			result = distance;
		}
		
		lastValue = distance;
		return result;
	}
	
	// formula to compute theta
	private static double computeAngle(double angleA, double angleB){
		if(angleA > angleB){
			double theta = ((angleA + angleB) / 2 + 45) % 360;
			Sound.beep();
			Sound.beep();
			return theta;
		}
		else {
			double theta = ((angleA + angleB) / 2 + 225) % 360;	
			return theta;
		}
	}
	
	
}
