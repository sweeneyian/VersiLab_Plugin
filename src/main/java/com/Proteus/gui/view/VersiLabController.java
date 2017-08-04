/*
 * The MIT License
 *
 * Copyright 2016 Fiji.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.Proteus.gui.view;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//import kirkwood.nidaq.access.NiDaqException;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

//import mmcorej.CMMCore;
//import mmcorej.StrVector;

import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
//import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.core.CvType.*;
import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

import com.Proteus.utils.Utils;

import gnu.trove.list.array.TIntArrayList;
import ij.ImagePlus;
import ij.io.FileSaver;

/**
 * FXML Controller class
 *
 * @author Hadrien Mary
 */
public class VersiLabController implements Initializable {
    @Parameter
    private LogService log;
    
	// Start Stop Buttom
    @FXML
    private Button button;
    @FXML
    private Button CaptureBackground;
    // the FXML image view
    @FXML
    private ImageView currentFrame;

    @FXML
    private ComboBox CameraComboBox;
    @FXML
    private ComboBox numberOfLED;
    @FXML
    private ComboBox AlgorithmComboBox;
    @FXML
    private ComboBox CalibrationAlgorithmComboBox;

    @FXML
    private Text FrameRate;

    @FXML
    private Text FrameTime;

    @FXML
    private CheckBox saveRawDatacheckBox;

    @FXML
    private CheckBox saveProcessedDatacheckBox;
    @FXML
    private Slider saveRawDataFrequencySlider;
    private int intSaveRawDataFrequencySlider;
    @FXML
    private Text saveRawDataFrequencyText;

    private int intCameraExposure = 33;
    @FXML
    private Text cameraExposureText;
    @FXML
    private Slider cameraExposureSlider;


    private static int CAP_PROP_POS_MSEC       =0,
    CAP_PROP_POS_FRAMES     =1,
    CAP_PROP_POS_AVI_RATIO  =2,
    CAP_PROP_FRAME_WIDTH    =3,
    CAP_PROP_FRAME_HEIGHT   =4,
    CAP_PROP_FPS            =5,
    CAP_PROP_FOURCC         =6,
    CAP_PROP_FRAME_COUNT    =7,
    CAP_PROP_FORMAT         =8,
    CAP_PROP_MODE           =9,
    CAP_PROP_BRIGHTNESS    =10,
    CAP_PROP_CONTRAST      =11,
    CAP_PROP_SATURATION    =12,
    CAP_PROP_HUE           =13,
    CAP_PROP_GAIN          =14,
    CAP_PROP_EXPOSURE      =15,
    CAP_PROP_CONVERT_RGB   =16,
    CAP_PROP_WHITE_BALANCE_BLUE_U =17,
    CAP_PROP_RECTIFICATION =18,
    CAP_PROP_MONOCROME     =19,
    CAP_PROP_SHARPNESS     =20,
    CAP_PROP_AUTO_EXPOSURE =21, // DC1394: exposure control done by camera, user can adjust refernce level using this feature
    CAP_PROP_GAMMA         =22,
    CAP_PROP_TEMPERATURE   =23,
    CAP_PROP_TRIGGER       =24,
    CAP_PROP_TRIGGER_DELAY =25,
    CAP_PROP_WHITE_BALANCE_RED_V =26,
    CAP_PROP_ZOOM          =27,
    CAP_PROP_FOCUS         =28,
    CAP_PROP_GUID          =29,
    CAP_PROP_ISO_SPEED     =30,
    CAP_PROP_BACKLIGHT     =32,
    CAP_PROP_PAN           =33,
    CAP_PROP_TILT          =34,
    CAP_PROP_ROLL          =35,
    CAP_PROP_IRIS          =36,
    CAP_PROP_SETTINGS      =37;

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    private VideoCapture capture; 
    // a flag to change the button behavior
    private boolean cameraActive = false;

    private static boolean OpenCVProcessing = true;
    private static int cameraId = 0, counter =0;// backoff = 20;

    long t0, t1, deltaT;
    double fps;
    private boolean init = true;
    private Mat RawOCVframe = new Mat();
    private ImagePlus RawIJframe;
    private Mat ProcessedOCVframe;
    private ImagePlus ProcessedIJframe;
    private Stage stage;
    private Parent root;
    private String fileName = "default";
    private String filePath = "default";
    private String acquisitionPath = "C:\\AcquisitionData\\";
    private String folder;

