# VersiLab Plugin

`VersiLab_Plugin` is early development targeted towards pre-clinical 
optical endo-microscopy (OEM). It is a plugin using JavaFX GUI, can acquire images from webcam, allows the processing of images in real-time from selection of drop down processing algorithms, and control of National Instruments Digtal Module for controlling LEDS.

Snapshot of Versilab detecting cores

https://github.com/sweeneyian/VersiLab_Plugin/blob/master/src/main/resources/versilab%20snapshot.png?raw=true

![versilab snapshot](https://raw.github.com/{sweeneyian}/{VersiLab_Plugin}/{1.0.3-SNAPSHOT}/{src/main/resources/})

# Requirements: 

## Java JDK 1.8

install `JDK 1.8` from
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
Ensure `JAVA_HOME` environment variable is set and points to your JDK installation

## Maven

https://maven.apache.org/download.cgi

and follow installation instructions

https://maven.apache.org/install.html

Extract distribution archive in any directory

	unzip apache-maven-3.5.0-bin.zip 

Add the `bin` directory of the created directory apache-maven-3.5.0 to the `PATH` environment variable


## OpenCV:

download and install `OpenCV`
http://opencv.org/opencv-3-2.html


The instalation of OpenCV 3.2 for webcam control. On startup a FileChooser popup will request 'opencv_java320.dll' filepath. typically '\opencv\build\java\x64\opencv_java320.dll' for 64-bit machines. 




## National Instruments LED's

Versilab plugin is Tested with NI-9402 C Series Digital Module and port LEDS. 

Install 'NI-DAQmx 17.0.0' from 
http://www.ni.com/nisearch/app/main/p/bot/no/ap/tech/lang/en/pg/1/sn/catnav:du,n8:3478.41.181.5495,ssnav:ndr/

Then using `NI Max` to `name` the Digital Module as `cDAQ1` 



# New Image Processing Algorithms

package com.Proteus.algorithms contains Image Processing algorithms.

`CoreDectionOCV` is during the background calibration phase

`CorePopulationOCV` uses the results of cores detected in `CoreDetectionOCV` to draw circles in the starting of camera image stream.

To implement a new image processing algorithm called `NewAlgorithm` that gaussian  blurs the image stream

create a  `NewAlgorithm.java` file in `com.Proteus.algorithms`

	
	package com.Proteus.algorithms;
	
	import com.Proteus.gui.view.PopupController;
	import com.Proteus.gui.view.VersiLabController;
	import com.Proteus.imagej.Versilab_Plugin;
	import org.opencv.core.Mat;
	import org.opencv.core.Size;
	import org.opencv.imgproc.Imgproc;
	import static org.opencv.core.Core.BORDER_DEFAULT;
	import static org.opencv.imgproc.Imgproc.*; 
	
	public class NewAlgorithm implements VersiLabController.OpenCVImageProcess {
	
	public void initialise(){
	
		// this is how we initialise initialise popup sliders
		VersiLabController mainController = 		Versilab_Plugin.getVersiLabPluginController();
		mainController.addPopupSlider("Sigma", 0,20,1,0.1);
		
	}
	
	
	public Mat processImage(Mat imageIn, int counter) {

		Size size = new Size(401, 401);
		// get value from slider
		double sigma = PopupController.getSliderValue(0); 
		// Do blurring based on slider value
		Imgproc.GaussianBlur(imageIn, imageIn, size, sigma, sigma, BORDER_DEFAULT);
			
			
		return imageIn;
    }

		

# Links:

This is ImageJ plugin built from combination of ImageJ guides

https://imagej.net/Developing_Plugins_for_ImageJ_1.x

http://imagej.net/Build_ImageJ2_Plugin_With_JavaFX

https://imagej.net/Introduction_into_Developing_Plugins

https://imagej.net/Writing_plugins

https://imagej.net/Distribution


More details for Proteus and OEM Imaging can be found at: 

https://proteus.ac.uk/

## To Do

OpenCV dll files get imported during runtime 'FileChooser' for acquiring images from Webcam.

`Bytedeco` shows the most promise for implementing universal hardware controls of specialised cameras. such as Grasshopper from FlyCapture2 dll files. This would be the next line of investigation and integration.

http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22flycapture-platform%22

http://bytedeco.org/



## Author

- [Ian Sweeney](mailto:sweeney.ian@gmail.com)


## Project Handed over to 

- [Antonios Perperidis](mailto:A.Perperidis@hw.ac.uk)


## License

MIT. See [LICENSE.txt](LICENSE.txt).