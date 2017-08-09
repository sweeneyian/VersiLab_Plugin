# VersiLab Plugin

`VersiLab_Plugin` is early development targeted towards pre-clinical 
optical endo-microscopy (OEM). It is a plugin using JavaFX GUI, can acquire images from webcam, allows the processing of images in real-time from selection of drop down processing algorithms, and control of National Instruments Digtal Module for controlling LEDS.

Requirements: 
The instalation of OpenCV 3.2 for webcam control. On startup a FileChooser popup will request 'opencv_java320.dll' filepath. typically '\opencv\build\java\x64\opencv_java320.dll' for 64-bit machines. http://opencv.org/opencv-3-2.html
Tested with NI-9402 C Series Digital Module and port LEDS. The instalation of 'NI-DAQmx 17.0.0' from http://www.ni.com/nisearch/app/main/p/bot/no/ap/tech/lang/en/pg/1/sn/catnav:du,n8:3478.41.181.5495,ssnav:ndr/
and using NI Max to name the Digital Module as 'cDAQ1'


This is ImageJ plugin built from combination of ImageJ guides
https://imagej.net/Developing_Plugins_for_ImageJ_1.x
http://imagej.net/Build_ImageJ2_Plugin_With_JavaFX
https://imagej.net/Introduction_into_Developing_Plugins
https://imagej.net/Writing_plugins
https://imagej.net/Distribution


More details for Proteus and OEM Imaging can be found at https://proteus.ac.uk/

## Authors

- [Ian Sweeney](mailto:sweeney.ian@gmail.com).

## License

MIT. See [LICENSE.txt](LICENSE.txt).