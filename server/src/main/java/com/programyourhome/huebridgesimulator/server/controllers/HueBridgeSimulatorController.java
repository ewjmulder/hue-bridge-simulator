package com.programyourhome.huebridgesimulator.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.huebridgesimulator.AbstractSimulatorPropertiesBase;
import com.programyourhome.huebridgesimulator.model.connection.ActivityType;
import com.programyourhome.huebridgesimulator.model.connection.ConnectedSuccesfully;
import com.programyourhome.huebridgesimulator.model.connection.ConnectionRequest;
import com.programyourhome.huebridgesimulator.model.connection.DeletedSuccesfully;
import com.programyourhome.huebridgesimulator.model.connection.ErrorMessage;
import com.programyourhome.huebridgesimulator.model.connection.ErrorType;
import com.programyourhome.huebridgesimulator.model.connection.HueBridgeResponse;
import com.programyourhome.huebridgesimulator.model.connection.SetLightSuccesfully;
import com.programyourhome.huebridgesimulator.model.connection.User;
import com.programyourhome.huebridgesimulator.model.connection.UserActivity;
import com.programyourhome.huebridgesimulator.model.connection.UserLookup;
import com.programyourhome.huebridgesimulator.model.lights.GetLightsResponse;
import com.programyourhome.huebridgesimulator.model.lights.SimHueLight;
import com.programyourhome.huebridgesimulator.model.lights.SimHueLightState;
import com.programyourhome.huebridgesimulator.proxy.SimHueBridge;

/**
 * This class simulates a hue bridge REST service. It responds to a subset of the URL's from the actual Philips Hue bridge.
 * A minimal set of supported operations is available to allow users to connect/disconnect and to provide light data and
 * respond to light switches. Furthermore it provides the description.xml that is referenced from the UPnP messages.
 *
 * This class could be extended to provide additional support for equivalent actual Philips Hue bridge functionality.
 */
@RestController
public class HueBridgeSimulatorController extends AbstractSimulatorPropertiesBase {

    private static final String AUTHORIZED_USERS_FILENAME = "authorizedUsers.txt";
    private static final String AUTHORIZED_USERS_SEPARATOR = " --- ";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private SimHueBridge hueBridge;

    private final File authorizedUsersFile;
    private final Map<String, User> authorizedUsers;
    private final SortedMap<LocalDateTime, UserActivity> userActivity;

    public HueBridgeSimulatorController() throws IOException {
        this.authorizedUsers = new HashMap<>();
        this.userActivity = new TreeMap<>();
        File propertiesFile = new File(System.getProperty("simulator.properties.location"));
        this.authorizedUsersFile = new File(propertiesFile.getParentFile() + "/" + AUTHORIZED_USERS_FILENAME);
        this.loadAuthorizedUsers();
    }

    private synchronized void loadAuthorizedUsers() throws IOException {
        this.log.debug("Loading authorized users from file: [" + this.authorizedUsersFile + "]");
        if (!this.authorizedUsersFile.exists()) {
            this.authorizedUsersFile.createNewFile();
        }
        IOUtils.readLines(new FileInputStream(this.authorizedUsersFile), Charset.forName("UTF-8")).stream()
                .map(line -> line.split(AUTHORIZED_USERS_SEPARATOR))
                .map(splitted -> new User(splitted[0], splitted[1]))
                .forEach(user -> this.authorizedUsers.put(user.getUsername(), user));
        this.log.debug("Authorized users after loading from file: " + this.authorizedUsers.values());
    }

    private synchronized void saveAuthorizedUsers() {
        this.log.debug("Saving authorized users to file: " + this.authorizedUsersFile);
        try {
            List<String> lines = this.authorizedUsers.values().stream()
                    .map(user -> user.getUsername() + AUTHORIZED_USERS_SEPARATOR + user.getDeviceType())
                    .collect(Collectors.toList());
            IOUtils.writeLines(lines, "\n", new FileOutputStream(this.authorizedUsersFile), Charset.forName("UTF-8"));
        } catch (Exception e) {
            throw new IllegalStateException("Exception during saving of users.", e);
        }
    }

    /**
     * Provide the description.xml file as is done by the actual Philips Hue bridge. The only differences are
     * the host, port and mac of the bridge to connect to. Also the name in the file has the suffix 'simulator'.
     *
     * @return the description.xml file as a String
     * @throws IOException when the file could not be found on the classpath
     */
    @RequestMapping(value = "description.xml", method = RequestMethod.GET)
    public String getDescription() throws IOException {
        this.log.debug("Request for description.xml");
        this.logUserActivity(null, ActivityType.GET_DESCRIPTION);
        final String descriptionString = IOUtils.toString(this.getClass().getResourceAsStream("/description.xml"), Charset.forName("UTF-8"));
        return descriptionString
                .replace("[HOST]", this.getSimulatorHost())
                .replace("[PORT]", Integer.toString(this.getSimulatorPort()))
                .replace("[MAC]", this.getSimulatorMac());
    }

