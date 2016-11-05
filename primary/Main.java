package primary;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
//import localization.LCDInfo;
//import localization.LightLocalizer;
//import localization.Odometer;
//import localization.USLocalizer;

/**
 * @author Erik-Olivier Riendeau, 2016
 * Main Class that contains all parameters passed to other Classes and Threads. Also starts the ODOMETER, THE DISPLAY
 * ,THE TIMER, THE USSENSORS AND THE COLORSENSORS. Starts by initializing the WIFI class to fetch important information
 * as parameters for the demo.
 *  
 */
public class Main {

	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 16.5; //14.7
	// sets motor to according ports
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
	private static final RegulatedMotor elevateMotor = new EV3LargeRegulatedMotor(MotorPort.D);
	private static final RegulatedMotor clawMotor = new EV3LargeRegulatedMotor(MotorPort.A);
	
	// set up sensors to ports
	private static final Brick brick = BrickFinder.getDefault();
	private static final Port s4 = brick.getPort("S4");
	private static final Port s3 = brick.getPort("S3");
	private static final Port s2 = brick.getPort("S2");
	private static final Port s1 = brick.getPort("S1");
	private static final EV3ColorSensor colorSensorRight = new EV3ColorSensor(s4);
	private static final EV3ColorSensor colorSensorLeft = new EV3ColorSensor(s3);
	private static final EV3UltrasonicSensor usSide = new EV3UltrasonicSensor(s2);
	private static final EV3UltrasonicSensor usFront = new EV3UltrasonicSensor(s1);
	
	
	/**
	 * @param args 
	 * Main method that executes directly in the EV3
	 */
	public static void main(String[] args) {
	    
		// https://sourceforge.net/p/lejos/wiki/Remote%20access%20to%20an%20EV3/
		// https://lejosnews.wordpress.com/2015/02/11/pan-configuration/ to add a sensor from another brick
		
	    // set up the display
	    
	}
	
	/**
	 * Creates a WIFI object and accesses the object to fetch needed information
	 */
	public static void getWIFI(){
		
	}
	
	/**
	 * Starts the TIMER, ODOMETER, USSENSORS, COLORSENSORS AND DISPLAY
	 */
	public static void startThreads(){
		Odometer odo = null;
		SampleProvider usValue = null;
		float[] usData = null;
		
		while (Button.waitForAnyPress() != Button.ID_LEFT
				&& Button.waitForAnyPress() != Button.ID_RIGHT); {

		if (Button.waitForAnyPress() == Button.ID_LEFT) {
			// perform the ultrasonic localization
			USLocalizer usl = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE, leftMotor, rightMotor);
			usl.doLocalization();
		}
		
		else{
			// perform the light sensor localization
			LightLocalizer lsl = new LightLocalizer(odo, colorSensorRight, leftMotor, rightMotor);
			lsl.doLocalization();			
		}}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
			
	}
	
	/**
	 * Initializes instances of localization and navigation
	 */
	public static void initializeObjects(){
			//Setup ultrasonic sensor
			SampleProvider usValue = usFront.getMode("Distance");			// colorValue provides samples from this instance
			float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned

			//setup color sensor
			//**************************************************** color sensor port might be changed
			SampleProvider colorValue = colorSensorRight.getMode("Red");			// colorValue provides samples from this instance
			float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
			// setup the odometer and display
			Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
			LCDInfo lcd = new LCDInfo(odo);
					
	}
}	
