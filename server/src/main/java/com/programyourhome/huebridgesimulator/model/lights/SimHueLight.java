package com.programyourhome.huebridgesimulator.model.lights;

import java.awt.Color;

import com.programyourhome.huebridgesimulator.model.connection.HueBridgeResponse;

/**
 * JSON DTO modeling the data for a hue light.
 * Contains constructor arguments for interesting fields to simulate, the rest is defaulted to sensible values.
 */
public class SimHueLight implements HueBridgeResponse {

    private final SimHueLightState state;
    private final String type;
    private final String name;
    private String modelid;
    private String uniqueid;
    private String swversion;
    private SimHuePointSymbol pointsymbol;

    public SimHueLight(final int index, final String name, final Color color, final boolean on) {
        this.state = new SimHueLightState(color, on);
        this.type = "Extended color light";
        this.name = name;
        this.modelid = "LCT001";
        // Pad with index of length 2, e.g. '01' or '34'. This limits the maximum number of simulated lights to 99, if a
        // unique id with the right length is required.
        this.uniqueid = "01:23:45:67:89:00:00:00-" + String.format("%02d", index);
        this.swversion = "66013452";
        this.pointsymbol = new SimHuePointSymbol();
    }

    public String getModelid() {
        return this.modelid;
    }

    public void setModelid(final String modelid) {
        this.modelid = modelid;
    }

    public String getUniqueid() {
        return this.uniqueid;
    }

    public void setUniqueid(final String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getSwversion() {
        return this.swversion;
    }

    public void setSwversion(final String swversion) {
        this.swversion = swversion;
    }

    public SimHuePointSymbol getPointsymbol() {
        return this.pointsymbol;
    }

    public void setPointsymbol(final SimHuePointSymbol pointsymbol) {
        this.pointsymbol = pointsymbol;
    }

    public SimHueLightState getState() {
        return this.state;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

}
