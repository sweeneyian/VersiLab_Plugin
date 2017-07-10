package com.Proteus.algorithms;

import java.io.File;

public class CVLoader {
    public static void load() {
        System.load(new File("opencv_java320.dll").getAbsolutePath());
    }
}
