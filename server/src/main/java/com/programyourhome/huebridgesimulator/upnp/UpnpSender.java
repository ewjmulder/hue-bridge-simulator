package com.programyourhome.huebridgesimulator.upnp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This part of the simulator mimics the UPnP broadcast behavior from the hue bridge as closely as possible.
 * This means: every minute 6 NOTIFY messages will be sent. Three different messages, each one sent twice.
 * The messages contain information about the Hue bridge device. Their content is exactly identical to the
 * ones from an actual Philips Hue bridge, except for the 'LOCATION:' and 'uuid:' parts.
 *
 * Credits: This code is based on sources in https://github.com/ps3mediaserver/
 */
@Component
public class UpnpSender extends AbstractUpnpHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // The time to wait after boot time before the first broadcast is sent. Take a few seconds for initialization processes to finish.
    private static final long BROADCAST_INITIAL_DELAY = 5000;
    // The broadcast interval. As for the actual Philips Hue bridge, this is 1 minute.
    private static final long BROADCAST_INTERVAL = 1 * 60 * 1000;
    // The interval between 2 messages that are part of the same broadcast. A few milliseconds should be enough.
    private static final int BROADCAST_INTER_MESSAGE_INTERVAL = 10;

    private final ScheduledExecutorService broadcastService;
    private String upnpMessage1;
    private String upnpMessage2;
    private String upnpMessage3;

    public UpnpSender() {
        this.broadcastService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Build the messages once, using the property values that are available post construct.
     */
    @PostConstruct
    public void init() {
        this.upnpMessage1 = this.buildUpnpMessage1();
        this.upnpMessage2 = this.buildUpnpMessage2();
        this.upnpMessage3 = this.buildUpnpMessage3();
        // Start the broadcast service that will send the UPnP messages over the network.
        this.broadcastService.scheduleAtFixedRate(this::broadcastUpnpInfo, BROADCAST_INITIAL_DELAY,
                BROADCAST_INTERVAL, TimeUnit.MILLISECONDS);
        this.log.info("UPnP broadcast service started.");
    }

    /**
     * Perform the actual UPnP message broadcasting.
     */
    public void broadcastUpnpInfo() {
        try {
            final MulticastSocket ssdpSocket = this.getNewMulticastSocket();
            this.sendMessageBatch(ssdpSocket);
            ssdpSocket.close();
            this.log.info("UPnP broadcast messages sent.");
        } catch (final IOException e) {
            this.log.error("IOException during sending of UPnP messages");
        }
    }

    /**
     * Send a batch of messages according to the way the Hue bridge does it.
     * The batch consists of two times message1, two times message 2 and two times message 3
     * in rapid succession.
     *
     * @param socket the socket to send the message over
     * @throws IOException in case of any IO related exception
     */
    private void sendMessageBatch(final DatagramSocket socket) throws IOException {
        this.sendMessage(socket, this.upnpMessage1);
        this.sleep(BROADCAST_INTER_MESSAGE_INTERVAL);
        this.sendMessage(socket, this.upnpMessage1);
        this.sleep(BROADCAST_INTER_MESSAGE_INTERVAL);
        this.sendMessage(socket, this.upnpMessage2);
        this.sleep(BROADCAST_INTER_MESSAGE_INTERVAL);
        this.sendMessage(socket, this.upnpMessage2);
        this.sleep(BROADCAST_INTER_MESSAGE_INTERVAL);
        this.sendMessage(socket, this.upnpMessage3);
        this.sleep(BROADCAST_INTER_MESSAGE_INTERVAL);
        this.sendMessage(socket, this.upnpMessage3);
    }

    /**
     * Sleep for a certain amount of milliseconds. Used to pause a little while between messages in one batch.
     *
     * @param millis the amount of millis to sleep
     */
    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
        }
    }

    /**
     * Build up UPnP message 1 as broadcasted by the actual Philips Hue bridge.
     * Use the configured parameters for host, port and mac to customize the message.
     *
     * @return the message as a String
     */
    private String buildUpnpMessage1() {
        final StringBuilder sb = new StringBuilder();
        sb.append("NOTIFY * HTTP/1.1").append(CRLF);
        sb.append("HOST: 239.255.255.250:1900").append(CRLF);
        sb.append("CACHE-CONTROL: max-age=100").append(CRLF);
        sb.append("LOCATION: http://").append(this.getSimulatorHost()).append(":").append(this.getSimulatorPort()).append("/description.xml").append(CRLF);
        sb.append("SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1").append(CRLF);
        sb.append("NTS: ssdp:alive").append(CRLF);
        sb.append("NT: upnp:rootdevice").append(CRLF);
        sb.append("USN: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append("::upnp:rootdevice").append(CRLF);
        sb.append("").append(CRLF);
        return sb.toString();
    }

    /**
     * Build up UPnP message 2 as broadcasted by the actual Philips Hue bridge.
     * Use the configured parameters for host, port and mac to customize the message.
     *
     * @return the message as a String
     */
    private String buildUpnpMessage2() {
        final StringBuilder sb = new StringBuilder();
        sb.append("NOTIFY * HTTP/1.1").append(CRLF);
        sb.append("HOST: 239.255.255.250:1900").append(CRLF);
        sb.append("CACHE-CONTROL: max-age=100").append(CRLF);
        sb.append("LOCATION: http://").append(this.getSimulatorHost()).append(":").append(this.getSimulatorPort()).append("/description.xml").append(CRLF);
        sb.append("SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1").append(CRLF);
        sb.append("NTS: ssdp:alive").append(CRLF);
        sb.append("NT: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append(CRLF);
        sb.append("USN: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append(CRLF);
        sb.append("").append(CRLF);
        return sb.toString();
    }

    /**
     * Build up UPnP message 3 as broadcasted by the actual Philips Hue bridge.
     * Use the configured parameters for host, port and mac to customize the message.
     *
     * @return the message as a String
     */
    private String buildUpnpMessage3() {
        final StringBuilder sb = new StringBuilder();
        sb.append("NOTIFY * HTTP/1.1").append(CRLF);
        sb.append("HOST: 239.255.255.250:1900").append(CRLF);
        sb.append("CACHE-CONTROL: max-age=100").append(CRLF);
        sb.append("LOCATION: http://").append(this.getSimulatorHost()).append(":").append(this.getSimulatorPort()).append("/description.xml").append(CRLF);
        sb.append("SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1").append(CRLF);
        sb.append("NTS: ssdp:alive").append(CRLF);
        sb.append("NT: urn:schemas-upnp-org:device:basic:1").append(CRLF);
        sb.append("USN: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append(CRLF);
        sb.append("").append(CRLF);
        return sb.toString();
    }

}
