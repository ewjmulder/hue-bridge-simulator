package com.programyourhome.huebridgesimulator.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.programyourhome.huebridgesimulator.AbstractSimulatorPropertiesBase;

public abstract class AbstractUpnpHandler extends AbstractSimulatorPropertiesBase {

    // Line feed used in UPnP traffic.
    protected final static String CRLF = "\r\n";

    // IPv4 Multicast channel reserved for SSDP by Internet Assigned Numbers Authority (IANA), must be 239.255.255.250.
    protected final static String IPV4_UPNP_HOST = "239.255.255.250";

    // Multicast channel reserved for SSDP by Internet Assigned Numbers Authority (IANA), must be 1900.
    protected final static int UPNP_PORT = 1900;

    protected InetAddress getUPNPAddress() throws IOException {
        return InetAddress.getByName(IPV4_UPNP_HOST);
    }

    /**
     * Get the multicast socket object to use for sending or receiving UPnP messages.
     *
     * @return the multicast socket
     * @throws IOException upon any IO problems
     */
    protected MulticastSocket getNewMulticastSocket() throws IOException {
        final MulticastSocket ssdpSocket = new MulticastSocket(UPNP_PORT);
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
        ssdpSocket.joinGroup(this.getUPNPAddress());
        return ssdpSocket;
    }

    /**
     * Send a message over a socket as a broadcast.
     *
     * @param socket the socket
     * @param message the message
     * @throws IOException upon any IO problems
     */
    protected void sendMessage(final DatagramSocket socket, final String message) throws IOException {
        socket.send(new DatagramPacket(message.getBytes(), message.length(), this.getUPNPAddress(), UPNP_PORT));
    }

    /**
     * Send a message over a socket to a specific host and port.
     *
     * @param socket the socket
     * @param message the message
     * @param host the receiving host
     * @param port the receiving port
     * @throws IOException upon any IO problems
     */
    protected void sendMessage(final DatagramSocket socket, final String message, final InetAddress host, final int port) throws IOException {
        socket.send(new DatagramPacket(message.getBytes(), message.length(), host, port));
    }

}
