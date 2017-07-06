package com.Proteus;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.frame.*;

public class Plugin_Frame extends PlugInFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Plugin_Frame() {
		super("Plugin_Frame");
		TextArea ta = new TextArea(15, 50);
		add(ta);
		pack();
		GUI.center(this);
		setVisible(true);
	}

}
