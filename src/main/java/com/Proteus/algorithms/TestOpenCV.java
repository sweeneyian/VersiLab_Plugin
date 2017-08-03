package com.Proteus.algorithms;


import com.Proteus.VersiLab_Plugin;
import com.Proteus.gui.view.PopupController;
import com.Proteus.gui.view.VersiLabController;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.imgproc.Imgproc.*;

public class TestOpenCV implements VersiLabController.OpenCVImageProcess {
	
	public void initialise(){
		// this is how we initialise initialise popop sliders
		VersiLabController mainController = VersiLab_Plugin.getVersiLabPluginController();

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