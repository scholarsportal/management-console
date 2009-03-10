
package org.duraspace.common.web;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.Enumeration;

import org.apache.log4j.Logger;

public class NetworkUtil {

    protected static final Logger log = Logger.getLogger(NetworkUtil.class);

    /**
     * <pre>
     * This method provides the current environment's IP address,
     * taking into account the Internet connection to any of the available
     * machine's Network interfaces.
     *
     * The outputs can be in octatos or in IPV6 format.
     * </pre>
     */
    public static String getCurrentEnvironmentNetworkIp() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            log.error(e);
        }

        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress addr = address.nextElement();
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                        && !(addr.getHostAddress().indexOf(":") > -1)) {
                    return addr.getHostAddress();
                }
            }
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn(e);
            return "127.0.0.1";
        }
    }

}
