package com.programyourhome.huebridgesimulator.config.responsefilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.programyourhome.huebridgesimulator.config.GetState;
import com.programyourhome.huebridgesimulator.config.ResponseFilter;
import com.programyourhome.huebridgesimulator.model.SimLight;

public interface IResponseFilter {

    public default void process(final String response, final GetState getState, final SimLight simLight) {
        ResponseFilter responseFilter = getResponseFilter(getState);
        if (responseFilter != null) {
            simLight.setOn(this.performFilter(response, responseFilter.getPatternOn(), responseFilter.getPatternOff()));
        }
    }

    public ResponseFilter getResponseFilter(GetState getState);

    public default boolean performFilter(final String response, final String patternOn, final String patternOff) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        String filter = this.getClass().getSimpleName();
        if (isPatternMatch(response, patternOn)) {
            log.debug(filter + " found a match for pattern on [" + patternOn + "] in response [" + response + "]");
            return true;
        }
        if (isPatternMatch(response, patternOff)) {
            log.debug(filter + " found a match for pattern off [" + patternOff + "] in response [" + response + "]");
            return false;
        }
        throw new IllegalArgumentException(this.getClass().getSimpleName() + " could not find "
                + "pattern on [" + patternOn + "] or pattern off [" + patternOff + "] in response [" + response + "]");
    }

    public boolean isPatternMatch(String response, String pattern);

}
