package com.programyourhome.huebridgesimulator.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.AbstractSimulatorPropertiesBase;

/**
 * This part of the simulator mimics the UPnP behavior from the hue bridge as closely as possible.
 * This means: every minute 6 NOTIFY messages will be sent. Three different messages, each one sent twice.
 * The messages contain information about the Hue bridge device. Their content is exactly identical to the
 * ones from an actual Philips Hue bridge, except for the 'LOCATION:' and 'uuid:' parts.
 *
 * Credits: This code is based on sources in https://github.com/ps3mediaserver/
 */
@Component
public class HueBridgeUpnpSimulator extends AbstractSimulatorPropertiesBase {

    // Line feed used in UPnP traffic.
    private final static String CRLF = "\r\n";

    // IPv4 Multicast channel reserved for SSDP by Internet Assigned Numbers Authority (IANA), must be 239.255.255.250.
    private final static String IPV4_UPNP_HOST = "239.255.255.250";

    // Multicast channel reserved for SSDP by Internet Assigned Numbers Authority (IANA), must be 1900.
    private final static int UPNP_PORT = 1900;

    private static InetAddress getUPNPAddress() throws IOException {
        return InetAddress.getByName(IPV4_UPNP_HOST);
    }

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

    public HueBridgeUpnpSimulator() {
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
    }

    /**
     * Get the multicast socket object to use for sending UPnP messages.
     *
     * @return the multicast socket
     * @throws IOException upon any IO problems
     */
    private MulticastSocket getNewMulticastSocket() throws IOException {
        final MulticastSocket ssdpSocket = new MulticastSocket();
        ssdpSocket.setReuseAddress(true);
        final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(this.getSimulatorHost()));
        if (networkInterface == null) {
            throw new IOException("Could not get network interface with host name: '" + this.getSimulatorHost() + "'.");
        }
        ssdpSocket.setNetworkInterface(networkInterface);

        // Force the use of an IPv4 address.
        final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
            final InetAddress inetAddress = inetAddresses.nextElement();
            if (inetAddress instanceof Inet4Address) {
                ssdpSocket.setInterface(inetAddress);
                break;
            }
        }

        ssdpSocket.setTimeToLive(32);
        ssdpSocket.joinGroup(getUPNPAddress());
        return ssdpSocket;
    }

    /**
     * Perform the actual UPnP message broadcasting.
     */
    public void broadcastUpnpInfo() {
        try {
            final MulticastSocket ssdpSocket = this.getNewMulticastSocket();
            this.sendMessageBatch(ssdpSocket);
            ssdpSocket.close();
            System.out.println("Hue Bridge UPnP simulation messages sent.");
        } catch (final IOException e) {
            e.printStackTrace();
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
     * Send a message over a socket.
     *
     * @param socket the socket
     * @param message the message
     * @throws IOException upon any IO problems
     */
    private void sendMessage(final DatagramSocket socket, final String message) throws IOException {
        socket.send(new DatagramPacket(message.getBytes(), message.length(), getUPNPAddress(), UPNP_PORT));
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

    // TODO: add support for replying to M-SEARCH queries. The commented code below might be useful for that.
    // public static void listen() throws IOException {
    // final Runnable rAlive = new Runnable() {
    // @Override
    // public void run() {
    // while (true) {
    // try {
    // Thread.sleep(delay);
    // sendAlive();
    // if (delay == 20000) // every 180s
    // {
    // delay = 180000;
    // }
    // if (delay == 10000) // after 10, and 30s
    // {
    // delay = 20000;
    // }
    // } catch (final Exception e) {
    // LOGGER.debug("Error while sending periodic alive message: " + e.getMessage());
    // }
    // }
    // }
    // };
    // aliveThread = new Thread(rAlive, "UPNP-AliveMessageSender");
    // aliveThread.start();
    //
    // final Runnable r = new Runnable() {
    // @Override
    // public void run() {
    // boolean bindErrorReported = false;
    // while (true) {
    // try {
    // // Use configurable source port as per http://code.google.com/p/ps3mediaserver/issues/detail?id=1166
    // final MulticastSocket socket = new MulticastSocket(PMS.getConfiguration().getUpnpPort());
    // if (bindErrorReported) {
    // LOGGER.warn("Finally, acquiring port " + PMS.getConfiguration().getUpnpPort() + " was successful!");
    // }
    // final NetworkInterface ni = NetworkConfiguration.getInstance().getNetworkInterfaceByServerName();
    // if (ni != null) {
    // socket.setNetworkInterface(ni);
    // } else if (PMS.get().getServer().getNi() != null) {
    // LOGGER.trace("Setting multicast network interface: " + PMS.get().getServer().getNi());
    // socket.setNetworkInterface(PMS.get().getServer().getNi());
    // }
    // socket.setTimeToLive(4);
    // socket.setReuseAddress(true);
    // socket.joinGroup(getUPNPAddress());
    // while (true) {
    // final byte[] buf = new byte[1024];
    // final DatagramPacket packet_r = new DatagramPacket(buf, buf.length);
    // socket.receive(packet_r);
    //
    // final String s = new String(packet_r.getData());
    //
    // final InetAddress address = packet_r.getAddress();
    // if (s.startsWith("M-SEARCH")) {
    // final String remoteAddr = address.getHostAddress();
    // final int remotePort = packet_r.getPort();
    //
    // if (PMS.getConfiguration().getIpFiltering().allowed(address)) {
    // LOGGER.trace("Receiving a M-SEARCH from [" + remoteAddr + ":" + remotePort + "]");
    //
    // if (StringUtils.indexOf(s, "urn:schemas-upnp-org:service:ContentDirectory:1") > 0) {
    // sendDiscover(remoteAddr, remotePort, "urn:schemas-upnp-org:service:ContentDirectory:1");
    // }
    //
    // if (StringUtils.indexOf(s, "upnp:rootdevice") > 0) {
    // sendDiscover(remoteAddr, remotePort, "upnp:rootdevice");
    // }
    //
    // if (StringUtils.indexOf(s, "urn:schemas-upnp-org:device:MediaServer:1") > 0) {
    // sendDiscover(remoteAddr, remotePort, "urn:schemas-upnp-org:device:MediaServer:1");
    // }
    //
    // if (StringUtils.indexOf(s, PMS.get().usn()) > 0) {
    // sendDiscover(remoteAddr, remotePort, PMS.get().usn());
    // }
    // }
    // } else if (s.startsWith("NOTIFY")) {
    // final String remoteAddr = address.getHostAddress();
    // final int remotePort = packet_r.getPort();
    //
    // LOGGER.trace("Receiving a NOTIFY from [" + remoteAddr + ":" + remotePort + "]");
    // }
    // }
    // } catch (final BindException e) {
    // if (!bindErrorReported) {
    // LOGGER.error("Unable to bind to " + PMS.getConfiguration().getUpnpPort()
    // + ", which means that PMS will not automatically appear on your renderer! "
    // + "This usually means that another program occupies the port. Please "
    // + "stop the other program and free up the port. "
    // + "PMS will keep trying to bind to it...[" + e.getMessage() + "]");
    // }
    // bindErrorReported = true;
    // sleep(5000);
    // } catch (final IOException e) {
    // LOGGER.error("UPNP network exception", e);
    // sleep(1000);
    // }
    // }
    // }
    // };
    // listener = new Thread(r, "UPNPHelper");
    // listener.start();
    // }

}
