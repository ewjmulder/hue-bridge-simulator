package com.programyourhome.huebridgesimulator.server.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.huebridgesimulator.model.ConnectedSuccesfully;
import com.programyourhome.huebridgesimulator.model.ConnectionRequest;
import com.programyourhome.huebridgesimulator.model.SimHueBridge;
import com.programyourhome.huebridgesimulator.model.SimHueLight;

@RestController
public class HueBridgeSimulatorController {

    @Autowired
    private SimHueBridge hueBridge;

    @RequestMapping(value = "description.xml", method = RequestMethod.GET)
    public String getDescription() throws IOException {
        System.out.println("getDescription() is being called!");
        return IOUtils.toString(this.getClass().getResourceAsStream("/description.xml"));
    }

    // TODO: handle delete on username at disconnect.

    // TODO: document: specific mapping for x-www-form-urlencoded, because Jackson otherwise trips over the URL encoding
    // in the post body. Works brilliantly!!
    @RequestMapping(value = "api", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ConnectedSuccesfully[] connectUrlEncoded(@RequestBody final String connectionRequestUrlEncoded) throws IOException {
        System.out.println("Post on api with url encoding: " + connectionRequestUrlEncoded);
        final String connectionRequestString = UriUtils.decode(connectionRequestUrlEncoded, "UTF8");
        return this.connect(new ObjectMapper().readValue(connectionRequestString, ConnectionRequest.class));
    }

    @RequestMapping(value = "api", method = RequestMethod.POST, consumes = "application/json")
    public ConnectedSuccesfully[] connect(@RequestBody final ConnectionRequest connectionRequest) throws IOException {
        // {"devicetype":"hdrv_hue#eneco-001-073803"}
        // http://www.developers.meethue.com/documentation/configuration-api - section 7.1
        System.out.println("Post on api received with device type: " + connectionRequest.getDevicetype());
        // System.out.println("Post on api received with user name: " + connectionRequest.getUsername());
        // System.out.println("Post on api received with data: " + connectionRequest);
        // return "[{\"error\":{\"type\":101,\"address\":\"\",\"description\":\"link button not pressed\"}}]";
        final String username = "simulatedhuebridgeuser" + (new Random()).nextInt(99999);
        System.out.println("Succesfully connecting user: " + username);
        return new ConnectedSuccesfully[] { new ConnectedSuccesfully(username) };
    }

    @RequestMapping("api/{username}/lights")
    public Map<String, SimHueLight> getLights() {
        System.out.println("getLights() is being called!");
        return this.hueBridge.getLights();
    }

    @RequestMapping(value = "/carupdate", method = RequestMethod.POST)
    public ResponseEntity<Car> update(@RequestBody final Car car) {

        if (car != null) {
            car.setMiles(car.getMiles() + 100);
        }
        System.out.println("Car update on car: " + car);

        return new ResponseEntity<Car>(car, HttpStatus.OK);
    }

    public static class Car {

        private String VIN;
        private String color;
        private Integer miles;
        private String test;

        public String getTest() {
            return this.test;
        }

        public void setTest(final String test) {
            this.test = test;
        }

        @Override
        public String toString() {
            return "Car [VIN=" + this.VIN + ", color=" + this.color + ", miles=" + this.miles + ", test=" + this.test + "]";
        }

        public String getVIN() {
            return this.VIN;
        }

        public void setVIN(final String vIN) {
            this.VIN = vIN;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(final String color) {
            this.color = color;
        }

        public Integer getMiles() {
            return this.miles;
        }

        public void setMiles(final Integer miles) {
            this.miles = miles;
        }
    }
}
