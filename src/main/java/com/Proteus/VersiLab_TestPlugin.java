package com.Proteus;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class VersiLab_TestPlugin implements PlugInFilter {

	@Override
	public void run(ImageProcessor arg0) {
		// TODO Auto-generated method stub
		IJ.log("Hello, World!");
	}

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		IJ.showMessage("Hello, World!");
		// TODO Auto-generated method stub
		return 0;
	}

}
