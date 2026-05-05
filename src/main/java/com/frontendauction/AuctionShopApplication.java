package com.frontendauction;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AuctionShopApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                        AuctionShopApplication.class.getResource("/com/frontendauction/login.fxml"),
                        "login.fxml not found"
                )
        );

        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setTitle("Auction Shop");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
