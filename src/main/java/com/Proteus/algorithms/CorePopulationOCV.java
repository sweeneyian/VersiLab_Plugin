package com.Proteus.algorithms;

import VersiLab.Controller;
import VersiLab.Main;
import VersiLab.popup.PopupController;
import clojure.lang.IFn;
import gnu.trove.list.array.TIntArrayList;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



import static org.opencv.imgproc.Imgproc.*;

public class CorePopulationOCV implements Controller.OpenCVImageProcess {

    public void initialise() {
        //System.out.println("we are here1");
        Controller mainController = Main.getController();
        mainController.clearPopupSliders();
        mainController.addPopupSlider("threshold1", 0, 255, 255, 1); // slider [0]
        mainController.addPopupSlider("threshold2", 0, 3, 3, 1); // slider [1]
    }

    public Mat processImage(Mat imageIn, int counter) {
        //cvtColor(imageIn, imageIn, COLOR_BGR2GRAY);
        System.out.println("counter" + counter);

        TIntArrayList troveIntArray = Controller.getTroveIntArray();
        //List<Circle> balls = new ArrayList<Circle>();
        for (int i =0; i<troveIntArray.size(); i+=3){
            //balls.add(new Circle((double)initVec.get(i), (double)initVec.get(i+1), (double)initVec.get(i+2) ));

            circle(imageIn, new Point(troveIntArray.get(i), troveIntArray.get(i+1)), troveIntArray.get(i+2), new Scalar(0, 255, 0), 1);
        }
            return imageIn;
    }
}
