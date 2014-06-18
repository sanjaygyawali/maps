/**
 * Copyright (c) 2014, OpenMapFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of LodgON, the website lodgon.com, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL LODGON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lodgon.openmapfx.service.miataru;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.lodgon.openmapfx.core.MultiPositionLayer;
import org.lodgon.openmapfx.core.Position;
import org.lodgon.openmapfx.core.PositionLayer;
import org.lodgon.openmapfx.core.PositionService;
import org.lodgon.openmapfx.service.MapViewPane;
import org.lodgon.openmapfx.service.OpenMapFXService;

/**
 *
 * @author johan
 */
public class MiataruService implements OpenMapFXService, LocationListener  {

    private PositionService positionService;
    private ObjectProperty<Position> positionProperty;

    private final Map<String, Node> deviceNodes = new HashMap<>();
    private final MultiPositionLayer knownDevicesPositionLayer = new MultiPositionLayer();
    private final PositionLayer personalPositionLayer;
    private Timeline getLocationsTimeline;

    final static String RESOURCES = "/org/lodgon/openmapfx/services/miataru";

    private final Model model = new Model();
    private final Communicator communicator = new Communicator(model);

    private final DevicesPane devicesPane;
    private final SettingsPane settingsPane;

    private MapViewPane pane;
    
    public MiataruService() {
        Circle icon = new Circle(5, Color.GREEN);
        personalPositionLayer = new PositionLayer(icon);

        this.devicesPane = new DevicesPane(model);
        this.settingsPane = new SettingsPane(model);

        communicator.addLocationListener(this);

        EventHandler<ActionEvent> onFinished = e -> communicator.retrieveLocation(model.trackingDevices());
        KeyFrame keyFrame = new KeyFrame(Duration.valueOf(model.getUpdateInterval()), onFinished);
        getLocationsTimeline = new Timeline(keyFrame);
        getLocationsTimeline.setCycleCount(Timeline.INDEFINITE);
        model.updateIntervalProperty().addListener((ov, oldValue, newValue) -> {
            try {
                Duration duration = Duration.valueOf(newValue);
                Timeline.Status initialTimelineStatus = getLocationsTimeline.getStatus();
                if (initialTimelineStatus.equals(Timeline.Status.RUNNING)) {
                    getLocationsTimeline.stop();
                }
                getLocationsTimeline.getKeyFrames().set(0, new KeyFrame(duration, onFinished));
                if (initialTimelineStatus.equals(Timeline.Status.RUNNING)) {
                    getLocationsTimeline.play();
                }
            } catch (IllegalArgumentException ex) {
                // ignore
            }
        });
    }

    @Override
    public String getName() {
        return "Miataru";
    }

    @Override
    public Node getMenu() {
        URL devicesUrl = this.getClass().getResource(RESOURCES + "/icons/devices.png");
        ImageView devicesView = new ImageView(devicesUrl.toString());
        Label devicesLabel = new Label ("devices");
        VBox devicesBox = new VBox(devicesView, devicesLabel);
        devicesBox.setAlignment(Pos.TOP_CENTER);
        devicesBox.setOnMouseClicked(e -> pane.setActiveNode(devicesPane));

        URL mapUrl = this.getClass().getResource(RESOURCES + "/icons/map.png");
        ImageView mapView= new ImageView(mapUrl.toString());
        Label mapLabel = new Label ("map");
        VBox mapBox = new VBox(mapView, mapLabel);
        mapBox.setAlignment(Pos.TOP_CENTER);
        mapBox.setOnMouseClicked(e -> pane.showMap());

        URL historyUrl = this.getClass().getResource(RESOURCES + "/icons/history.png");
        ImageView historyView= new ImageView(historyUrl.toString());
        Label historyLabel = new Label ("history");
        VBox historyBox = new VBox(historyView, historyLabel);
        historyBox.setAlignment(Pos.TOP_CENTER);

        URL settingsUrl = this.getClass().getResource(RESOURCES + "/icons/settings.png");
        ImageView settingsView= new ImageView(settingsUrl.toString());
        Label settingsLabel = new Label("settings");
        VBox settingsBox = new VBox (settingsView, settingsLabel);
        settingsBox.setAlignment(Pos.TOP_CENTER);
        settingsBox.setOnMouseClicked(e -> pane.setActiveNode(settingsPane));

        GridPane menu = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25.0);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(25.0);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(25.0);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(25.0);
        menu.getColumnConstraints().addAll(column1, column2, column3, column4);

        menu.addRow(0, devicesBox, mapBox, historyBox, settingsBox);

        return menu;
    }

    @Override
    public void activate(MapViewPane pane) {
        this.pane = pane;
        System.out.println("Activate miataruService");
        pane.getMap().getLayers().addAll(personalPositionLayer, knownDevicesPositionLayer);
        if (positionService == null) {
            positionService = PositionService.getInstance();
            positionProperty = positionService.positionProperty();
            positionProperty.addListener(observable -> {
                Position position = positionProperty.get();
                double lat = position.getLatitude();
                double lon = position.getLongitude();
                personalPositionLayer.updatePosition(lat, lon);
                if (model.trackingProperty().get()) {
                    communicator.updateLocation(lat, lon);
                    System.out.println("new position: "+positionProperty.get());
                }
            });
        }

        getLocationsTimeline.play();
    }

    @Override
    public void deactivate() {
        this.pane.getMap().getLayers().removeAll(personalPositionLayer, knownDevicesPositionLayer);
        this.pane.showMap();
        this.getLocationsTimeline.stop();
    }

    @Override
    public void newLocation(Location location) {
        Node deviceNode = deviceNodes.get(location.getDevice());
        if (deviceNode == null) {
            deviceNode = new Circle(5, Color.RED);
            deviceNodes.put(location.getDevice(), deviceNode);
            knownDevicesPositionLayer.addNode(deviceNode, location.getLatitude(), location.getLongitude());
        } else {
            knownDevicesPositionLayer.updatePosition(deviceNode, location.getLatitude(), location.getLongitude());
        }
    }

}