    DecimalFormat df = new DecimalFormat("##");

    //private CMMCore core = new CMMCore();
    //String info = core.getVersionInfo();

    private static OpenCVImageProcess openCV_ip;
    private static ImageJImageProcess ImageJ_ip;
    private static OpenCVImageProcess openCVCalibration_ip;
    private static ImageJImageProcess ImageJCalibration_ip;
    private static boolean loop_processing = false;
    private static boolean calibration_processing = false;
    private static BufferedImage bi;

    //make another stage for popup
    private Stage popupStage;
    private FXMLLoader popupLoader;
    private BorderPane popupRootElement;
    private Scene popupScene ;
    private PopupController popupController;

    // LED Controls
    //NiDaqSimpleDemo niDaqSimpleDemo = new NiDaqSimpleDemo();
    
    private static int  numberOfLEDs;
    private static byte       redLED[] = { 1,0,0,0 }; // channel 0
    private static byte       greenLED[] = { 0,1,0,0 }; // channel 1
    private static byte       blueLED[] = { 0,0,1,0 }; // channel 2
    private static byte       allLED[] = { 1,1,1,1 }; // channel 2

    // background Image
    private static Mat OCVbackground;
    private static ImagePlus IJbackground;
    public static Mat getOCVbackground(){return OCVbackground;}
    public ImagePlus getIJbackground(){return this.IJbackground;}


    // Vector used in initialising ImageProcess for quick lookup during loop processing
    private static Vector<Double> initVector = new Vector<>();
    public static Vector getInitVector(){
        return initVector;
    }
    public static void addInitVector(Double addDouble){
        initVector.addElement(addDouble);
    }
    public static void clearInitVector(){
        initVector.clear();
    }

    private static TIntArrayList troveIntArray = new TIntArrayList(30000, -999);
    public static TIntArrayList getTroveIntArray(){return troveIntArray;}
    public static void addTroveInt (int addint){troveIntArray.add(addint);}
    public static void clearTroveInt (){troveIntArray.clear();}

