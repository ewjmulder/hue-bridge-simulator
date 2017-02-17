package com.programyourhome.huebridgesimulator.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This part of the simulator mimics the UPnP response behavior from the hue bridge as closely as possible.
 * This means: sending a response after an MSEARCH has been received. The response consists of three different HTTP messages.
 * The messages contain information about the Hue bridge device. Their content is exactly identical to the
 * ones from an actual Philips Hue bridge, except for the 'LOCATION:' and 'uuid:' parts.
 *
 * Credits: This code is copied and adapted from https://github.com/bwssytems/ha-bridge
 */
@Component
public class UpnpListener extends AbstractUpnpHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String upnpResponse1;
    private String upnpResponse2;
    private String upnpResponse3;

    /**
     * Build the messages once, using the property values that are available post construct.
     */
    @PostConstruct
    public void init() throws IOException {
        this.upnpResponse1 = this.buildUpnpResponse1();
        this.upnpResponse2 = this.buildUpnpResponse2();
        this.upnpResponse3 = this.buildUpnpResponse3();
        new Thread(() -> this.listenForDiscovery()).start();
    }

    /**
     * Start listening for SSDP discovery packets and returns a response.
     * This method will block the calling Thread forever.
     */
    private void listenForDiscovery() {
        try {
            final MulticastSocket multicastSocket = this.getNewMulticastSocket();
            this.log.info("UPnP listener running and ready.");
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                this.log.debug("Detected packet from " + packet.getAddress().getHostAddress());
                if (this.isSSDPDiscoveryPacket(packet)) {
                    this.log.info("Detected SSDP Discovery packet from " + packet.getAddress().getHostAddress());
                    try {
                        this.sendUpnpResponse(multicastSocket, packet.getAddress(), packet.getPort());
                        this.log.info("UPnP response sent to " + packet.getAddress().getHostAddress());
                    } catch (IOException e) {
                        this.log.warn("IOException during sending response to: " + packet.getAddress().getHostAddress(), e);
                    }
                }
            }
        } catch (IOException e) {
            this.log.error("IOException during listening for UPnP traffic.", e);
        }
    }

    /**
     * Checks whether the datagram packet is an SSDP discovery packet.
     */
    private boolean isSSDPDiscoveryPacket(final DatagramPacket packet) {
        String packetString = new String(packet.getData(), 0, packet.getLength());
        return packetString != null
                && packetString.startsWith("M-SEARCH * HTTP/1.1")
                && packetString.contains("\"ssdp:discover\"")
                && (packetString.contains("ST: urn:schemas-upnp-org:device:basic:1")
                        || packetString.contains("ST: upnp:rootdevice")
                        || packetString.contains("ST: ssdp:all"));
    }

    private void sendUpnpResponse(final DatagramSocket socket, final InetAddress host, final int port) throws IOException {
        this.sendMessage(socket, this.upnpResponse1, host, port);
        this.sendMessage(socket, this.upnpResponse2, host, port);
        this.sendMessage(socket, this.upnpResponse3, host, port);
    }

    /**
     * Build up UPnP response 1 as broadcasted by the actual Philips Hue bridge.
     * Use the configured parameters for host, port and mac to customize the message.
     *
     * @return the message as a String
     */
    private String buildUpnpResponse1() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK").append(CRLF);
        sb.append("HOST: 239.255.255.250:1900").append(CRLF);
        sb.append("CACHE-CONTROL: max-age=100").append(CRLF);
        sb.append("EXT:").append(CRLF);
        sb.append("LOCATION: http://").append(this.getSimulatorHost()).append(":").append(this.getSimulatorPort()).append("/description.xml").append(CRLF);
        sb.append("SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1").append(CRLF);
        sb.append("ST: upnp:rootdevice").append(CRLF);
        sb.append("USN: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append("::upnp:rootdevice").append(CRLF);
        sb.append("").append(CRLF);
        return sb.toString();
    }

    /**
     * Build up UPnP response 2 as broadcasted by the actual Philips Hue bridge.
     * Use the configured parameters for host, port and mac to customize the message.
     *
     * @return the message as a String
     */
    private String buildUpnpResponse2() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK").append(CRLF);
        sb.append("HOST: 239.255.255.250:1900").append(CRLF);
        sb.append("CACHE-CONTROL: max-age=100").append(CRLF);
        sb.append("EXT:").append(CRLF);
        sb.append("LOCATION: http://").append(this.getSimulatorHost()).append(":").append(this.getSimulatorPort()).append("/description.xml").append(CRLF);
        sb.append("SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1").append(CRLF);
        sb.append("ST: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append("::upnp:rootdevice").append(CRLF);
        sb.append("USN: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append("::upnp:rootdevice").append(CRLF);
        sb.append("").append(CRLF);
        return sb.toString();
    }

    /**
     * Build up UPnP response 3 as broadcasted by the actual Philips Hue bridge.
     * Use the configured parameters for host, port and mac to customize the message.
     *
     * @return the message as a String
     */
    private String buildUpnpResponse3() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK").append(CRLF);
        sb.append("HOST: 239.255.255.250:1900").append(CRLF);
        sb.append("CACHE-CONTROL: max-age=100").append(CRLF);
        sb.append("EXT:").append(CRLF);
        sb.append("LOCATION: http://").append(this.getSimulatorHost()).append(":").append(this.getSimulatorPort()).append("/description.xml").append(CRLF);
        sb.append("SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1").append(CRLF);
        sb.append("ST: urn:schemas-upnp-org:device:basic:1").append(CRLF);
        sb.append("USN: uuid:2f402f80-da50-11e1-9b23-").append(this.getSimulatorMac()).append("::upnp:rootdevice").append(CRLF);
        sb.append("").append(CRLF);
        return sb.toString();
    }

}
