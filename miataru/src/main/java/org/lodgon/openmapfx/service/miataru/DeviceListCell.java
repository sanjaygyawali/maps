/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lodgon.openmapfx.service.miataru;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import static org.lodgon.openmapfx.service.miataru.MiataruService.RESOURCES;

/**
 *
 * @author joeri
 */
public class DeviceListCell extends ListCell<Device> {

    private final Image removeImage = new Image(this.getClass().getResourceAsStream(RESOURCES + "/icons/remove.png"));
    private final Image historyImage = new Image(this.getClass().getResourceAsStream(RESOURCES + "/icons/history.png"));

    private final Label label;
    private final HBox graphic;

    public DeviceListCell(Communicator communicator, Model model) {
        ImageView removeImageView = new ImageView(removeImage);
        removeImageView.setFitHeight(12.0);
        removeImageView.setFitWidth(12.0);

        ImageView historyImageView = new ImageView(historyImage);
        historyImageView.setFitHeight(12.0);
        historyImageView.setFitWidth(12.0);

        Button remove = new Button("", removeImageView);
        remove.setOnAction(e -> {
            model.trackingDevices().remove(getItem());
        });

        Button history = new Button("", historyImageView);
        history.setOnAction(e -> {
            communicator.retrieveHistory(getItem());
        });

        label = new Label();

        graphic = new HBox(5, remove, history, label);
        graphic.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(Device item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            label.setText(item.getName());

            setGraphic(graphic);
        } else {
            setText(null);
            setGraphic(null);
        }
    }

}
