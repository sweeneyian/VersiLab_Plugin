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
package com.Proteus.gui;

import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//import net.imagej.ImageJ;
import javafx.scene.layout.BorderPane;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import net.imagej.ImageJ;

import com.Proteus.gui.view.VersiLabController;
import com.Proteus.imagej.VersiLab_Plugin;

import ij.WindowManager; 


import org.scijava.Contextual;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 * This class is called from the ImageJ plugin.
 * 
 * contributor Hadrien Mary
 * @author Ian Sweeney
 */
public class MainAppFrame extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Parameter
    private LogService log;

    private ImageJ ij;

    private JFXPanel fxPanel;
    
    private JFileChooser chooser;
    

	

    public MainAppFrame(ImageJ ij) {

    	String OpenCVPath="";
    	chooser = new JFileChooser(); 
    	
        if (OpenCVPath==""){
        chooser.setDialogTitle("Please select your systems opencv_java320.dll file in OpenCV->build->java->");
        
	        //    
	        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	        	OpenCVPath = chooser.getSelectedFile().toString();
	        }
        }
        try{
        	System.load(OpenCVPath );
        	System.out.println("OPENCV LOADED");}
        catch(Exception e){
        	System.out.println(e);
        	 System.out.println("OPENCV NOT Loaded");
        }

        ((Contextual) ij).context().inject(this);
        this.ij = ij;
    }
    
    public java.awt.Window getWindow(){
    	return WindowManager.getActiveWindow();
    }

    /**
     * Create the JFXPanel that make the link between Swing (IJ) and JavaFX plugin.
     */
    public void init() {
        this.fxPanel = new JFXPanel();
        this.add(this.fxPanel);
        this.setVisible(true);
        
        // The call to runLater() avoid a mix between JavaFX thread and Swing thread.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });

    }

    public void initFX(JFXPanel fxPanel) {
        // Init the root layout
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/com/Proteus/gui/view/VersiLabRootLayout.fxml"));
            BorderPane rootLayout = (BorderPane) loader.load();
          
            // Get the controller and add an ImageJ context to it.
            // need to figure out what context is doing exactly 
            VersiLabController controller = loader.getController();
            controller.setContext(((Contextual) ij).context());
            VersiLab_Plugin.setVeriLabPluginController(controller);
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            this.fxPanel.setScene(scene);
            this.fxPanel.setVisible(true);

            // Resize the JFrame to the JavaFX scene
            this.setSize((int) scene.getWidth()+50, (int) scene.getHeight()+50);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
