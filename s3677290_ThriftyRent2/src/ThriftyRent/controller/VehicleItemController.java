package ThriftyRent.controller;

import ThriftyRent.interfaces.OnVehicleAdded;
import ThriftyRent.model.Van;
import ThriftyRent.model.Vehicle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class VehicleItemController extends ListCell<Vehicle> {
    public Button book;
    public ImageView image;
    public TextField model;
    public TextField vehicle_type;
    public TextField make;
    public TextField year;
    public TextField seats;
    public TextField lmd;
    private FXMLLoader mLoader;
    public Node root;

    public VehicleItemController() {
    }

    private FXMLLoader loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/vehicle_list_item.fxml"));
            loader.setController(this);
            loader.load();
            return loader;
        } catch (Exception e) {
            //System.err.println(e.getMessage() + "l31");
        }
        return null;
    }

    @Override
    protected void updateItem(Vehicle item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            if (mLoader == null) {
                mLoader = loadFXML();
            }
            if(model==null){
                //System.err.println("Empty model 48");
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            make.setText(item.getMake());
            model.setText(item.getModel());
            year.setText(item.getYear()+"");
            if(item.getLastMaintenanceDate()!=null)
                lmd.setText(item.getLastMaintenanceDate().getFormattedDate());
            else lmd.setText("Not maintenanced yet");
            seats.setText(item.getNumberOfPassengers()+"");
            if(item instanceof Van)
                vehicle_type.setText("Van");
            else vehicle_type.setText("Car");
            book.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    openVehicleDetails(item,book);
                }
            });
            try {
                if (item.getImage() == null || "".equals(item.getImage().trim()))
                    item.setImage("No_image_available.png");
                File file = new File("images", item.getImage());
                if (file.exists())
                    image.setImage(new Image("file:" +file.getAbsolutePath()));
            }catch (Exception e){
               // System.err.println("60"+e.getMessage());
            }
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
    public void openVehicleDetails(Vehicle vehicle,Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/vehicle_details.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = node.getScene();
            stage.initOwner(scene.getWindow());
            VehicleDetails controller =
                    loader.getController();
            controller.setVehicle(vehicle);
            stage.setTitle("Vehicle Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
