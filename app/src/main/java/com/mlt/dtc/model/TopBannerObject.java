package com.mlt.dtc.model;

import java.io.File;

public
class TopBannerObject {
    private File TBThumbnail, TBImage;

    public TopBannerObject(File TBThumbnail, File TBImage) {
        this.TBThumbnail = TBThumbnail;
        this.TBImage = TBImage;
    }

    public File getTBThumbnail() {
        return TBThumbnail;
    }

    public File getTBImage() {
        return TBImage;
    }
}
