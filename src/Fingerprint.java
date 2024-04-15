import java.util.ArrayList;
import java.util.List;

public class Fingerprint {
    public WifiFingerprint instance = new WifiFingerprint();
    public Point center = new Point();
    public double radius;
    public int color;
    public List<Integer> color4f = new ArrayList<>();
    public boolean isRemoved;

    public List<String> keySet() {
        List<String> keys = new ArrayList<>();
        for (MacAndValue macAndValue : instance.macsAndValues) {
            keys.add(macAndValue.mac);
        }
        return keys;
    }

    public int get(String mac) {
        for (MacAndValue macAndValue : instance.macsAndValues) {
            if (mac.equals(macAndValue.mac))
                return macAndValue.signal;
        }
        return Integer.MAX_VALUE;
    }

    public boolean containsKey(String mac) {
        for (MacAndValue macAndValue : instance.macsAndValues) {
            if (macAndValue.mac.equals(mac))
                return true;
        }
        return false;
    }
}

class WifiFingerprint {
    public List<MacAndValue> macsAndValues = new ArrayList<>();
}

class MacAndValue {
    public String mac;
    public int signal;

    public MacAndValue(String mac, int signal) {
        this.mac = mac;
        this.signal = signal;
    }
}

class Point {
    public double x;
    public double y;
}
