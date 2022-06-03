package net.neednot.onewheel.item;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class FenderRender extends GeoItemRenderer<FenderItem> {
    public FenderRender() {
        super(new FenderModel());
    }
}