    /**
     * Special variant of posting a connect with mime type 'application/x-www-form-urlencoded'. This is needed, because
     * otherwise JSON deserialization will trip over de URL encoded characters. This method will perform the URL decoding and
     * then forward the JSON string to the connect method with the 'proper' mime type.
     *
     * @see connect for further details
     */
    @RequestMapping(value = "api", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public HueBridgeResponse connectUrlEncoded(@RequestBody final String connectionRequestUrlEncoded) throws IOException {
        this.log.debug("Request to connect (form wrapper)");
        return this.connect(new ObjectMapper().readValue(this.urlDecode(connectionRequestUrlEncoded), ConnectionRequest.class));
    }

    /**
     * Processes a post to connect a new user to the bridge. The user and it's device type will be saved in an internal
     * collection of connected users and a persistent file of authorized users.
     *
     * @param connectionRequest a JSON connection request
     * @return a 'connected successfully' result
     */
    @RequestMapping(value = "api", method = RequestMethod.POST, consumes = "application/json")
    public HueBridgeResponse connect(@RequestBody final ConnectionRequest connectionRequest) {
        this.log.info("Request to connect for device: " + connectionRequest.getDevicetype());
        final String username = this.defineUsername(connectionRequest);
        final User user = new User(username, connectionRequest.getDevicetype());
        this.authorizedUsers.put(username, user);
        this.saveAuthorizedUsers();
        this.logUserActivity(user, ActivityType.CONNECT);
        this.log.info("Request granted with username: " + username);
        return new ConnectedSuccesfully(username);
    }

    /**
     * Processes a post to disconnect a user to the bridge. The provided {usernameToDelete} will be
     * removed from the list of connected users.
     *
     * @param username the connected username
     * @param usernameToDelete the username to detele
     * @return
     */
    @RequestMapping(value = "api/{username}/config/whitelist/{usernameToDelete}", method = RequestMethod.DELETE)
    public HueBridgeResponse disconnect(@PathVariable("username") final String username, @PathVariable("usernameToDelete") final String usernameToDelete) {
        this.log.info("Request to disconnect for username: " + usernameToDelete);
        // final UserLookup userLookup = this.lookupUser(username);
        // return this.executeOrError(userLookup, () -> {
        // TODO: As long as we don't have persistent users, we should always allow a client to delete an 'old' user.
        this.authorizedUsers.remove(usernameToDelete);
        this.saveAuthorizedUsers();
        // this.logUserActivity(userLookup.getUser(), ActivityType.DISCONNECT, usernameToDelete);
        return new DeletedSuccesfully(usernameToDelete);
        // });
    }

    /**
     * Get the data about all the lights that are (currently) available on this bridge.
     *
     * @param username the connected username
     * @return all light data or an error if the username is not a connected user
     */
    @RequestMapping(value = "api/{username}/lights", method = RequestMethod.GET)
    public HueBridgeResponse getLights(@PathVariable("username") final String username) {
        this.log.debug("Request for lights");
        final UserLookup userLookup = this.lookupUser(username);
        return this.executeOrError(userLookup, () -> {
            this.logUserActivity(userLookup.getUser(), ActivityType.GET_LIGHTS);
            return new GetLightsResponse(this.hueBridge.getLights());
        });
    }

    /**
     * Get the data about one specific light.
     *
     * @param username the connected username
     * @param index the index of the light
     * @return specific light data or an error if no such light or the username is not a connected user
     */
    @RequestMapping(value = "api/{username}/lights/{index}", method = RequestMethod.GET)
    public HueBridgeResponse getLight(@PathVariable("username") final String username, @PathVariable("index") final int index) {
        this.log.debug("Request for light " + index);
        final UserLookup userLookup = this.lookupUser(username);
        return this.executeOrError(userLookup, () -> {
            this.logUserActivity(userLookup.getUser(), ActivityType.GET_LIGHTS);
            Map<String, SimHueLight> lights = this.hueBridge.getLights();
            HueBridgeResponse response;
            if (lights.containsKey("" + index)) {
                response = lights.get("" + index);
            } else {
                response = new ErrorMessage(ErrorType.RESOURCE_NOT_AVAILABLE, "/...", "resource not available");
            }
            return response;
        });
    }

    /**
     * Special variant of putting a set light with mime type 'application/x-www-form-urlencoded'. This is needed, because
     * otherwise JSON deserialization will trip over de URL encoded characters. This method will perform the URL decoding and
     * then forward the JSON string to the connect method with the 'proper' mime type.
     *
     * @see setLight for further details
     */
    @RequestMapping(value = "api/{username}/lights/{index}/state", method = RequestMethod.PUT, consumes = "application/x-www-form-urlencoded")
    public HueBridgeResponse setLight(@RequestBody final String stateUrlEncoded, @PathVariable("username") final String username,
            @PathVariable("index") final int index) throws IOException {
        this.log.debug("Request to change the state of light " + index + " (form wrapper)");
        return this.setLight(new ObjectMapper().readValue(this.urlDecode(stateUrlEncoded), SimHueLightState.class), username, index);
    }

    /**
     * Set the state of a specific light to on or off. The new state of the light is put
     * as a JSON object of type SimHueLightState. Only the 'on' property is used in this method.
     *
     * @param state the new state of the light
     * @param username the connected username
     * @param index the index of the light
     * @return void or an error if the username is not a connected user
     */
    @RequestMapping(value = "api/{username}/lights/{index}/state", method = RequestMethod.PUT, consumes = "application/json")
    public HueBridgeResponse setLight(@RequestBody final SimHueLightState state, @PathVariable("username") final String username,
            @PathVariable("index") final int index) {
        this.log.debug("Request to change the state of light " + index);
        final UserLookup userLookup = this.lookupUser(username);
        return this.executeOrError(userLookup, () -> {
            this.logUserActivity(userLookup.getUser(), ActivityType.SET_LIGHT, index + " -> " + (state.isOn() ? "on" : "off"));
            this.hueBridge.switchLight(index, state.isOn());
            return new SetLightSuccesfully(index, state.isOn());
        });
    }

    /**
     * Meta information about the simulated brigde: get a list of authorized users.
     *
     * @return the list of connected users
     */
    @RequestMapping(value = "simulation/authorizedUsers", method = RequestMethod.GET)
    public Collection<User> getConnectedUsers() {
        return this.authorizedUsers.values();
    }

    /**
     * Meta information about the simulated brigde: get a log list of all user activity.
     *
     * @return the list of all user activity that was logged
     */
    @RequestMapping(value = "simulation/log", method = RequestMethod.GET)
    public Map<LocalDateTime, UserActivity> getLog() {
        return this.userActivity;
    }

    /**
     * Define the username to use. This will be either the supplied username or a randomly generated one.
     *
     * @param connectionRequest the connected request
     * @return the username that was defined
     */
    private String defineUsername(final ConnectionRequest connectionRequest) {
        final String username;
        if (connectionRequest.getUsername() != null) {
            username = connectionRequest.getUsername();
        } else {
            username = "SimulatedHueBridgeUser" + (new Random()).nextInt(99999999);
        }
        return username;
    }

    /**
     * Execute the supplied action and return the result of the supplier if the user lookup succeeded
     * or return an error if the user lookup failed.
     *
     * @param lookupUser the user lookup result
     * @param supplier the supplier of the result, to be called if the user lookup succeeded
     * @return the response
     */
    private HueBridgeResponse executeOrError(final UserLookup lookupUser, final Supplier<HueBridgeResponse> supplier) {
        final HueBridgeResponse response;
        if (lookupUser.hasError()) {
            response = lookupUser.getError();
            this.logUserActivity(null, ActivityType.ERROR, lookupUser.getError().toString());
        } else {
            response = supplier.get();
        }
        return response;
    }

    /**
     * Lookup the user with the provided username. This is presented as a lookup result that contains either
     * the user or an error that occured during lookup.
     *
     * @param username the username
     * @return the user lookup result
     */
    private UserLookup lookupUser(final String username) {
        final User user = this.authorizedUsers.get(username);
        final HueBridgeResponse error;
        if (user == null) {
            this.log.warn("Request from unautorized user: " + username);
            error = new ErrorMessage(ErrorType.UNAUTHORIZED_USER, "/...", "unauthorized user");
        } else {
            error = null;
        }
        return new UserLookup(user, error);
    }

    private String urlDecode(final String input) throws IOException {
        String output = URLDecoder.decode(input, "UTF8");
        if (output.endsWith("=")) {
            // Weird case of a trailing '=', that should be removed.
            output = output.substring(0, output.length() - 1);
        }
        return output;
    }

    /**
     * Log the user activity in an internal collection.
     *
     * @param user the user
     * @param activityType the type
     */
    private void logUserActivity(final User user, final ActivityType activityType) {
        this.logUserActivity(user, activityType, null);
    }

    /**
     * Log the user activity in an internal collection.
     *
     * @param user the user
     * @param activityType the type
     * @param data the data involved
     */
    private void logUserActivity(final User user, final ActivityType activityType, final String data) {
        this.userActivity.put(LocalDateTime.now(), new UserActivity(user, activityType, data));
    }

}
