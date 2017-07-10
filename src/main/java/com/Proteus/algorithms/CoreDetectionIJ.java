package com.Proteus.algorithms;

import VersiLab.Controller;
import VersiLab.Main;
import VersiLab.popup.PopupController;
import ij.ImagePlus;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.*;

public class CoreDetectionIJ implements Controller.ImageJImageProcess {

    @Override
    public ImagePlus processImage(ImagePlus imageIn, int counter) {

        imageIn.getProcessor().findEdges();
        int threshhold = (int) PopupController.getSliderValue(5); // get value from slider
        imageIn.getProcessor().threshold(threshhold);

        //imageIn.getProcessor().
        return imageIn;
    }

    public void initialise() {
		System.out.println("Initialising CoreDetectionIJ");
		Controller mainController = Main.getController();
		mainController.addPopupSlider("min_dist", 5, 15, 8, 1); // slider [0]
        mainController.addPopupSlider("param_1 Canny Edge", 60, 100, 60, 1); // slider [1]
        mainController.addPopupSlider("param_2 Center detection", 0, 255, 255, 1); // slider [2]
        mainController.addPopupSlider("min_radius", 1, 5, 2, 1); // slider [3]
        mainController.addPopupSlider("max_radius", 1, 20, 3, 1); // slider [4]
        mainController.addPopupSlider("threshold", 0, 255, 100, 1); // slider [5]
        //System.out.println("we are here2");

	}
}