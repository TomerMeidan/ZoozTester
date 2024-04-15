import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        List<Fingerprint> fingerprints = new ArrayList<>();

        Locator locator = new Locator();

        jsonParseFingerprints(fingerprints, "radio_map.json");

        Fingerprint firstFinger = fingerprints.get(144);
        fingerprints.remove(144);

        Locator.PointF point = locator.getLocation(fingerprints, firstFinger);

        printResults(firstFinger, point);
    }

    private static void printResults(Fingerprint firstFinger, Locator.PointF point) {
        // ANSI escape codes for bold red and bold turquoise
        String boldRed = "\033[1;31m";
        String boldTurquoise = "\033[1;36m";
        String reset = "\033[0m"; // Reset to default color and style

// Print user location input with values first, then descriptions in parentheses
        System.out.println(boldRed + "(" +firstFinger.center.x + " | " + firstFinger.center.y +")" + reset + " (" + "User Location Input (X | Y)" + ")");

// Print calculated user location with values first, then descriptions in parentheses
        System.out.println(boldTurquoise +  "(" + String.format("%.6f", point.getX()) + " | " + String.format("%.6f", point.getY()) +")" + reset + " (" + "Calculated User Location to Nearest Fingerprint (X | Y)" + ")");

    }

    private static void jsonParseFingerprints(List<Fingerprint> fingerprints, String jsonFilePath) {
        try {
            // Read the JSON file
            String jsonText = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

            // Parse the JSON
            JSONArray jsonArray = new JSONArray(jsonText);

            // Iterate through each object in the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract CLASSNAME and INSTANCE properties
                String className = jsonObject.getString("CLASSNAME");
                JSONObject instance = jsonObject.getJSONObject("INSTANCE");

                // Extract INSTANCE properties
                JSONObject wifiFingerprint = instance.getJSONObject("mWiFiFingerprint");
                JSONObject center = instance.getJSONObject("mCenter");
                double radius = instance.getDouble("mRadius");
                int color = instance.getInt("mColor");
                JSONArray color4f = instance.getJSONArray("mColor4f");
                boolean isRemoved = instance.getBoolean("mIsRemoved");

                Fingerprint fingerprint = new Fingerprint();
                fingerprint.center.x = center.getDouble("x");
                fingerprint.center.y = center.getDouble("y");

                fingerprint.radius = radius;
                fingerprint.color =color;
                fingerprint.isRemoved = isRemoved;

                for (int j = 0; j < wifiFingerprint.names().length(); j++) {
                    String mac = wifiFingerprint.names().getString(j);
                    int signal = wifiFingerprint.getInt(mac);
                    MacAndValue macAndValue = new MacAndValue(mac, signal);
                    fingerprint.instance.macsAndValues.add(macAndValue);
                }

                for (int j = 0; j < color4f.length(); j++) {
                    fingerprint.color4f.add(color4f.getInt(j));
                }

                fingerprints.add(fingerprint);
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
