package com.ewell.ui.view;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import com.ibm.cognos.CRNConnect;
import com.ibm.cognos.Logon;

public class Login {

	public static Logon logon = new Logon();

	public static CRNConnect connect;

	@FXML
	private HBox nsPane;

	@FXML
	private PasswordField passwordInput;

	@FXML
	private TextField userNameInput;

	@FXML
	void initialize() {

		connect = new CRNConnect();
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
	public void cancel() {
		System.exit(0);
	}

}
