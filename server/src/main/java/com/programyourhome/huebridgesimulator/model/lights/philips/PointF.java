package com.programyourhome.huebridgesimulator.model.lights.philips;

/**
 * Decompiled from Philips Hue SDK resources, to be able to run without the jar dependency.
 */
public class PointF
{
    public float x;
    public float y;

    public PointF()
    {
    }

    public PointF(final float x, final float y)
    {
        this.x = x;
        this.y = y;
    }

    public final void set(final float x, final float y)
    {
        this.x = x;
        this.y = y;
    }

    public final void set(final PointF p)
    {
        this.x = p.x;
        this.y = p.y;
    }

    public final void negate() {
        this.x = (-this.x);
        this.y = (-this.y);
    }

    public final void offset(final float dx, final float dy) {
        this.x += dx;
        this.y += dy;
    }

    public final boolean equals(final float x, final float y)
    {
        return ((this.x == x) && (this.y == y));
    }
}