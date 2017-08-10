# VersiLab Plugin

`VersiLab_Plugin` is early development targeted towards pre-clinical 
optical endo-microscopy (OEM). It is a plugin using JavaFX GUI, can acquire images from webcam, allows the processing of images in real-time from selection of drop down processing algorithms, and control of National Instruments Digtal Module for controlling LEDS.

#Requirements: 

##OpenCV:

The instalation of OpenCV 3.2 for webcam control. On startup a FileChooser popup will request 'opencv_java320.dll' filepath. typically '\opencv\build\java\x64\opencv_java320.dll' for 64-bit machines. http://opencv.org/opencv-3-2.html



##National Instruments LED's

Versilab plugin is Tested with NI-9402 C Series Digital Module and port LEDS. The instalation of 'NI-DAQmx 17.0.0' from http://www.ni.com/nisearch/app/main/p/bot/no/ap/tech/lang/en/pg/1/sn/catnav:du,n8:3478.41.181.5495,ssnav:ndr/
and using NI Max to name the Digital Module as 'cDAQ1'



# New Image Processing Algorithms

package com.Proteus.algorithms contains Image Processing algorithms.

'CoreDectionOCV' is during the background calibration phase

'CorePopulationOCV' uses the results of cores detected in 'CoreDetectionOCV' to draw circles in the starting of camera image stream.

To implement a new image processing algorithm called 'NewAlgorithm' 

create a  NewAlgorithm.java file in com.Proteus.algorithms
	
	
	
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
}
		



#Links:

This is ImageJ plugin built from combination of ImageJ guides

https://imagej.net/Developing_Plugins_for_ImageJ_1.x
http://imagej.net/Build_ImageJ2_Plugin_With_JavaFX
https://imagej.net/Introduction_into_Developing_Plugins
https://imagej.net/Writing_plugins
https://imagej.net/Distribution

More details for Proteus and OEM Imaging can be found at: 

https://proteus.ac.uk/

## Authors

- [Ian Sweeney](mailto:sweeney.ian@gmail.com).

## License

MIT. See [LICENSE.txt](LICENSE.txt).