package com.library.main;

import javafx.application.Application;
import javafx.stage.Stage;
import com.library.util.DBUtil;
import com.library.ui.LoginUI;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        DBUtil.initializeDatabase();
        LoginUI loginUI = new LoginUI();
        loginUI.showLoginWindow(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
