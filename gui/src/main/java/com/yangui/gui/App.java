package com.yangui.gui;

import com.sun.javafx.application.LauncherImpl;
import com.yangui.main.MainView;
import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class App extends Application implements SharedScene {
	
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
	/** Initial size of scene */
	public static final int SCENE_WIDTH = 1800;
	public static final int SCENE_HEIGHT = 1100;
	
	
	/** Initial size of import files dialog */
	public static final int IMPORT_DLG_WIDTH = 1400;
	public static final int IMPORT_DLG_HEIGHT = 800;
	
	
	/** Initial size of query wizard dialog */
	public static final int QUERYWIZ_DLG_WIDTH = 1300;
	public static final int QUERYWIZ_DLG_HEIGHT = 800;
	
	
	/** Initial size of xpath selector dialog */
	public static final int XPATHSEL_DLG_WIDTH = 1300;
	public static final int XPATHSEL_DLG_HEIGHT = 800;
	
	/** Number of threads used by Executor to execute business tasks*/
	public static final int THREAD_POOL_SIZE = 1;
	
	/** Line size displayed in the Log Window*/
	public static final int CONSOLE_LINE_BUFFER_SIZE = 500;
	
	public static Stage MAIN_STAGE;
	
	public static HostServices WEB_BROWSER;
	
	private Parent parentNode;
	
	
	public static void main(String[] args) {
		Conf.init(args);//initialize configuration
		LauncherImpl.launchApplication(App.class, AppPreloader.class, args);
	}


	@Override
	public Parent getParentNode() {
		return parentNode;
	}
	
	
	@Override
    public void init() throws Exception {//code is executed on JavaFX Launcher Thread
        super.init();
        MainView appView = new MainView();
        parentNode = appView.getView();
    }

	
    @Override
    public void start(Stage stage) throws Exception {//code moved to AppPreloader
    }

    
    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }
}
