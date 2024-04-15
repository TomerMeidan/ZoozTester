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

        Fingerprint firstFinger = fingerprints.get(85);
        fingerprints.remove(85);

        Locator.PointF point = locator.getLocation(fingerprints, firstFinger);

        System.out.println(firstFinger.center.x + " " + firstFinger.center.y);
        System.out.println(String.format("%.6f", point.getX()) + " " + String.format("%.6f", point.getY()));
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
