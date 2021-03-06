/*
 * Copyright (c) 2014, 2015, OpenMapFX and LodgON
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of LodgON, OpenMapFX, any associated website, nor the
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

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static org.lodgon.openmapfx.service.miataru.MiataruService.RESOURCES;

/**
 *
 * @author joeri
 */
public class Marker extends Group {

    private static final Image markerIconImage = new Image(Marker.class.getResourceAsStream(RESOURCES + "/icons/marker-icon.png"));
    private static final Image markerShadowImage = new Image(Marker.class.getResourceAsStream(RESOURCES + "/icons/marker-shadow.png"));

    public Marker() {
        ImageView markerIcon = new ImageView(markerIconImage);
        ImageView markerShadow = new ImageView(markerShadowImage);

        getChildren().addAll(markerShadow, markerIcon);
    }

    /**
     * Half the width of the marker image is the actual point around which the
     * x coordinate of the location should be centered.
     *
     * @return the point where the x coordinate of the location should be
     * centered
     */
    public double getCenterX() {
        return markerIconImage.getWidth() / -2.0;
    }

    /**
     * The bottom of the marker image is the actual point around which the y
     * coordinate of the location should be centered.
     *
     * @return the point where the y coordinate of the location should be
     * centered
     */
    public double getCenterY() {
        return markerIconImage.getHeight() * -1.0;
    }
}
