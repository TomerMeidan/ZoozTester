import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * The main class to demonstrate the use of the Locator class for finding proximity points based on fingerprints.
 */
public class Main {
    public static void main(String[] args) {
        // Demonstrate the use of the Locator class for finding proximity points
        proximityPointFinding();
        decreasingProximityPointFinding();
    }

    /**
     * Demonstrates the process of finding a point of interest based on decreasing proximity to fingerprints.
     * It iteratively removes the closest fingerprint and recalculates the location until only one fingerprint remains.
     */
    private static void decreasingProximityPointFinding() {
        List<Fingerprint> fingerprint = new ArrayList<>();
        List<Fingerprint> trainingPrints = new ArrayList<>();

        Locator locator = new Locator();

        jsonParseFingerprints(trainingPrints, "training.json");
        Fingerprint firstFinger = trainingPrints.get(15);
        trainingPrints.remove(15);

        while(!trainingPrints.isEmpty()) {
            Locator.PointF point = locator.getLocation(trainingPrints, firstFinger);
            System.out.println("Number of k neighbours: " + trainingPrints.size());
            printResults(firstFinger, point);
            trainingPrints.remove(0);
        }
    }

    /**
     * Demonstrates the process of finding a point of interest based on proximity to fingerprints.
     * It calculates the location of a point of interest based on the remaining fingerprints.
     */
    private static void proximityPointFinding() {
        List<Fingerprint> fingerprints = new ArrayList<>();

        Locator locator = new Locator();

        jsonParseFingerprints(fingerprints, "radio_map.json");

        Fingerprint firstFinger = fingerprints.get(0);
        fingerprints.remove(0);

        Locator.PointF point = locator.getLocation(fingerprints, firstFinger);

        printResults(firstFinger, point);
    }

    /**
     * Prints the results of the location finding process, including the original fingerprint location and the calculated location.
     * It also calculates and prints the distance from the original location to the calculated location.
     *
     * @param firstFinger The original fingerprint used for the location finding process.
     * @param point The calculated location based on the remaining fingerprints.
     */
    private static void printResults(Fingerprint firstFinger, Locator.PointF point) {
        // ANSI escape codes for bold red and bold turquoise
        String boldRed = "\033[1;31m";
        String boldTurquoise = "\033[1;36m";
        String reset = "\033[0m"; // Reset to default color and style

        // Print user location input with values first, then descriptions in parentheses
        System.out.print(boldRed + "(" +firstFinger.center.x + " , " + firstFinger.center.y +")" + reset);

        // Print calculated user location with values first, then descriptions in parentheses
        System.out.print(boldTurquoise + "(" + String.format("%.6f", point.getX()) + " , " + String.format("%.6f", point.getY()) +")" + reset);
        System.out.println(" Distance from point: " + calculateDistance(point.getX(), point.getY(), firstFinger.center.x, firstFinger.center.y));
    }

    /**
     * Calculates the Euclidean distance between two points in a two-dimensional space.
     *
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The Euclidean distance between the two points.
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /**
     * Parses fingerprints from a JSON file and adds them to a list of fingerprints.
     * The JSON file is expected to contain an array of fingerprint objects, each with properties for the fingerprint's characteristics.
     *
     * @param fingerprints A list to which the parsed fingerprints will be added.
     * @param jsonFilePath The path to the JSON file containing the fingerprints.
     */
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
