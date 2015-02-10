package com.programyourhome.huebridgesimulator.server.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.huebridgesimulator.AbstractSimulatorPropertiesBase;
import com.programyourhome.huebridgesimulator.model.connection.ActivityType;
import com.programyourhome.huebridgesimulator.model.connection.ConnectedSuccesfully;
import com.programyourhome.huebridgesimulator.model.connection.ConnectionRequest;
import com.programyourhome.huebridgesimulator.model.connection.DeletedSuccesfully;
import com.programyourhome.huebridgesimulator.model.connection.ErrorMessage;
import com.programyourhome.huebridgesimulator.model.connection.ErrorType;
import com.programyourhome.huebridgesimulator.model.connection.HueBridgeResponse;
import com.programyourhome.huebridgesimulator.model.connection.User;
import com.programyourhome.huebridgesimulator.model.connection.UserActivity;
import com.programyourhome.huebridgesimulator.model.connection.UserLookup;
import com.programyourhome.huebridgesimulator.model.lights.SimHueLightState;
import com.programyourhome.huebridgesimulator.proxy.SimHueBridge;

@RestController
public class HueBridgeSimulatorController extends AbstractSimulatorPropertiesBase {

    @Autowired
    private SimHueBridge hueBridge;

    private final Map<String, User> connectedUsers;
    private final SortedMap<LocalDateTime, UserActivity> userActivity;

    public HueBridgeSimulatorController() {
        this.connectedUsers = new HashMap<>();
        this.userActivity = new TreeMap<>();
    }

    @RequestMapping(value = "description.xml", method = RequestMethod.GET)
    public String getDescription() throws IOException {
        this.logUserActivity(null, ActivityType.GET_DESCRIPTION);
        final String descriptionString = IOUtils.toString(this.getClass().getResourceAsStream("/description.xml"));
        return descriptionString
                .replace("[HOST]", this.getSimulatorHost())
                .replace("[PORT]", Integer.toString(this.getSimulatorPort()))
                .replace("[MAC]", this.getSimulatorMac());
    }

    /**
     * Special variant of posting on /api with mime type 'application/x-www-form-urlencoded'. This is needed, because
     * otherwise JSON deserialization will trip over de URL encoded characters. This method will perform the URL decoding and
     * then forward the JSON string to the connect method with the 'proper' mime type.
     *
     * @see connect for further details
     */
    @RequestMapping(value = "api", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public HueBridgeResponse connectUrlEncoded(@RequestBody final String connectionRequestUrlEncoded) throws IOException {
        final String connectionRequestString = UriUtils.decode(connectionRequestUrlEncoded, "UTF8");
        return this.connect(new ObjectMapper().readValue(connectionRequestString, ConnectionRequest.class));
    }

    /**
     * Processes a post on /api to connect a new user to the bridge. The user and it's device type will be saved in an internal
     * collection of users.
     *
     * @param connectionRequest a JSON connection request
     * @return a 'connected succesfully' result
     */
    @RequestMapping(value = "api", method = RequestMethod.POST, consumes = "application/json")
    public HueBridgeResponse connect(@RequestBody final ConnectionRequest connectionRequest) {
        final String username = this.defineUsername(connectionRequest);
        final User user = new User(username, connectionRequest.getDevicetype());
        this.connectedUsers.put(username, user);
        this.logUserActivity(user, ActivityType.CONNECT);
        return new ConnectedSuccesfully(username);
    }

    @RequestMapping(value = "api/{username}/config/whitelist/{usernameToDelete}", method = RequestMethod.DELETE)
    public HueBridgeResponse connect(@PathVariable("username") final String username, @PathVariable("usernameToDelete") final String usernameToDelete) {
        final UserLookup userLookup = this.lookupUser(username);
        return this.executeOrError(userLookup, () -> {
            this.connectedUsers.remove(usernameToDelete);
            this.logUserActivity(userLookup.getUser(), ActivityType.DISCONNECT, usernameToDelete);
            return new DeletedSuccesfully(usernameToDelete);
        });
    }

    @RequestMapping(value = "api/{username}/lights", method = RequestMethod.GET)
    public HueBridgeResponse getLights(@PathVariable("username") final String username) {
        // TODO: re-enable when the server is more stable (or save/load users?)
        // final User user = this.assertUserConnected(username);
        // this.logUserActivity(user, ActivityType.GET_LIGHTS);
        return this.hueBridge.getLights();
    }

    @RequestMapping(value = "api/{username}/lights/{index}/state", method = RequestMethod.PUT, consumes = "application/x-www-form-urlencoded")
    public void setLight(@RequestBody final String stateUrlEncoded, @PathVariable("index") final int index) throws IOException {
        final String stateString = UriUtils.decode(stateUrlEncoded, "UTF8");
        this.setLight(new ObjectMapper().readValue(stateString, SimHueLightState.class), index);
    }

    @RequestMapping(value = "api/{username}/lights/{index}/state", method = RequestMethod.PUT, consumes = "application/json")
    public void setLight(@RequestBody final SimHueLightState state, @PathVariable("index") final int index) {
        // TODO: re-enable when the server is more stable (or save/load users?)
        // final User user = this.assertUserConnected(username);
        // this.logUserActivity(user, ActivityType.SET_LIGHT);
        this.hueBridge.switchLight(index, state.isOn());
    }

    @RequestMapping(value = "simulation/connectedUsers", method = RequestMethod.GET)
    public Collection<User> getConnectedUsers() {
        return this.connectedUsers.values();
    }

    @RequestMapping(value = "simulation/log", method = RequestMethod.GET)
    public Map<LocalDateTime, UserActivity> getLog() {
        return this.userActivity;
    }

    private String defineUsername(final ConnectionRequest connectionRequest) {
        final String username;
        if (connectionRequest.getUsername() != null) {
            username = connectionRequest.getUsername();
        } else {
            username = "SimulatedHueBridgeUser" + (new Random()).nextInt(99999999);
        }
        return username;
    }

    private HueBridgeResponse executeOrError(final UserLookup lookupUser, final Supplier<HueBridgeResponse> supplier) {
        final HueBridgeResponse response;
        if (lookupUser.hasError()) {
            response = lookupUser.getError();
        } else {
            response = supplier.get();
        }
        return response;
    }

    private UserLookup lookupUser(final String username) {
        final User user = this.connectedUsers.get(username);
        final HueBridgeResponse error;
        if (user == null) {
            error = new ErrorMessage(ErrorType.USER_NOT_CONNECTED, "/api", "User: '" + username + "' is not connected to the bridge.");
        } else {
            error = null;
        }
        return new UserLookup(user, error);
    }

    private void logUserActivity(final User user, final ActivityType activityType) {
        this.logUserActivity(user, activityType, null);
    }

    private void logUserActivity(final User user, final ActivityType activityType, final String data) {
        this.userActivity.put(LocalDateTime.now(), new UserActivity(user, activityType, data));
    }

}
