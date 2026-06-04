package de.marcschuler.webrtcserver.data.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A list of all image preview types
 * TODO add other formats, like X64, X256 or X1024 for JpegXL
 */
@AllArgsConstructor
@Getter
public enum PreviewFormat {
    J64("jpg",64,0.7f),
    J256("jpg",256,.8f),
    J1024("jpg",1024,.85f);

    // the file format. currently both the format for the internal jdk image plugin and the file extension
    private final String format;
    //the resulation. is the max resolution for either side
    private final int resolution;
    //the quality from 0 to 1. should be above 0.6 everytime
    private final float quality;

}
