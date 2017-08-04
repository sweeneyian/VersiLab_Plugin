package com.Proteus.algorithms;

import com.Proteus.VersiLab_Plugin;
import com.Proteus.gui.view.PopupController;
import com.Proteus.gui.view.VersiLabController;
import clojure.lang.IFn;
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
        VersiLabController mainController = VersiLab_Plugin.getVersiLabPluginController();
        mainController.clearPopupSliders();
    }

    public Mat processImage(Mat imageIn, int counter) {
        //cvtColor(imageIn, imageIn, COLOR_BGR2GRAY);
        System.out.println("counter" + counter);

        TIntArrayList troveIntArray = VersiLabController.getTroveIntArray();
        //List<Circle> balls = new ArrayList<Circle>();
        for (int i =0; i<troveIntArray.size(); i+=3){
            //balls.add(new Circle((double)initVec.get(i), (double)initVec.get(i+1), (double)initVec.get(i+2) ));

            circle(imageIn, new Point(troveIntArray.get(i), troveIntArray.get(i+1)), troveIntArray.get(i+2), new Scalar(0, 255, 0), 1);
        }
            return imageIn;
    }
}
