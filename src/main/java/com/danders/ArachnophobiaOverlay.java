package com.danders;

import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;

public class ArachnophobiaOverlay extends Overlay {
    private final ArachnophobiaPlugin pluginInstance;

    @Inject
    public ArachnophobiaOverlay(ArachnophobiaPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphicsContext) {
        for (NPC threatEntity : pluginInstance.retrieveActiveThreats()) {
            obscureThreatEntity(graphicsContext, threatEntity);
        }
        return null;
    }

    private void obscureThreatEntity(Graphics2D graphicsContext, NPC threatEntity) {
        LocalPoint entityLocalPoint = threatEntity.getLocalLocation();
        if (entityLocalPoint == null) {
            return;
        }

        Shape entityHullShape = threatEntity.getConvexHull();
        if (entityHullShape != null) {
            graphicsContext.setColor(Color.BLACK);
            graphicsContext.fill(entityHullShape);
        }
    }
}