package com.programyourhome.huebridgesimulator.config.responsefilter;

import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.config.GetState;
import com.programyourhome.huebridgesimulator.config.ResponseFilter;

@Component
public class StringResponseFilter implements IResponseFilter {

    @Override
    public ResponseFilter getResponseFilter(final GetState getState) {
        return getState.getStringResponseFilter();
    }

    @Override
    public boolean isPatternMatch(final String response, final String pattern) {
        return response.contains(pattern);
    }

}
