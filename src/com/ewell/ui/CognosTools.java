package com.ewell.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import com.ewell.cognos.content.CMFacade;
import com.ewell.cognos.content.CMFacadeImpl;
import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.cell.CheckBoxContentCell;
import com.ewell.ui.launch.Skeleton;
import com.ewell.ui.task.ContentTreeService;
import com.ewell.ui.util.CMUtil;
import com.ewell.ui.util.FXMLLoaderUtil;
import com.ewell.ui.view.App;
import com.ewell.ui.view.Login;
import com.ibm.cognos.CRNConnect;
import com.ibm.cognos.Logon;

/**
 * 
 * @author comfort
 * 
 */
public class CognosTools extends Application implements Skeleton {

	private Window mainWindow;
	private ContentItem root;
	private TreeView<ContentItem> contentNav;
	private SplitPane mainPane;

	private CMFacade cmFacade;
	private CRNConnect connect;

	private Pane parent;
	private Scene mainScene;

	public static Skeleton skeleton;

	@Override
	public void start(final Stage primaryStage) throws Exception {

		Pane login = (Pane) FXMLLoaderUtil.loadConvention(Login.class);
		Scene loginScene = new Scene(login);

		loginScene.getStylesheets().add(
				CognosTools.class.getResource("app.css").toExternalForm());

		final Stage loginStage = new Stage();
		loginStage.setScene(loginScene);

		loginStage.getIcons().addAll(
				new Image(CognosTools.class.getResource("cognos.jpg")
						.openStream()));
		loginStage.setTitle("Cognos-toos 登录");
		loginStage.setResizable(false);
		loginStage.show();

		Button loginBtn = (Button) loginScene.lookup("#login");

		loginBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					connect = Login.connect;
					Logon sessionLogon = Login.logon;
					while (!Logon.loggedIn(connect)) {
						sessionLogon.logon(connect);
					}

					cmFacade = new CMFacadeImpl(connect);
					loginStage.close();
					showMain(primaryStage);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void showMain(Stage primaryStage) throws IOException {
		skeleton = this;
		parent = (Pane) FXMLLoaderUtil.loadConvention(App.class);
		Scene myScene = new Scene(parent);

		myScene.getStylesheets().add(
				CognosTools.class.getResource("app.css").toExternalForm());
		primaryStage.setScene(myScene);

		maximize(primaryStage);

		primaryStage.getIcons().addAll(
				new Image(CognosTools.class.getResource("cognos.jpg")
						.openStream()));
		primaryStage.setTitle("Cognos-toos");

		mainWindow = primaryStage;
		mainScene = myScene;

		primaryStage.show();

		contentNav = (TreeView<ContentItem>) parent.lookup("#contentNav");

		mainPane = (SplitPane) parent.lookup("#mainPane");
		mainPane.getItems().remove(1);

		// TabPane tp = (TabPane) parent.lookup("#mainTab");
		System.out.println(contentNav);

		final ContentTreeService ctService = new ContentTreeService();
		ctService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				contentNav.setRoot(ctService.getValue());
				contentNav.getRoot().setExpanded(true);
			}
		});
		ctService.start();
	}

	private void maximize(Stage primaryStage) {
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

		primaryStage.setX(bounds.getMinX());
		primaryStage.setY(bounds.getMinY());
		primaryStage.setWidth(bounds.getWidth());
		primaryStage.setHeight(bounds.getHeight());
	}

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public Window getMainWindow() {
		return mainWindow;
	}

	@Override
	public Scene getMainScene() {
		return mainScene;
	}

	@Override
	public TreeView<ContentItem> getContentNav() {
		return contentNav;
	}

	@Override
	public SplitPane getMainPane() {
		return mainPane;
	}

	@Override
	public CMFacade getCMFacade() {
		return cmFacade;
	}

	@Override
	public CRNConnect getConnect() {
		return connect;
	}

	@Override
	public ContentItem getRootContent() {
		if (root == null) {
			root = cmFacade.buildContentItem();
		}
		return root;
	}

	@Override
	public TreeView<ContentItem> getChoiceView() {
		ContentItem root = this.getRootContent();
		TreeItem<ContentItem> ti = CMUtil.createNode(root);
		TreeView<ContentItem> ts = new TreeView<ContentItem>(ti);
		ts.setCellFactory(new Callback<TreeView<ContentItem>, TreeCell<ContentItem>>() {

			@Override
			public TreeCell<ContentItem> call(TreeView<ContentItem> arg0) {
				return new CheckBoxContentCell();
			}
		});
		return ts;
	}

}