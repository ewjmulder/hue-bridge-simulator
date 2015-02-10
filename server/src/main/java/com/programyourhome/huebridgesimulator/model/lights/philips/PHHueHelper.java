package com.programyourhome.huebridgesimulator.model.lights.philips;

/**
 * Decompiled from Philips Hue SDK resources, to be able to run without the jar dependency.
 */
public class PHHueHelper
{
    public static final int BRIDGE_VERSION_1_0 = 1005215;
    public static final int BRIDGE_VERSION_1_1 = 1005825;
    public static final int BRIDGE_VERSION_1_1_1 = 1005948;
    public static final int BRIDGE_VERSION_1_1_2 = 1006390;
    public static final int BRIDGE_VERSION_1_2 = 1007000;
    public static final int BRIDGE_VERSION_1_1_2_1 = 1007920;
    public static final int BRIDGE_VERSION_1_1_2_2 = 1007986;

    public static int getBridgeVersion(final int bridgeVer)
    {
        if (bridgeVer <= 1005215) {
            return 1005215;
        }
        if (bridgeVer < 1005825) {
            return 1005825;
        }
        if ((bridgeVer <= 1005948) || (bridgeVer == 1007920) || (bridgeVer == 1007986)) {
            return 1005948;
        }
        return 1007000;
    }

    public static String formatMacAddress(final String macAddress)
    {
        if (macAddress == null) {
            return null;
        }

        final String stringWithoutColons = macAddress.replace(":", "");
        if (stringWithoutColons.length() != 12)
        {
            return macAddress;
        }
        final StringBuffer sbMacData = new StringBuffer();
        sbMacData.append(stringWithoutColons.substring(0, 2)).append(":")
                .append(stringWithoutColons.substring(2, 4)).append(":")
                .append(stringWithoutColons.substring(4, 6)).append(":")
                .append(stringWithoutColons.substring(6, 8)).append(":")
                .append(stringWithoutColons.substring(8, 10)).append(":")
                .append(stringWithoutColons.substring(10, 12));

        return sbMacData.toString();
    }

    public static String macAddressFromBridgeId(final String bridgeIdentifier)
    {
        if (bridgeIdentifier == null) {
            return null;
        }
        if (bridgeIdentifier.length() != 16) {
            return bridgeIdentifier;
        }
        final StringBuffer sbMacAddress = new StringBuffer();
        sbMacAddress.append(bridgeIdentifier.substring(0, 6));
        sbMacAddress.append(bridgeIdentifier.substring(10));
        return formatMacAddress(sbMacAddress.toString());
    }

    public static float precision(final float d)
    {
        return (Math.round(10000.0F * d) / 10000.0F);
    }
}