package com.ewell.ui.view;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.ewell.ui.BiBusHelper;
import com.ewell.ui.CognosTools;
import com.ewell.ui.util.FXMLLoaderUtil;
import com.ibm.cognos.CRNConnect;
import com.ibm.cognos.Logon;

public class Login  {
	
	private Logon logon = new Logon();

	@FXML
	private HBox nsPane;

	@FXML
	private PasswordField passwordInput;

	@FXML
	private TextField userNameInput;

	@FXML
	void initialize() {

		CRNConnect connect = BiBusHelper.getInstance().getConnect();
		connect.connectToCognosServer();
		String[] ns = logon.getNamespaces(connect);

		String namespaces[] = new String[ns.length / 2];
		String namespaceIDs[] = new String[ns.length / 2];

		for (int j = 0, k = 0; k < ns.length; j++, k++) {
			namespaces[j] = ns[k++];
			namespaceIDs[j] = ns[k];
		}

		ToggleGroup toggleGroup = new ToggleGroup();

		for (int i = 0; i < namespaceIDs.length; i++) {
			RadioButton t = new RadioButton(namespaceIDs[i]);
			t.setToggleGroup(toggleGroup);
			t.setUserData(namespaceIDs[i]);
			nsPane.getChildren().add(t);
			if (i == 0) {
				toggleGroup.selectToggle(t);
			}
		}

	}


	@FXML
	public void cancel(){
		System.exit(0);
	}
	
	public void start(Stage loginStage) throws Exception {
		Pane login = (Pane) FXMLLoaderUtil.loadConvention(Login.class);
		Scene loginScene = new Scene(login);

		loginScene.getStylesheets().add(
				CognosTools.class.getResource("app.css").toExternalForm());
		loginStage.setScene(loginScene);

		loginStage.getIcons().addAll(
				new Image(CognosTools.class.getResource("cognos.jpg")
						.openStream()));
		loginStage.setTitle("Cognos-toos 登录");

		loginStage.show();
	}
	
	
}