    @SuppressWarnings("unchecked")
	@Override
    public void initialize(URL location, ResourceBundle resources) {

        createDirectories();
        capture = new VideoCapture();

        CameraComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> selected, String oldSelection, String newSelection) {

               // System.out.println("Selected " + selected);
               // System.out.println("old Selection " +  oldSelection);
               // System.out.println("new Selection " + newSelection);

                if (newSelection==null)
                    newSelection = oldSelection;
                if (newSelection != null) { //was newSelection
                    switch (newSelection) {
                        case "WebCam": /*dosomething*/
                            init = false;
                            break;
                        case "Grasshopper": /*dosomething*/
                            init = true;
                            break;

                        case "File":
                            // Load file
                            FileChooser fileChooser = new FileChooser();
                            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tiff files (*.tiff) (*.tif)", "*.tif");
                            fileChooser.getExtensionFilters().add(extFilter);

                            //Set to user directory or go to default if cannot access
                            String userDirectoryString = System.getProperty("user.home");
                            File userDirectory = new File(userDirectoryString);
                            if (!userDirectory.canRead()) {
                                userDirectory = new File("c:/");
                            }
                            fileChooser.setInitialDirectory(userDirectory);

                            File file = fileChooser.showOpenDialog(stage);
                            //System.out.println(file);

                            filePath = file.getParent();
                            //System.out.println(filePath);

                            fileName = file.getName();
                            int pos = fileName.lastIndexOf(".");
                            if (pos > 0) {
                                fileName = fileName.substring(0, pos);
                            }
                            break;

                        case "reset":
                        case "default":
                           // System.out.println("Camera Selection Error");
                    }
                }
            }
        });

        numberOfLED.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> selected, String oldSelection, String newSelection) {
            	System.out.println("LEDS Changed");
                /*if (oldSelection!=null) {
                    try {
                        niDaqSimpleDemo.endTask();
                    } catch (NiDaqException e) {
                        e.printStackTrace();
                    }
                }
                if (newSelection != null) {
                    // init niDAQ controller

                    switch (newSelection) {
                        case "1":
                            numberOfLEDs = 1;
                            try {
                                niDaqSimpleDemo.initTask();
                            } catch (NiDaqException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "2":
                            numberOfLEDs = 2;
                            try {
                                niDaqSimpleDemo.initTask();
                            } catch (NiDaqException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "3":
                            numberOfLEDs = 3;
                            try {
                                niDaqSimpleDemo.initTask();
                            } catch (NiDaqException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "0":
                            break;
                        case "default":
                            // No LEDS will switch on in loop
                            numberOfLEDs =0;
                    }
                }*/
            }
        });


        AlgorithmComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            // deprecated
            @Override
            public void changed(ObservableValue<? extends String> selected, String oldSelection, String newSelection) {


                URLClassLoader classLoader = null;
                try {
                    classLoader = new URLClassLoader(new URL[]{new File("").toURI().toURL()});
                    Class<?> loadedClass = null;
                    loadedClass = classLoader.loadClass("com.Proteus.algorithms." + newSelection);

                    // Create a new instance...
                    Object obj = loadedClass.newInstance();
                    // Santity check
                    if (obj instanceof OpenCVImageProcess) {
                        // Cast to the DoStuff interface
                        openCV_ip = (OpenCVImageProcess) obj;
                        OpenCVProcessing = true;
                    } else if (obj instanceof ImageJImageProcess) {
                        // Cast to the DoStuff interface
                        ImageJ_ip = (ImageJImageProcess) obj;
                        OpenCVProcessing = false;
                    } else {
                        System.out.println("!!!!!---------- UNKNOWN a OBJECT ------------!!!!!!!!!");
                    }

                } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException  e) {
                    e.printStackTrace();
                }



                try {
                    // startup for popop
                    //make another stage for scene2
                    popupStage = new Stage();
                    //popupStage.setScene(scene2);
                    // tell stage it is meant to pop-up (Modal)
                    // Not popupStage.initModality(Modality.APPLICATION_MODAL);
                    // Modality.APPLICATION_MODAL doesnt allow switching between stages


                    popupStage.initModality(Modality.NONE);

                    // load the FXML resource
                    popupLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
                    // store the root element so that the controllers can use it
                    popupRootElement = (BorderPane) popupLoader.load();
                    // create and style a scene
                    popupScene = new Scene(popupRootElement, 500, 500);

                    popupScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                    // create the stage with the given title and the previously created
                    // scene
                    popupStage.setTitle(fileName);

                    popupStage.setScene(popupScene);
                    // show the GUI
                    //popupStage.show();


                    // set the proper behavior on closing the application
                    popupController = popupLoader.getController();

                    if (openCV_ip != null) {
                        openCV_ip.initialise();
                    } else if (ImageJ_ip != null) {
                        ImageJ_ip.initialise();
                    }

                    popupStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent we) {
                            popupController.setClosed();
                        }
                    }));


                    // show the GUI
                    popupStage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        CalibrationAlgorithmComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> selected, String oldSelection, String newSelection) {

                URLClassLoader classLoader = null;
                try {
                    classLoader = new URLClassLoader(new URL[]{new File("").toURI().toURL()});
                    Class<?> loadedClass = null;
                    //System.out.println("com.Proteus.algorithms." + newSelection);
                    loadedClass = classLoader.loadClass("com.Proteus.algorithms." + newSelection);

                    // Create a new instance...
                    Object obj = loadedClass.newInstance();
                    //System.out.println(obj);
                    // Santity check
                    if (obj instanceof OpenCVImageProcess) {
                        // Cast to the DoStuff interface
                        openCVCalibration_ip = (OpenCVImageProcess) obj;
                        OpenCVProcessing = true;
                    } else if (obj instanceof ImageJImageProcess) {
                        // Cast to the DoStuff interface
                        ImageJCalibration_ip = (ImageJImageProcess) obj;
                        OpenCVProcessing = false;
                    } else {
                        System.out.println("!!!!!---------- UNKNOWN b OBJECT ------------!!!!!!!!!");
                    }

                } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException  e) {
                    e.printStackTrace();
                }



                try {
                    // startup for popop
                    //make another stage for scene2
                    popupStage = new Stage();
                    //popupStage.setScene(scene2);
                    // tell stage it is meant to pop-up (Modal)
                    // Not popupStage.initModality(Modality.APPLICATION_MODAL);
                    // Modality.APPLICATION_MODAL doesnt allow switching between stages


                    popupStage.initModality(Modality.NONE);

                    // load the FXML resource
                    popupLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
                    // store the root element so that the controllers can use it
                    popupRootElement = (BorderPane) popupLoader.load();
                    // create and style a scene
                    popupScene = new Scene(popupRootElement, 500, 500);

                    popupScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                    // create the stage with the given title and the previously created
                    // scene
                    popupStage.setTitle(fileName);

                    popupStage.setScene(popupScene);
                    // show the GUI
                    //popupStage.show();


                    // set the proper behavior on closing the application
                    popupController = popupLoader.getController();

                    if (openCVCalibration_ip != null) {
                        openCVCalibration_ip.initialise();
                    } else if (ImageJCalibration_ip != null) {
                        ImageJCalibration_ip.initialise();
                    }

                    popupStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent we) {
                            popupController.setClosed();
                        }
                    }));


                    // show the GUI
                    popupStage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


        cameraExposureSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                cameraExposureText.textProperty().setValue(
                        String.valueOf((int) cameraExposureSlider.getValue())+" ms");
                //System.out.println("slider value changed");
                intCameraExposure = (int)cameraExposureSlider.getValue();
                //boolean setexposure = capture.set(CAP_PROP_EXPOSURE ,((double)cameraExposureSlider.getValue()));
                //System.out.println(setexposure);
                //set(CV_CAP_PROP_EXPOSURE, (double)cameraExposureSlider.getValue()/1000 );
                //capture.set(CAP_PROP_FRAME_WIDTH ,((double)cameraExposureSlider.getValue()));
                //capture.set(CAP_PROP_FRAME_HEIGHT ,((double)cameraExposureSlider.getValue()));
                //CAP_PROP_FRAME_WIDTH    =3,
                 //       CAP_PROP_FRAME_HEIGHT   =4,
            }
        });

        saveRawDataFrequencySlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                saveRawDataFrequencyText.textProperty().setValue("1/" +
                        String.valueOf((int) saveRawDataFrequencySlider.getValue()));
                //System.out.println("slider value changed");

            }
        });

    }

    @FXML
    protected void captureBackground(ActionEvent actionEvent) {

        calibration_processing = true;
        loop_processing = false;

        if (!this.cameraActive) {
            //Main.popupStage.show();
            // start the video capture
            this.capture.open(cameraId);
            initialiseCamera();
            counter = 0;

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec) from 33 milisecond scheduler
                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run() {
                        counter++;
                        updateTimers();
                        // LED Control
                        LEDControl();
                        grabFrame();
                        processData();
                        saveCalibrationData();
                        // convert and show the frame
                        Image imageToShow;

                        if (OpenCVProcessing) {
                            imageToShow = Utils.mat2Image(ProcessedOCVframe);
                        }
                        else {
                            bi = ProcessedIJframe.getBufferedImage();
                            imageToShow = SwingFXUtils.toFXImage(bi, null);
                        }
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                // 500ms...
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 500, TimeUnit.MILLISECONDS);

                // update the button content
                this.CaptureBackground.setText("Stop Calibration");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.CaptureBackground.setText("Calibrate Background");

            // stop the timer
            this.stopAcquisition();
        }
    }



    @FXML
    protected void startCamera(ActionEvent event) {
        loop_processing = true;
        calibration_processing = false;
       

        if (!this.cameraActive) {
        	 System.out.println("Start Camera");
            //Main.popupStage.show();
            // start the video capture
        	System.out.println("Open cameraID: " + cameraId);
            this.capture.open(cameraId);
            System.out.println("initialiseCamera");
            initialiseCamera();
            counter = 0;

            // is the video stream available?
            if (this.capture.isOpened()) {
            	System.out.println("CaptureOpen");
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec) from 33 milisecond scheduler
                Runnable frameGrabber = new Runnable() {
                	
                    @Override
                    public void run() {
                        counter++;
                        updateTimers();
                        // LED Control
                        LEDControl();
                        grabFrame();
                        saveRawData(); // saves raw data
                        processData();
                        saveProcessedData(); // saves processed data 
                        // convert and show the frame
                        Image imageToShow;

                        if (OpenCVProcessing) {
                            imageToShow = Utils.mat2Image(ProcessedOCVframe);
                        }
                        else {
                            bi = ProcessedIJframe.getBufferedImage();
                            imageToShow = SwingFXUtils.toFXImage(bi, null);
                        }
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                // zero delay in reececution
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 0, TimeUnit.MILLISECONDS);


                // update the button content
                this.button.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.button.setText("Start Camera");

            // stop the timer
            this.stopAcquisition();
        }
    }

    private void updateTimers() {
        t1 = t0; // t1 = previous t0
        t0 = System.currentTimeMillis();

        deltaT = t0 - t1;
        //fSystem.out.println("frametime " + deltaT);
        FrameTime.setText( Long.toString(deltaT) + " ms ");
        fps = 1000/deltaT;
        FrameRate.setText(df.format(fps) + " fps ");
    }

    private void LEDControl() {
        /*if (numberOfLEDs >0) {
            try {
                switch (numberOfLEDs) {
                    case 1:
                        // write to R port 0;
                        niDaqSimpleDemo.writeDigitalOut(redLED);
                        break;
                    case 2:
                        if (counter % 2 == 0) {
                            niDaqSimpleDemo.writeDigitalOut(redLED);
                        } else {
                            niDaqSimpleDemo.writeDigitalOut(greenLED);
                        }
                        break;
                    case 3:
                        if (counter % 3 == 0) {
                            niDaqSimpleDemo.writeDigitalOut(redLED);
                        } else if (counter % 3 == 1) {
                            niDaqSimpleDemo.writeDigitalOut(greenLED);
                        } else {
                            niDaqSimpleDemo.writeDigitalOut(blueLED);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }*/
    }

    private void processData(){
        if (loop_processing){
            if (OpenCVProcessing) {
                ProcessedOCVframe = RawOCVframe;
                ProcessedOCVframe = openCV_ip.processImage(ProcessedOCVframe, counter);
            }
            else {
                ProcessedIJframe = RawIJframe;
                ProcessedIJframe = ImageJ_ip.processImage(ProcessedIJframe, counter);
            }
        } else if (calibration_processing){
                if (OpenCVProcessing) {
                    ProcessedOCVframe = RawOCVframe;
                    ProcessedOCVframe = openCVCalibration_ip.processImage(ProcessedOCVframe, counter);
                }
                else {
                    ProcessedIJframe = RawIJframe;
                    ProcessedIJframe = ImageJCalibration_ip.processImage(ProcessedIJframe, counter);
                }
        }

    }



    private void saveCalibrationData() {
        //String timedata = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(Calendar.getInstance().getTime());


        Imgcodecs.imwrite("C:\\AcquisitionData\\" + folder + "\\" + "BACKGROUND\\BACKGROUND_RAW.tiff", RawOCVframe);

        if(OpenCVProcessing){
            //System.out.println(OCVframe);
            Imgcodecs.imwrite("C:\\AcquisitionData\\" + folder + "\\" + "BACKGROUND\\BACKGROUND_OCV_PROCESSED.tiff", ProcessedOCVframe);
            //System.out.println("C:\\AcquisitionData\\" + folder + "\\" + "PROCESSED\\"+ timedata + "_OCV_PROCESSED.tiff");
        }
        else{
            if (ProcessedIJframe == null){
                System.out.println("IJFrame is null, ensure the image acquired is converted for IJ Processing");
            }
            else {
                FileSaver fs = new FileSaver(ProcessedIJframe);
                fs.saveAsTiff("C:\\AcquisitionData\\" + folder + "\\" + "BACKGROUND\\BACKGROUND_IJ_PROCESSED.tiff");
                //Save the image as raw data using the specified path.
            }
        }
    }


    private void saveRawData() {
        // See if we need to save raw data before processing it
        if (saveRawDatacheckBox.isSelected()) {
            // save raw data
            intSaveRawDataFrequencySlider = (int) saveRawDataFrequencySlider.getValue();
            if (counter % intSaveRawDataFrequencySlider == 0) {
                String timedata = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(Calendar.getInstance().getTime());
                // when counter == slider frequency
                Imgcodecs.imwrite("C:\\AcquisitionData\\" + folder + "\\" + "RAW\\" + timedata + "_RAW.tiff", RawOCVframe);
                //System.out.println("C:\\AcquisitionData\\" + folder + "\\" + "RAW\\" + timedata+"_RAW.tiff");
            }
        }
    }

    private void saveProcessedData() {
        if (saveProcessedDatacheckBox.isSelected()){
            String timedata = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(Calendar.getInstance().getTime());
            //save processed data
            if(OpenCVProcessing){
                //System.out.println(OCVframe);
                Imgcodecs.imwrite("C:\\AcquisitionData\\" + folder + "\\" + "PROCESSED\\" + timedata + "_OCV_PROCESSED.tiff", ProcessedOCVframe);
            //System.out.println("C:\\AcquisitionData\\" + folder + "\\" + "PROCESSED\\"+ timedata + "_OCV_PROCESSED.tiff");
            }
            else{
                if (ProcessedIJframe == null){
                    System.out.println("IJFrame is null, ensure the image acquired is converted for IJ Processing");
                }
                else {
                    FileSaver fs = new FileSaver(ProcessedIJframe);
                    fs.saveAsTiff("C:\\AcquisitionData\\" + folder + "\\" + "PROCESSED\\" + timedata + "_IJ_PROCESSED.tiff");
                    //Save the image as raw data using the specified path.
                }
            }
        }
    }

    private void createDirectories() {
        folder = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File outFile = new File("C:\\AcquisitionData\\"+folder + "\\RAW\\test.file");
        outFile.getParentFile().mkdirs();
        outFile = new File("C:\\AcquisitionData\\"+folder + "\\PROCESSED\\test.file");
        outFile.getParentFile().mkdirs();
        outFile = new File("C:\\AcquisitionData\\"+folder + "\\BACKGROUND\\test.file");
        outFile.getParentFile().mkdirs();
        String timedata = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(Calendar.getInstance().getTime());
        if (OCVbackground!=null) {
            Imgcodecs.imwrite("C:\\AcquisitionData\\" + folder + "\\" + "BACKGROUND\\" + timedata + "_BACKGROUND.tiff", OCVbackground);
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private void grabFrame() {
        // init everything

        String cameraSelection = CameraComboBox.getValue().toString();
        switch (cameraSelection) {
                case "File":
                    RawOCVframe = imread(filePath +"\\" + fileName + ".tif");
                    //RawOCVframe = imread("C:\\AcquisitionData\\bgrd.tif");
                    if (!RawOCVframe.empty()) {
                        Imgproc.cvtColor(RawOCVframe, RawOCVframe, Imgproc.COLOR_BGR2GRAY);
                    }
                    if (!OpenCVProcessing){
                        RawIJframe =  createIJframe(RawOCVframe);
                    }
                    break;
                case "WebCam":
                    // read the current frame
                	System.out.println("Grab Frame From Webcam");
                	System.out.println("Set Exposure");
                    this.capture.set(CAP_PROP_EXPOSURE, intCameraExposure);
                    System.out.println("Read");
                    this.capture.read(RawOCVframe);

                    // converts webcam to black and white
                    if (!RawOCVframe.empty()) {
                    	System.out.println("Empty");
                        Imgproc.cvtColor(RawOCVframe, RawOCVframe, Imgproc.COLOR_BGR2GRAY);
                    }
                    if (!OpenCVProcessing){
                    	System.out.println("Not empty");
                        RawIJframe =  createIJframe(RawOCVframe);
                    }
                    break;
                case "Grasshopper":
                    /*try {
                        // using mmcore to acquire from grasshopper
                        // camera been initialised to 8bit images
                        core.setExposure(intCameraExposure);
                        core.snapImage();
                        //System.out.println("Byte Per Pixel: " + core.getBytesPerPixel());

                        if (core.getBytesPerPixel() == 1) {
                            // 8-bit grayscale pixels
                            byte[] img = (byte[]) core.getImage();
                            //System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
                            int width = (int) core.getImageWidth();
                            int height = (int) core.getImageHeight();
                            RawOCVframe = new Mat(height, width, CV_8UC1);
                            RawOCVframe.put(0, 0, img);
                            // System.out.println("Pixel [0,0] value = " + img[0]);

                            // convert OPenCV image to ImageJ
                            if (!OpenCVProcessing){
                                RawIJframe = createIJframe(RawOCVframe);
                            }

                        } else if (core.getBytesPerPixel() == 2) {
                            // 16-bit grayscale pixels
                            short[] img = (short[]) core.getImage();
                            System.out.println("Hadle 16 bit images here");
                            //frame = new Mat(img, CV_16SC3);
                            //Mat (const std::vector< int > &sizes, int type, void *data, const size_t *steps=0)

                            //System.out.println("Pixel [0,0] value = " + img[0]);
                        } else {
                            System.out.println("Dont' know how to handle images with " +
                                   core.getBytesPerPixel() + " byte pixels.");
                        }

                    } catch (Exception e) {
                        System.out.println(e);
                    }*/
                    break;

                default:
                    System.out.println("No Camera");
                    break;
            }

/*            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }*/
        return;
    }

    private ImagePlus createIJframe(Mat fromMat) {
        bi = new BufferedImage(fromMat.width(), fromMat.height(), BufferedImage.TYPE_BYTE_GRAY);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        fromMat.get(0, 0, data);
        ImagePlus createIJFrame = new ImagePlus("",bi);
        return  createIJFrame;
    }


    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    public void updateImageView(Image imageToShow) {
        Utils.onFXThread( currentFrame.imageProperty(), imageToShow);
    }

    /*public static void updateImageView( Image image) {
        //ImageView view = currentFrame;
        Utils.onFXThread(currentFrame.imageProperty(), image);
    }*/

    /**
     * On application close, stop the acquisition from the camera
     */

    protected void setClosed() {
        this.stopAcquisition();
    }


    /*@FXML
    public void setCameraExposure(ActionEvent actionEvent){
        intCameraExposure = (int) cameraExposureSlider.getValue();
        cameraExposureText.setText(Integer.toString(intCameraExposure));
    }*/

    @FXML
    public void loadAlgorithm(ActionEvent actionEvent) {

        // Load file
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Java algorithm files (*.java)", "*.java");
        fileChooser.getExtensionFilters().add(extFilter);

        //Set to user directory or go to default if cannot access
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showOpenDialog(stage);
        //System.out.println(file);

        filePath = file.getParent();
        //System.out.println(filePath);

        fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        // ----------------------------------------------------------


        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        File loadedFile = new File("src//VersiLab/algorithms/"+fileName+".java");
        if (loadedFile.getParentFile().exists() || loadedFile.getParentFile().mkdirs()) {

            try {
                Writer writer = null;
                try {
                    writer = new FileWriter(loadedFile);
                    writer.write(stringBuilder.toString());
                    //System.out.println(stringBuilder.toString());
                    writer.flush();
                } finally {
                    try {
                        writer.close();
                    } catch (Exception e) {
                    }
                }



                /** Compilation Requirements *********************************************************************************************/
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                System.out.println("compiler: " + compiler);
                //System.setProperties("java.home", "Java Runtime 1.8.0_72");
                System.out.println("java.home: "+ System.getProperty("java.home"));
                //System.out.println("we make it here");
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                //StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                //System.out.println("do we make it here");
                // This sets up the class path that the compiler will use.
                // I've added the .jar file that contains the DoStuff interface within in it...
                List<String> optionList = new ArrayList<String>();
                optionList.add("-classpath");
                optionList.add(System.getProperty("java.class.path") + ";dist/VersiLab.jar");

                Iterable<? extends JavaFileObject> compilationUnit
                        = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(loadedFile));
                JavaCompiler.CompilationTask task = compiler.getTask(
                        null,
                        fileManager,
                        diagnostics,
                        optionList,
                        null,
                        compilationUnit);
                /********************************************************************************************* Compilation Requirements **/
                if (task.call()) {
                    /** Load and execute *************************************************************************************************/
                    // Create a new custom class loader, pointing to the directory that contains the compiled
                    // classes, this should point to the top of the package structure!
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
                    // Load the class from the classloader by name....
                    Class<?> loadedClass = classLoader.loadClass("com.Proteus.algorithms." + fileName);
                    // Create a new instance...
                    Object obj = loadedClass.newInstance();
                    // Santity check
                    if (obj instanceof OpenCVImageProcess) {
                        // Cast to the DoStuff interface
                        openCV_ip = (OpenCVImageProcess) obj;
                        OpenCVProcessing = true;
                    } else if (obj instanceof ImageJImageProcess) {
                        // Cast to the DoStuff interface
                        ImageJ_ip = (ImageJImageProcess) obj;
                        OpenCVProcessing = false;
                    }
                    else {
                        System.out.println("!!!!!---------- UNKNOWN c OBJECT ------------!!!!!!!!!");
                    }
                    /************************************************************************************************* Load and execute **/
                } else {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        System.out.format("Error on line %d in %s%n",
                                diagnostic.getLineNumber(),
                                diagnostic.getSource().toUri());
                    }
                }
                fileManager.close();
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
                exp.printStackTrace();
            }

            // ------------------------------------------------------------


            try {
                // startup for popop
                //make another stage for scene2
                popupStage = new Stage();
                //popupStage.setScene(scene2);
                // tell stage it is meant to pop-up (Modal)
                // Not popupStage.initModality(Modality.APPLICATION_MODAL);
                // Modality.APPLICATION_MODAL doesnt allow switching between stages


                popupStage.initModality(Modality.NONE);

                // load the FXML resource
                popupLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
                // store the root element so that the controllers can use it
                popupRootElement = (BorderPane) popupLoader.load();
                // create and style a scene
                popupScene = new Scene(popupRootElement, 500, 500);

                popupScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                // create the stage with the given title and the previously created
                // scene
                popupStage.setTitle(fileName);

                popupStage.setScene(popupScene);
                // show the GUI
                //popupStage.show();


                // set the proper behavior on closing the application
                popupController = popupLoader.getController();

                if (openCV_ip != null) {
                    openCV_ip.initialise();
                } else if (ImageJ_ip != null) {
                    ImageJ_ip.initialise();
                }

                popupStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        popupController.setClosed();
                    }
                }));


                // show the GUI
                popupStage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    public void addPopupSlider(String name, double min, double max, double value, double step){
        popupController.addSlider(name, min, max, value, step);
    }
    public void clearPopupSliders(){
    	//System.out.println("clearpopupSlider");
        popupController.clearSlider();
    }



    public static interface ImageJImageProcess{

        public ImagePlus processImage(ImagePlus imageIn, int counter);
        public void initialise();
    }

    public static interface OpenCVImageProcess{

        public Mat processImage(Mat imageIn, int counter);
        public void initialise();
    }
    private void initialiseCamera() {
        //Main.popupStage.show();
        // start the video capture
        this.capture.open(cameraId);

        // is the video stream available?
        if (this.capture.isOpened()) {
            this.cameraActive = true;
        }

        String cameraSelection = CameraComboBox.getValue().toString();
        byte[] data;
        Image imageToShow;

        // Which camera is selected in Camera Selecction box

        switch (cameraSelection) {
            case "WebCam":

                break;
            case "Grasshopper":
                    /*try {
                        core.loadDevice("Camera", "PointGrey", "Grasshopper3 GS3-U3-41C6NIR-C_15123105");
                        //core.loadDevice("LED", "NI100X", "DigitalIO");
                        core.initializeAllDevices();
                        core.setProperty("Camera", "PixelType", "8-bit");
                        //core.setProperty("Camerea", );
                        core.setExposure(intCameraExposure);
                        StrVector properties = core.getDevicePropertyNames("Camera");
                        for (int i = 0; i < properties.size(); i++) {
                            String prop = properties.get(i);
                            String val = core.getProperty("Camera", prop);
                            System.out.println("Name: " + prop + ", value: " + val);
                        }
                        init = false;
                StrVector LEDproperties = core.getDevicePropertyNames("LED");
                for (int i = 0; i < LEDproperties.size(); i++) {
                    String prop = LEDproperties.get(i);
                    String val = core.getProperty("Camera", prop);
                    System.out.println("Name: " + prop + ", value: " + val);
                }

                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage() + "\nExiting now.");
                        System.exit(1);
                    }*/
                    break;
            default:
                break;
        }


    }
    
    public void setContext(Context context) {
        context.inject(this);
    }
    
}


