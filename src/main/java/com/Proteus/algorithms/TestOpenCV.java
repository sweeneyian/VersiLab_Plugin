package com.Proteus.algorithms;

import VersiLab.Main;
import VersiLab.popup.PopupController;
import VersiLab.Controller;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.imgproc.Imgproc.*;

public class TestOpenCV implements Controller.OpenCVImageProcess {
	
	public void initialise(){
		// this is how we initialise initialise popop sliders
		Controller mainController = Main.getController();

		mainController.addPopupSlider("Sigma", 0,20,1,0.1);
		//Controller.addPopupSlider("Sigma", 1,20,20,0.1);
		
	}
	
	public Mat processImage(Mat imageIn, int counter) {
		// This is the black box
		
		Size size = new Size(401, 401);
		double sigma = PopupController.getSliderValue(0); // get value from slider
		Imgproc.GaussianBlur(imageIn, imageIn, size, sigma, sigma, BORDER_DEFAULT);
			
		return imageIn;
    }
		
}