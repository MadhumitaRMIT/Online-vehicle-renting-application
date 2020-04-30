package ThriftyRent.controller;

import ThriftyRent.model.RentalRecord;
import ThriftyRent.model.Vehicle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

//rental record controler associated with every record
public class RentalRecordItemController extends ListCell<RentalRecord> {
    public TextField rentalRecordId;
    public TextField rentDate;
    public TextField estimatedReturnDate;
    public TextField rentalLateFee;
    public TextField rentFee;
    public TextField actualReturnDate;
    private FXMLLoader mLoader;
    public Node root;

    public RentalRecordItemController() {
    }

    private FXMLLoader loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/rent_record_item.fxml"));
            loader.setController(this);
            loader.load();
            return loader;
        } catch (Exception e) {
           // System.err.println(e.getMessage() + "l31");
        }
        return null;
    }

    @Override
    protected void updateItem(RentalRecord item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            if (mLoader == null) {
                mLoader = loadFXML();
            }
            if(rentalRecordId==null){
                //System.err.println("Empty RentalRecord 48");
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            rentalRecordId.setText(item.getRecord_id());
            rentDate.setText(item.getRentDate().getFormattedDate());
            estimatedReturnDate.setText(item.getEstimatedReturnDate().getFormattedDate());
            if(item.getActualReturnDate()!=null)
            actualReturnDate.setText(item.getActualReturnDate().getFormattedDate());
            else actualReturnDate.setText("Not Returned Yet");
            rentalLateFee.setText(item.getLateFee()+"");
            rentFee.setText(item.getRentalFee()+"");
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
