# VersiLab Plugin

`VersiLab_Plugin` is early development targeted towards pre-clinical optical endo-microscopy (OEM). It runs as an ImageJ/Fiji plugin using a JavaFX GUI, can acquire images from webcam, allows the processing of images in real-time from selection of drop down processing algorithms, and control of National Instruments Digtal Module for controlling LEDS.

## Snapshot of Versilab detecting cores:
![versilab snapshot](https://github.com/sweeneyian/VersiLab_Plugin/blob/master/src/main/resources/versilab%20snapshot.png?raw=true)


## Snapshot of Versilab blurring real-time webcam images:
![blur](https://github.com/sweeneyian/VersiLab_Plugin/blob/master/src/main/resources/blur.png?raw=true)

# Requirements to run plugin through ImageJ: 

download and install `Fiji` from http://imagej.net/Fiji/Downloads

download and install `OpenCV 3.2` from http://opencv.org/opencv-3-2.html

run the Fiji / ImageJ application 

within (Fiji Is Just) ImageJ select `Help > Update`

once updated, restart application and again select  `Help > Update` then `Manage update sites`

Scroll and select `VersiLab_plugins`, Close, Apply changes, restart application

Select `Plugins > VersiLab_Plugin`

On startup a FileChooser popup will request `opencv_java320.dll` filepath. typically `\opencv\build\java\x64\opencv_java320.dll` for 64-bit machines.


# Requirements to develop plugin: 

## Note

Successful rebuilds have been witnessed on Windows 10
Windows 8 failed build and pointed to errors with parent in pom.xml

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

## Eclipse:

Download and install `Eclipse` from http://www.eclipse.org/

Clone the Versilab project from https://github.com/sweeneyian/VersiLab_Plugin and unzip to the working directory of Eclipse.

import > existing Maven Project > (the pom.xml of the Versilab clone)

### Eclipse Run configurations:


#### Add new `Maven Build`

select workspace: `${workspace_loc:/Versilab_Plugin}`

add Parameter: name=`imagej.app.directory` value=`{path to ImageJ or Fiji directory}`

Running `Maven build` will have more enforcements than running `Java Application` and will ensure outputs are ImageJ compatible and ready for distribution to ImageJ update sites.



#### Add new `Java Application`. 

Select Project: `Versilab_Plugin`

Main class: `com.Proteus.imagej.Versilab_Plugin`

Running this Java application will use Maven to download the projects dependencies and store them in a `.m2` folder like `C:\Users\Owner\.m2\repository`. 
Note: Running the `Java Application` will bypass some Maven enforcement rules, which can be helpful for local development testing but May cause issues if trying to upload jar updates that fail Maven enforcement rules.



## OpenCV:

download and install `OpenCV`
http://opencv.org/opencv-3-2.html


The instalation of OpenCV 3.2 for webcam control. On startup a FileChooser popup will request `opencv_java320.dll` filepath. typically `\opencv\build\java\x64\opencv_java320.dll` for 64-bit machines. 




## National Instruments LED's

Versilab_Plugin tested with `NI-9402 C Series Digital Module` for LED control. 

Install `NI-DAQmx 17.0.0` from 
http://www.ni.com/nisearch/app/main/p/bot/no/ap/tech/lang/en/pg/1/sn/catnav:du,n8:3478.41.181.5495,ssnav:ndr/

Then using `NI Max` to `name` the Digital Module as `cDAQ1` 



# New Image Processing Algorithms

`package com.Proteus.algorithms` contains Image Processing algorithms.

`CoreDectionOCV` is during the background calibration phase

`CorePopulationOCV` uses the results of cores detected in `CoreDetectionOCV` to draw circles in the starting of camera image stream.

To implement a new image processing algorithm called `NewAlgorithm` that gaussian  blurs the image stream create a  `NewAlgorithm.java` file in `com.Proteus.algorithms`

	
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
		VersiLabController mainController = Versilab_Plugin.getVersiLabPluginController();
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

		
And add the name `NewAlgorithm` to  `VersiLabRootLayout.fxml` in `com.Proteus.gui.view` to an algorithm drop down menu

	<ComboBox fx:id="AlgorithmComboBox" prefWidth="170.0" promptText="Loop Algorithm" GridPane.columnIndex="0" GridPane.rowIndex="14">
	                <items>
	                    <FXCollections fx:factory="observableArrayList">
	                        
	                        <String fx:value="NewAlgorithm"/>
	                        
	                        <String fx:value="CorePopulationOCV"/>
	                        <String fx:value="TestImageJ" />
	                        <String fx:value="TestOpenCV" />
	                    </FXCollections>
	                </items>
	            </ComboBox>


# Links:

Versilab is an ImageJ plugin built from combination of ImageJ guides

https://imagej.net/Developing_Plugins_for_ImageJ_1.x

http://imagej.net/Build_ImageJ2_Plugin_With_JavaFX

https://imagej.net/Introduction_into_Developing_Plugins

https://imagej.net/Writing_plugins

https://imagej.net/Distribution


More details for Proteus and OEM Imaging can be found at: 

https://proteus.ac.uk/

https://proteus.ac.uk/technology/mcwffm/


## Next To Do

Native OpenCV dll files get imported during runtime using `FileChooser` to specify a local directory. OpenCV can then be used to acquiring images from Webcam. It is possible that a similar method would work with `Bytedeco`, which shows the most promise for implementing universal hardware controls of specialised cameras, such as Grasshopper from FlyCapture2 dll files, which would be the next line of investigation and integration for having a modular approach to hardware for real-time image acquisition and processing.

http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22flycapture-platform%22

http://bytedeco.org/


Memory management wiith Java and real-time image processing requires attention.


## Author

- [Ian Sweeney](mailto:sweeney.ian@gmail.com) (mailto:sweeney.ian@gmail.com)

## Project Handed over to 

- [Antonios Perperidis](mailto:A.Perperidis@hw.ac.uk) (mailto:A.Perperidis@hw.ac.uk)


## License

MIT. See [LICENSE.txt](LICENSE.txt).