import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fingerprint for location finding based on WiFi signals.
 * Contains information about WiFi signals, including MAC addresses and signal strengths,
 * as well as geometric properties like center point and radius.
 */
public class Fingerprint {
    public WifiFingerprint instance = new WifiFingerprint();
    public Point center = new Point();
    public double radius;
    public int color;
    public List<Integer> color4f = new ArrayList<>();
    public boolean isRemoved;

    /**
     * Retrieves a list of MAC addresses present in the fingerprint.
     *
     * @return A list of MAC addresses.
     */
    public List<String> keySet() {
        List<String> keys = new ArrayList<>();
        for (MacAndValue macAndValue : instance.macsAndValues) {
            keys.add(macAndValue.mac);
        }
        return keys;
    }

    /**
     * Retrieves the signal strength for a given MAC address.
     *
     * @param mac The MAC address of the WiFi signal.
     * @return The signal strength of the WiFi signal, or Integer.MAX_VALUE if the MAC address is not found.
     */
    public int get(String mac) {
        for (MacAndValue macAndValue : instance.macsAndValues) {
            if (mac.equals(macAndValue.mac))
                return macAndValue.signal;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Checks if the fingerprint contains a signal for a given MAC address.
     *
     * @param mac The MAC address of the WiFi signal.
     * @return true if the fingerprint contains the MAC address, false otherwise.
     */
    public boolean containsKey(String mac) {
        for (MacAndValue macAndValue : instance.macsAndValues) {
            if (macAndValue.mac.equals(mac))
                return true;
        }
        return false;
    }
}

/**
 * Represents a collection of WiFi signals within a fingerprint.
 * Each signal is represented by a MAC address and its signal strength.
 */
class WifiFingerprint {
    public List<MacAndValue> macsAndValues = new ArrayList<>();
}

/**
 * Represents a WiFi signal with a MAC address and its signal strength.
 */
class MacAndValue {
    public String mac;
    public int signal;

    /**
     * Constructs a new MacAndValue object with the specified MAC address and signal strength.
     *
     * @param mac The MAC address of the WiFi signal.
     * @param signal The signal strength of the WiFi signal.
     */
    public MacAndValue(String mac, int signal) {
        this.mac = mac;
        this.signal = signal;
    }
}

/**
 * Represents a point in a two-dimensional space with x and y coordinates.
 */
class Point {
    public double x;
    public double y;
}
