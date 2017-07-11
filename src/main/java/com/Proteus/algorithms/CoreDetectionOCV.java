package com.Proteus.algorithms;

import com.Proteus.VersiLab_Plugin;
import com.Proteus.gui.view.PopupController;
import com.Proteus.gui.view.VersiLabController;


import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import org.opencv.core.Scalar;

import java.util.*;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;

public class CoreDetectionOCV implements VersiLabController.OpenCVImageProcess {

    public void initialise() {
        //System.out.println("we are here1");
        VersiLabController mainController = VersiLab_Plugin.getVersiLabPluginController();
        mainController.clearPopupSliders();
        mainController.addPopupSlider("Threshhold", 0, 255, 255, 1); // slider [0]
        mainController.addPopupSlider("Canny threshold", 0, 255, 255, 1); // slider [0]
        mainController.addPopupSlider("Canny Ratio", 0, 255, 1, 1); // slider [1]

        mainController.addPopupSlider("min_radius", 1, 5, 3, 1); // slider [2]
        mainController.addPopupSlider("max_radius", 3, 6, 3, 1); // slider [3]
        mainController.addPopupSlider("min_dist", 4, 10, 5, 1); // slider [4]
        mainController.addPopupSlider("kernel size", 1, 7, 1, 2); // slider [5]
        mainController.addPopupSlider("appeture size", 3, 7, 3, 2); // slider [6]
        mainController.addPopupSlider("bool L2gradient", 0, 1, 1, 1); // slider [7]
        mainController.addPopupSlider("roundness", 1, 2, 1, 0.01); // slider [7]
        mainController.addPopupSlider("Stage", 0, 5, 0, 1); // slider [7]
        // System.out.println("we are here2");

    }

