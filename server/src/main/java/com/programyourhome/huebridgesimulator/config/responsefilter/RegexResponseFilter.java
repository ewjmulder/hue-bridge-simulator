package com.programyourhome.huebridgesimulator.config.responsefilter;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.config.GetState;
import com.programyourhome.huebridgesimulator.config.ResponseFilter;

@Component
public class RegexResponseFilter implements IResponseFilter {

    @Override
    public ResponseFilter getResponseFilter(final GetState getState) {
        return getState.getRegexResponseFilter();
    }

    @Override
    public boolean isPatternMatch(final String response, final String pattern) {
        return Pattern.compile(pattern).matcher(response).find();
    }

}
