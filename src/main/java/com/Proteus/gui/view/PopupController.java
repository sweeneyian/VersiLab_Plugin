package com.Proteus.gui.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.Initializable;

import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import javafx.fxml.FXML;


import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by Owner on 28/05/2017.
 */
public class PopupController implements Initializable {

    @FXML
    private ListView<Slider> visibleList;
    @FXML
    private ListView<Text> visibleValues;
    @FXML
    private ListView<Text> visibleNames;

    private static ObservableList<Slider> sliders =
            FXCollections.observableArrayList();

    private ObservableList<Text> texts =
            FXCollections.observableArrayList();

    private ObservableList<Text> names =
            FXCollections.observableArrayList();


    @FXML
    public void addSlider(String name, double min, double max, double value, double step) {

        Slider s = new Slider(min, max, value);
        s.setBlockIncrement(step);
        sliders.add(s);

        Text n = new Text(name);
        names.add(n);

        Text t = new Text();
        texts.add(t);
        s.valueProperty().addListener((ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) -> {
                t.setText(String.valueOf( new_val.intValue()));
            //String.valueOf((int) saveRawDataFrequencySlider.getValue())
        });
    }

    @FXML
    public void clearSlider() {
      //  sliders.removeAll();
        sliders.clear();
      //  texts.removeAll();
        texts.clear();
      //  names.removeAll();
        names.clear();

    }

    public void setClosed()
    {
        System.out.println("close popup");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        visibleList.setItems(sliders);
        visibleValues.setItems(texts);
        visibleNames.setItems(names);
    }

    public static double getSliderValue(int slider_index){
        //index from zero
        double slider_value = sliders.get(slider_index).getValue();

        return slider_value;
    }
}
    /*@FXML
    private ListView<Slider> visibleList;

    @FXML
    private ListView<Text> visibleValues;

    private ObservableList<Slider> sliders =
            FXCollections.observableArrayList();
    //private ObservableList<Text> texts =
    //        FXCollections.observableArrayList();

    @FXML
    public void addSlider(double min, double max, double value, double step) {

        Slider s = new Slider(min, max, value);
        s.setBlockIncrement(step);
        sliders.add(s);

        Text t = new Text();
        s.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                t.setText(new_val.toString());
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("initialise popup");
       // addSlider(0,1,1,1);
        visibleList.setItems(sliders);
        //visibleList.setItems(texts);
    }

    public void setClosed()
    {
        System.out.println("close popup");
    }*/


