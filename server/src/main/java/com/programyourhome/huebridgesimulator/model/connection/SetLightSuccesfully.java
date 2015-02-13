package com.programyourhome.huebridgesimulator.model.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.programyourhome.huebridgesimulator.model.connection.SetLightSuccesfully.Wrapper;

/**
 * JSON DTO for the api response of setting the light state.
 * The ArrayList<Wrapper> construct is needed to create the right type of JSON output, namely an array with one item.
 */
public class SetLightSuccesfully extends ArrayList<Wrapper> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

    public SetLightSuccesfully(final int lightId, final boolean on) {
        this.add(new Wrapper(lightId, on));
    }

    public class Wrapper {

        private final StateInfo success;

        public Wrapper(final int lightId, final boolean on) {
            this.success = new StateInfo();
            this.success.lightId = lightId;
            this.success.on = on;
        }

        public StateInfo getSuccess() {
            return this.success;
        }

        public class StateInfo {
            private int lightId;
            private boolean on;

            // To be able to use a custom and dynamic property name.
            @JsonAnyGetter
            public Map<String, Object> customProperty() {
                final Map<String, Object> customProperties = new HashMap<String, Object>();
                customProperties.put("/lights/" + this.lightId + "/state/on", this.on);
                return customProperties;
            }
        }

    }

}
