package com.reading.start.sdk.core;

/**
 * Represent detect mode.
 */
public enum DetectMode {
    Mode2x1,
    Mode3x1,
    Mode2x2,
    Mode3x2,
    Mode4x2;

    @Override
    public String toString() {
        return name();
    }
}
