package com.Proteus.algorithms;

import com.Proteus.gui.view.PopupController;
import com.Proteus.gui.view.VersiLabController;
import com.Proteus.imagej.Versilab_Plugin;

import clojure.lang.IFn;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;



import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



import static org.opencv.imgproc.Imgproc.*;

public class CorePopulationOCV implements VersiLabController.OpenCVImageProcess {

    public void initialise() {
        //System.out.println("we are here1");
        VersiLabController mainController = Versilab_Plugin.getVersiLabPluginController();
        mainController.clearPopupSliders();
    }

    /*public Mat processImage(Mat imageIn, int counter) {
        //cvtColor(imageIn, imageIn, COLOR_BGR2GRAY);
        System.out.println("counter" + counter);
        Point circlepoint = new Point();
        TDoubleArrayList troveArray = VersiLabController.getTroveDoubleArray();
        //List<Circle> balls = new ArrayList<Circle>();
        for (int i =0; i<VersiLabController.getTroveDoubleArray().size(); i+=3){
        	circlepoint.x = (int)troveArray.get(i);
        	circlepoint.y = (int)troveArray.get(i+1);
            circle(imageIn, circlepoint, (int)troveArray.get(i+2), new Scalar(0, 255, 0), 1);
        }
        return imageIn;
    }*/
    
    public Mat processImage(Mat imageIn, int counter) {
        //cvtColor(imageIn, imageIn, COLOR_BGR2GRAY);
        System.out.println("counter" + counter);
        Point circlepoint = new Point();
        Vector<Double> initVec = VersiLabController.getInitVector();
        //List<Circle> balls = new ArrayList<Circle>();
        for (int i =0; i<initVec.size(); i+=3){
        	//System.out.print("circlepoint x :" +(int)(double)initVec.get(i) );
        	circlepoint.x = (int)(double)initVec.get(i);
        	circlepoint.y = (int)(double)initVec.get(i+1);
            circle(imageIn, circlepoint, (int)(double)initVec.get(i+2), new Scalar(0, 255, 0), 1);
        }
        return imageIn;
    }
}
