package com.ewell.ui.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public final class FXMLLoaderUtil {

	public static Pane load(URL fxml, Object controller) {
		FXMLLoader fxmlLoader = new FXMLLoader(fxml);
		fxmlLoader.setController(controller);
		Pane v = null;
		try {
			v = (Pane) fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return v;
	}

	public static Pane loadConvention(Class<?> v) {
		try {
			return FXMLLoader.load(v.getResource(v.getSimpleName() + ".fxml"));
		} catch (IOException e) {
			throw new RuntimeException();
		}

	}
}
