package com.Proteus.algorithms;

import VersiLab.Main;
import VersiLab.popup.PopupController;
import VersiLab.Controller;
import ij.ImagePlus;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.*;

public class TestImageJ implements Controller.ImageJImageProcess {
	
	public void initialise(){
		// this is how we initialise initialise popop sliders
		//
		//
		Controller mainController = Main.getController();

		mainController.addPopupSlider("Sigma", 0,20,1,0.1);
		//Controller.addPopupSlider("Blue", 0,20,1,0.1);
		//VersiLab.Controller.addPopupSlider("green", 0,255,50,10);		
		//VersiLab.Controller.addPopupSlider("blue", 0,255,50,10);
		
	}
	
	public ImagePlus processImage(ImagePlus imageIn, int counter) {
		// This is the black box
		
		double sigma = PopupController.getSliderValue(0); // get value from slider

			imageIn.getProcessor().blurGaussian(sigma);
			

		return imageIn;
    }
		
}
