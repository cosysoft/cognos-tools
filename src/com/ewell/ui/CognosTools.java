package com.ewell.ui;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.launch.Skeleton;
import com.ewell.ui.task.ContentTreeService;

/**
 * 
 * @author comfort
 * 
 */
public class CognosTools extends Application implements Skeleton {

	private Window mainWindow;
	private TreeItem<BaseClass> root;
	private TreeView<BaseClass> contentNav;
	private SplitPane mainPane;

	private Pane parent;
	private Scene mainScene;

	public static Skeleton skeleton;

	@Override
	public void start(Stage primaryStage) throws Exception {

		skeleton = this;
		parent = (Pane) FXMLLoader.load(CognosTools.class
				.getResource("App.fxml"));
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

		contentNav = (TreeView<BaseClass>) parent.lookup("#contentNav");

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
	public TreeItem<BaseClass> getContentTree() {
		return root;
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
	public TreeView<BaseClass> getContentNav() {
		return contentNav;
	}

	@Override
	public SplitPane getMainPane() {
		return mainPane;
	}

}