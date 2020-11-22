package com.mlt.dtc.modal;

import java.io.File;

public class SideBannerObject {

    private File SBThumbNail, SBMainImage;

    public SideBannerObject(File SBThumbNail, File SBMainImage) {
        this.SBThumbNail = SBThumbNail;
        this.SBMainImage = SBMainImage;
    }

    public File getSBThumbNail() {
        return SBThumbNail;
    }

    public File getSBMainImage() {
        return SBMainImage;
    }
}
