package com.ewell.ui.test;
import java.net.URL;

import javafx.fxml.FXML;


public class ViewController{
	
    public ViewController() {
		super();
	}

    @FXML
    private URL location;


    @FXML
    void initialize() {

    	System.out.println(location);

    }

}
