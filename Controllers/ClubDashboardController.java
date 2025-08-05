package controller;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.CreateFXML;
import model.Session;

import java.io.IOException;

public class ClubDashboardController {
    @FXML private Label welcomeLabel;

    @FXML
    public void handleSearchPlayer(ActionEvent event) throws IOException {
        Main.setRoot("SearchPlayer.fxml");
    }

    @FXML
    public void handlePlayers(ActionEvent event) throws IOException {
        Main.setRoot("Players.fxml");
    }

    @FXML
    public void handleTeams(ActionEvent event) throws IOException {
        Main.setRoot("SearchClub.fxml");
    }

    @FXML
    public void handleLiveAuction(ActionEvent event) throws IOException {
        Main.setRoot("clubAuction.fxml");
    }

    @FXML
    public void handlePreviousAuctions(ActionEvent event) throws IOException {
        Main.setRoot("PreviousAuctions.fxml");
    }

    @FXML
    public void handleAboutUs(ActionEvent event) throws IOException {
        Main.setRoot("AboutUs.fxml");
    }

    @FXML
    public void handleLogOut(ActionEvent actionEvent) {
        Main.setRoot("LoginPage.fxml");
    }

    public void handleProfile(ActionEvent actionEvent) {
        CreateFXML.createClubProfileFXML(Session.getUsername());
        Main.setRoot("ClubProfile.fxml");
    }

    public void handleRemovePlayer(ActionEvent actionEvent) {
        Main.setRoot("SearchPlayer.fxml");
    }

    public void handleUpdateBudget(ActionEvent actionEvent) {
        Main.setRoot("UpdateBudget.fxml");
    }
}