    public Mat processImage(Mat imageIn, int counter) {
        //cvtColor(imageIn, imageIn, COLOR_BGR2GRAY);
        System.out.println("counter" + counter);

        int stage = (int) PopupController.getSliderValue(10); // get
        if (stage==0)
            return imageIn;

        Mat processed = new Mat();

        Mat heirachy = new Mat();

        final List<Circle> balls = new ArrayList<Circle>();
        //final List<BallCluster> ballClusters = new ArrayList<BallCluster>();
        final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        int thresholdThreshold = (int) PopupController.getSliderValue(0);
        int threshold1 = (int) PopupController.getSliderValue(1); // get value from slider
        int ratio = (int) PopupController.getSliderValue(2); // get value from slider
        int min_radius = (int) PopupController.getSliderValue(3); // get value from slider
        int max_radius = (int) PopupController.getSliderValue(4); // get value from slider
        int min_dist = (int) PopupController.getSliderValue(5); // get value from slider

        int kernelsize = (int) PopupController.getSliderValue(6); // get value from slider

        double roundness =  PopupController.getSliderValue(9); // get

        int appeturesize = (int) PopupController.getSliderValue(7); // get value from slider
        if (appeturesize!=3 || appeturesize != 7)
            appeturesize = 5;

        boolean L2gradient;
        if (PopupController.getSliderValue(8) ==0) // get value from slider
            L2gradient = false;
        else
            L2gradient = true;



        blur(imageIn, processed, new Size(kernelsize, kernelsize));
        if (stage==1)
            return processed;

        threshold(processed, processed, thresholdThreshold, 255, 0);
        if (stage==2)
            return processed;
        Canny(processed, processed, threshold1, threshold1 * ratio, appeturesize, L2gradient);

        if (stage==3)
            return processed;
        //Mat cirleKernel = getStructuringElement(MORPH_ELLIPSE, new Size(min_radius,min_radius));


        // find the contours
        Imgproc.findContours(processed, contours, heirachy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        double minArea = Math.PI * (min_radius * min_radius); // minimal ball area
        double maxArea = Math.PI * (max_radius * max_radius); // maximal ball area

        //int threshold1 = (int) PopupController.getSliderValue(1); // get value from slider
        // int threshold2 = (int) PopupController.getSliderValue(2); // get value from slider

        for (MatOfPoint contour :contours) {
            //for (int l = 0; l < heirachy. ; l++)
            double area = Imgproc.contourArea(contour);
            if (area > minArea) {
                if (area < maxArea) {
                    if (boundingRect(contour).width < max_radius*2) {
                        if (boundingRect(contour).height > boundingRect(contour).width/roundness) {

                            // we found a ball
                            float[] radius = new float[1];
                            Point center = new Point();
                            Imgproc.minEnclosingCircle(new MatOfPoint2f(contour.toArray()), center, radius);


//                    br = cv2.boundingRect(contour)
//                    radii.append(br[2])
//
//                    m = cv2.moments(contour)
//                    center = (int(m['m10'] / m['m00']), int(m['m01'] / m['m00']))
//                    centers.append(center)

                            if (balls.size() == 0)
                                balls.add(new Circle(center.x, center.y, 2));
                            else
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

            VersiLabController.clearTroveInt();


        for (Circle ball: balls) {
                // void circle(Mat img, Point center, int radius, Scalar color, int thickness)
                circle(imageIn, new Point(ball.x, ball.y), (int) ball.radius, new Scalar(0, 255, 0), 1);
                //circle(imageIn, (Point) centers.get(j), 1, new Scalar(0, 255, 0), -1);
                //cv2.circle(drawing, center, 3, (255, 0, 0), -1)
                VersiLabController.addTroveInt((int)ball.x);
                VersiLabController.addTroveInt((int)ball.y);
                VersiLabController.addTroveInt((int)ball.radius);
            }

            if (stage==4)
                return imageIn;

        /*else {
            blur(imageIn, imageIn, new Size(kernelsize, kernelsize));
            Canny(imageIn, imageIn, threshold1, threshold1 * ratio, appeturesize, L2gradient);

            //Mat cirleKernel = getStructuringElement(MORPH_ELLIPSE, new Size(min_radius,min_radius));


            // find the contours
            Imgproc.findContours(imageIn, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

            //double minArea = Math.PI * (min_radius * min_radius); // minimal ball area
            //double maxArea = Math.PI * (max_radius * max_radius); // maximal ball area

            //int threshold1 = (int) PopupController.getSliderValue(1); // get value from slider
            // int threshold2 = (int) PopupController.getSliderValue(2); // get value from slider


        }*/


        /*
        double thresholdArea = PopupController.getSliderValue(0);
        cvtColor(imageIn, imageIn, COLOR_BGR2GRAY);

        Mat cirleKernel = getStructuringElement(MORPH_ELLIPSE, new Size(3,3));
        dilate(imageIn, imageIn, cirleKernel);


        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);



        // threshold(Mat src, Mat dst, double thresh, double maxval, int type)
        //Canny( src_gray, canny_output, thresh, thresh*2, 3 );
        Canny(imageIn, imageIn, threshold1, threshold2);
    //public static void Canny(Mat dx, Mat dy, Mat edges, double threshold1, double threshold2, boolean L2gradient)

       // Canny();
        Mat cirleKernel = getStructuringElement(MORPH_ELLIPSE, new Size(3,3));
        dilate(imageIn, imageIn, cirleKernel);
        //dilate(imageIn, imageIn, cirleKernel);



        //System.out.println(imageIn);

        //if (true) {
        //    return imageIn;

       // }
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Mat hierarchy = new Mat();
    //public static void findContours(Mat image, List<MatOfPoint> contours, Mat hierarchy, int mode, int method)
        findContours(imageIn, contours, hierarchy, RETR_LIST, CHAIN_APPROX_NONE );

        Vector centers = new Vector();
        Vector radii = new Vector();

        if (contours.size()>0)
            System.out.println(contours.size());
        for (int i=0; i<contours.size(); i++){
            double area = contourArea(contours.get(i));
            if (area > thresholdArea)
                continue;

            Rect br = boundingRect(contours.get(i));
            radii.add(br.width);

            Moments m = moments(contours.get(i));
            centers.add(new Point((int) (m.get_m10()/m.get_m00()), (int)(m.get_m01()/m.get_m00())));
        }

        for (int j=0; j< centers.size() ;j++){
            // void circle(Mat img, Point center, int radius, Scalar color, int thickness)
            circle(imageIn, (Point) centers.get(j), (int) radii.get(j), new Scalar(0, 255, 0), 1);
            circle(imageIn, (Point) centers.get(j), 1, new Scalar(0, 255, 0), -1);
            //cv2.circle(drawing, center, 3, (255, 0, 0), -1)
        }

        //findContours(imageIn, contours, RETR_LIST, CHAIN_APPROX_NONE);
        //image = cv2.dilate(image, el, iterations=6)*/

            return imageIn;
    }
}

//	public Mat processImage(Mat imageIn) {
//        Mat processedImage = new Mat();
//
//
//        int threshold1 = (int) PopupController.getSliderValue(0); // get value from slider
//        int threshold2 = (int) PopupController.getSliderValue(1); // get value from slider
//        int apertureSize = (int) PopupController.getSliderValue(2); // needs to be 3, 5 or 7;
//        if (apertureSize!=3 || apertureSize!=7){
//            apertureSize = 5;
//        }
//        int L2gradient = (int)  PopupController.getSliderValue(3); // get value from slider
//        //if
//        boolean gradient = false;
//        if (L2gradient!=0){
//            gradient = true;
//        }
//        int min_dist = (int) PopupController.getSliderValue(4);
//        int param_1 = (int) PopupController.getSliderValue(5);
//        int param_2 = (int) PopupController.getSliderValue(6);
//        int minRadius = (int)PopupController.getSliderValue(7);
//        int maxRadius = (int)PopupController.getSliderValue(8);
//        int threshold = (int) PopupController.getSliderValue(9);
//
//        Imgproc.cvtColor(imageIn, imageIn, Imgproc.COLOR_BGR2GRAY);
//
//        /*
//        Canny image processing
//                Imgproc.Canny(detectedEdges, detectedEdges, this.threshold.getValue(), this.threshold.getValue() * 3, 3, false);
//         */
//        //Imgproc.Canny(imageIn, imageIn, threshold1, threshold2 * 3, apertureSize, gradient);
//
//
//        /*
//        Huogh circles
//        Imgproc.HoughCircles(thresholdImage, circles, Imgproc.CV_HOUGH_GRADIENT,
//         2.0, thresholdImage.rows() / 8, iCannyUpperThreshold, iAccumulator,
//         iMinRadius, iMaxRadius);
//         */
//        Imgproc.HoughCircles(imageIn, processedImage, Imgproc.CV_HOUGH_GRADIENT,
//                2.0, min_dist, param_1, param_2,
//                minRadius, maxRadius);
//        //image_color= cv2.imread("Ye3gs.png")
//        //image_ori = cv2.cvtColor(image_color,cv2.COLOR_BGR2GRAY)
//
//        if (processedImage.cols()>0){
//            System.out.println("detected circles  " +processedImage.cols());
//            System.out.println("min_dist  " +min_dist);
//            System.out.println("param_1  " +param_1);
//            System.out.println("param_2  " +param_2);
//            System.out.println("minRadius " +minRadius);
//            System.out.println("maxRadius " +maxRadius);
//
//
//            for (int x = 0; x < processedImage.cols(); x++)
//            {
//
//                for(int i = 0; i < processedImage.cols(); i++) {
//                    double[] circle = processedImage.get(0, i);
//
//                    if (circle == null)
//                        break;
//
//
//
//                    //FloatPointer p = new FloatPointer(cvGetSeqElem(circles, i));
//                    //int radius = Math.round(p.get(2));
//
//                    Point center = new Point (Math.round(circle[0]), Math.round(circle[1]));
//                    int radius = (int)Math.round(circle[2]);
//                    //public Size(double width, double height)
//                    //Size size = new Size((int)Math.round(circle[2])*2,(int)Math.round(circle[2])*2);
//                    //System.out.println("X: " + circle[0]+ "\tY: "+circle[1] + "\tradius: " +circle[2]);
//                    // width double radius, height double radius
//
//                    /////////////////////////////////////////////////////////////////////////////
//                    // draw the circle center
//                    //circle(Mat img, Point center, int radius, Scalar color, int thickness)
//                    //circle(imageIn, new Point(50,50), 10, new Scalar(0,255,0), 5 );
//                    ////////////////////////////////////////////////////////////////////////////////
//                    circle(imageIn, center, radius, new Scalar(255,255,255), 1 );
//
//
//                    //processedImage. getpr((int)circle[0] - (int)circle[2], (int)circle[1] - (int)circle[2], (int)circle[2] * 2, (int)circle[2] * 2);
//                }
//
//            }
//
//            return imageIn;
//        }
//        //Log.w("circles", circles.cols()+"");
//
///*        Scalar lower_bound = new Scalar(0, 0, 10);
//        Scalar upper_bound = new Scalar(255, 255, 195);
//
//        //Imgproc.inRan//
//        //public static void inRange(Mat src, Scalar lowerb, Scalar upperb, Mat dst)
//        //Core.inRange(imageIn, lower_bound, upper_bound, imageIn);
//
//        int kernelsize = 3;
//
//        Mat kernel = getStructuringElement(MORPH_RECT, new Size(kernelsize, kernelsize));
//        Mat kernel2 = getStructuringElement(MORPH_RECT, new Size(kernelsize, kernelsize));*/
//
//
//        return imageIn;
//        /*Mat kernel = new Mat(kernelsize,kernelsize, CvType.CV_32F) {
//            {
//                put(1, 1, 1);
//                put(1, 1, 1);
//                put(1, 1, 1);
//
//                put(1, 1, 1);
//                put(1, 1, 1);
//                put(1, 1, 1);
//
//                put(1, 1, 1);
//                put(1, 1, 1);
//                put(1, 1, 1);
//            }
//        };*//*
//
//
//        Imgproc.erode(imageIn, imageIn, kernel, 6);
//
//        kernel = np.ones((3, 3), np.uint8)
//
//#Use erosion and dilation combination to eliminate false positives.
//#In this case the text Q0X could be identified as circles but it is not.
//                mask = cv2.erode(mask, kernel, iterations=6)
//        mask = cv2.dilate(mask, kernel, iterations=3)
//
//        closing = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel)
//
//        contours = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
//                cv2.CHAIN_APPROX_SIMPLE)[0]
//        contours.sort(key=lambda x:cv2.boundingRect(x)[0])
//
//        array = []
//        ii = 1
//        print len(contours)
//        for c in contours:
//        (x,y),r = cv2.minEnclosingCircle(c)
//        center = (int(x),int(y))
//        r = int(r)
//        if r >= 6 and r<=10:
//        cv2.circle(image,center,r,(0,255,0),2)
//        array.append(center)*/
//
//
//        /*//imageIn.getProcessor().findEdges();
//        //int threshhold = (int) PopupController.getSliderValue(5); // get value from slider
//        //imageIn.getProcessor().threshold(threshhold);
//
//        int threshold1 = (int) PopupController.getSliderValue(0); // get value from slider
//        int threshold2 = (int) PopupController.getSliderValue(1); // get value from slider
//        int apertureSize = (int) PopupController.getSliderValue(2); // needs to be 3, 5 or 7;
//        if (apertureSize!=3 || apertureSize!=7){
//            apertureSize = 5;
//        }
//        int L2gradient = (int)  PopupController.getSliderValue(3); // get value from slider
//        //if
//        boolean gradient = false;
//        if (L2gradient!=0){
//            gradient = true;
//        }
//        int min_dist = (int) PopupController.getSliderValue(4);
//        int param_1 = (int) PopupController.getSliderValue(5);
//        int param_2 = (int) PopupController.getSliderValue(6);
//        int minRadius = (int)PopupController.getSliderValue(7);
//        int maxRadius = (int)PopupController.getSliderValue(8);
//        int threshold = (int) PopupController.getSliderValue(9);
//
//   // public static void HoughCircles(Mat image, Mat circles, int method, double dp, double minDist, double param1, double param2, int minRadius, int maxRadius)
//        Mat circles = new Mat();
//        //Imgproc.Canny(imageIn, imageIn, threshold1, threshold2, apertureSize, gradient);
//        Imgproc.HoughCircles(imageIn, circles, Imgproc.CV_HOUGH_GRADIENT,
//                2.0, imageIn.rows()/8, param_1, param_2,
//                minRadius, maxRadius);
//
//
//
//        //Imgproc.GaussianBlur( imageIn, imageIn, new Size(9, 9), 2, 2 );
//
//       // Mat circles = new Mat();
//
//        /// Apply the Hough Transform to find the circles
//        //HoughCircles( imageIn, circles, CV_HOUGH_GRADIENT, 1, imageIn.rows()/8, 200, 100, 0, 0 );
//
//        /// Draw the circles detected
//        if (circles.cols()>0) {
//            System.out.println("circles" + circles.cols() + "");
//        }
//        for (int x = 0; x < circles.cols(); x++)
//        {
//            double vCircle[]=circles.get(0,x);
//
//            Point center=new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
//            //int radius = (int)Math.round(vCircle[2]);
//            // draw the circle center
//            Imgproc.circle(circles, center, 3,new Scalar(0,255,0), 2, 8, 0 );
//            // draw the circle outline
//            //Imgproc.circle( circles, center, radius, new Scalar(0,0,255), 3, 8, 0 );
//
//        }*/
//
//
//        //Imgproc.threshold(imageIn, imageIn, threshold, 255, THRESH_BINARY);
//
//        //return circles;
//        //System.out.println("We in the processLoop");
//        // This is the black box
//       /* double t0 = System.currentTimeMillis();
//        //System.out.println("Processstart" );
//
//        //t0; // t1 = previous t0
//
//        int min_dist = (int) PopupController.getSliderValue(0);
//        int param_1 = (int) PopupController.getSliderValue(1);
//        int param_2 = (int) PopupController.getSliderValue(2);
//        int minRadius = (int)PopupController.getSliderValue(3);
//        int maxRadius = (int)PopupController.getSliderValue(4);
//        int threshold = (int)PopupController.getSliderValue(5);
//
//		Imgproc.threshold(imageIn, imageIn, threshold, 255, THRESH_BINARY);
//        Imgproc.cvtColor(imageIn, imageIn, Imgproc.COLOR_BGR2GRAY);
//
//        Mat kernel = getStructuringElement(MORPH_RECT, new Size(minRadius, minRadius));
//        Mat kernel2 = getStructuringElement(MORPH_RECT, new Size(maxRadius, maxRadius));
//        morphologyEx(imageIn, imageIn, MORPH_OPEN, kernel);
//        morphologyEx(imageIn, imageIn, MORPH_CLOSE, kernel2);
//
//
//        Mat circles = new Mat();
//
//        Imgproc.HoughCircles(imageIn, circles, Imgproc.CV_HOUGH_GRADIENT, 2, min_dist, param_1, param_2, minRadius, maxRadius);
////        //HoughCircles(gray_filtered, circles, CV_HOUGH_GRADIENT, 2, 10, 100, 20, 5, 10);
////        System.out.println("Circles: " + circles);
////        double t1 = System.currentTimeMillis();
////
////        double deltaT = t1 - t0;
////        System.out.println("Process time " + deltaT);
//
//        return circles;*/
//    }
//}