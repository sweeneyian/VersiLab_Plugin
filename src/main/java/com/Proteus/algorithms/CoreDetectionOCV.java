package com.Proteus.algorithms;
 
import com.Proteus.gui.view.PopupController;
import com.Proteus.gui.view.VersiLabController;
import com.Proteus.imagej.Versilab_Plugin;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.*;

/*
 * Core detection algorithm to be used during background calibration phase. 
 * Background image file needs to be selected for processing.
 * Cores are saved to memory and accessible during main Camera Loop
 * 
 * Slider popup appears giving user ability to control processing parameter values
 * 
 * author Ian Sweeney
 * 
 */

public class CoreDetectionOCV implements VersiLabController.OpenCVImageProcess {

    public void initialise() {
        VersiLabController mc = Versilab_Plugin.getVersiLabPluginController();
        mc.clearPopupSliders();
        mc.addPopupSlider("1 - Kernel size", 1, 7, 1, 2); // slider [0]
        mc.addPopupSlider("2 - Threshold1", 0, 255, 91, 1); // slider [1]
        mc.addPopupSlider("3 - Canny Threshold2", 0, 255, 126, 1); // slider [2]
        mc.addPopupSlider("3 - Canny Ratio", 0, 255, 45, 1); // slider [3]
        mc.addPopupSlider("3 - Aperture size", 3, 7, 3, 2); // slider [4]
        mc.addPopupSlider("3 - Bool L2gradient", 0, 1, 1, 1); // slider [5]

        mc.addPopupSlider("4 - Min_radius", 1, 5, 1, 1); // slider [6]
        mc.addPopupSlider("4 - Max_radius", 3, 6, 6, 1); // slider [7]
        mc.addPopupSlider("4 - Min_dist", 4, 10, 4, 1); // slider [8]

        mc.addPopupSlider("4 - Roundness", 1, 2, 1.6, 0.01); // slider [9]
        mc.addPopupSlider("Phase", 0, 4, 4, 1); // slider [10]

    }

    public Mat processImage(Mat imageIn, int counter) {
    	int phase = (int) PopupController.getSliderValue(10); // get Stage
    	
        if (phase==0) {// return raw image
        	System.out.println("phase0");
            return imageIn;
        }

        Mat processed = new Mat();
        Mat heirachy = new Mat();

        final List<Circle> balls = new ArrayList<Circle>();
        final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        // Get slider values
        int kernel_size = (int) PopupController.getSliderValue(0);
        int threshold1 = (int) PopupController.getSliderValue(1);
        int threshold2 = (int) PopupController.getSliderValue(2);
        int ratio = (int) PopupController.getSliderValue(3);
        int aperture_size = (int) PopupController.getSliderValue(4);
        if (aperture_size!=3 || aperture_size != 7)
            aperture_size = 5;
        boolean L2gradient;
        if (PopupController.getSliderValue(5) ==0)
            L2gradient = false;
        else
            L2gradient = true;
        int min_radius = (int) PopupController.getSliderValue(6);
        int max_radius = (int) PopupController.getSliderValue(7);
        int min_dist = (int) PopupController.getSliderValue(8);

        double roundness =  PopupController.getSliderValue(9);


        blur(imageIn, processed, new Size(kernel_size, kernel_size));
        if (phase==1) {
        	System.out.println("phase1");
            return processed;
        }

        threshold(processed, processed, threshold1, 255, 0);
        if (phase==2) {
        	System.out.println("phase2");
            return processed;
        }

        Canny(processed, processed, threshold2, threshold2 * ratio, aperture_size, L2gradient);

        if (phase==3) {
        	System.out.println("phase3");
            return processed;
        }

        // find the contours
        Imgproc.findContours(processed, contours, heirachy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        double minArea = Math.PI * (min_radius * min_radius); // minimal ball area
        double maxArea = Math.PI * (max_radius * max_radius); // maximal ball area


        for (MatOfPoint contour :contours) {
            double area = Imgproc.contourArea(contour);
            if (area > minArea) {
                if (area < maxArea) {
                    if (boundingRect(contour).width < max_radius*2) {
                        if (boundingRect(contour).height > boundingRect(contour).width/roundness) {
                            // we found a ball
                            float[] radius = new float[1];
                            Point center = new Point();
                            Imgproc.minEnclosingCircle(new MatOfPoint2f(contour.toArray()), center, radius);

                            if (balls.size() == 0)
                                balls.add(new Circle(center.x, center.y, 2));
                            else {
                                for (int k = 0; k < balls.size(); k++) {
                                    double dist2 = (center.x - balls.get(k).x) * (center.x - balls.get(k).x)
                                            + (center.y - balls.get(k).y) * (center.y - balls.get(k).y);
                                    if (dist2 < min_dist * min_dist)
                                        continue;
                                    if (k == balls.size() - 1)
                                        balls.add(new Circle(center.x, center.y, 2));
                                    //dont add balls that are close together
                                }
                            }
                        }
                    }
                }
            }
        }

/*       VersiLabController.clearTroveInt(); // make sure we adding to an empty int array
        //VersiLabController.clearInitVector();
        Point circlepoint = new Point();
        for (Circle ball: balls) {
        	circlepoint.x = ball.x;
        	circlepoint.y = ball.y;
            circle(imageIn, circlepoint, (int) ball.radius, new Scalar(0, 255, 0), 1);
            VersiLabController.addTroveDouble(ball.x);
            VersiLabController.addTroveDouble(ball.y);
            VersiLabController.addTroveDouble(ball.radius);
        }*/
        
        //VersiLabController.clearTroveInt(); // make sure we adding to an empty int array
        VersiLabController.clearInitVector();
        Point circlepoint = new Point();
        for (Circle ball: balls) {
        	circlepoint.x = ball.x;
        	circlepoint.y = ball.y;
            circle(imageIn, circlepoint, (int) ball.radius, new Scalar(0, 255, 0), 1);
            
            VersiLabController.addInitVector(ball.x);
            VersiLabController.addInitVector(ball.y);
            VersiLabController.addInitVector(ball.radius);
        }
        
        System.out.println("phase4");
        return imageIn;
    }
}
