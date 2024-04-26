import java.util.*;

/**
 * A class to locate a point based on a set of fingerprints.
 * The location is determined by calculating the dissimilarity between fingerprints and using a weighted average of coordinates.
 */
public class Locator {
    private static int MIN_RSS_TO_COUNT = -75;
    private static int NEIGHBOUR_MIN_SCORE = -1;
    private static int RSS_OFFSET = 100;

    /**
     * Calculates the location of a given fingerprint based on the dissimilarity to other fingerprints in the dataset.
     * The location is determined by weighted averages of the x and y coordinates of fingerprints with a high score.
     *
     * @param fingerprintsDataSet A list of fingerprints to be used as a reference dataset.
     * @param fingerprint The fingerprint for which the location is to be calculated.
     * @return A PointF object representing the calculated location.
     */
    public PointF getLocation(List<Fingerprint> fingerprintsDataSet, Fingerprint fingerprint){
        PointF point = new PointF();

        float x = 0, y = 0;
        float weight;
        float weightSum = 0;

        List<Fingerprint> fingerprints = getMarksWithSameAps2(fingerprintsDataSet, fingerprint);

        for (Fingerprint mark : fingerprints) {
            float distance = dissimilarity(fingerprint, mark);
            weight = 1 / distance;

            x += (float) (weight * mark.center.x);
            y += (float) (weight * mark.center.y);
            weightSum += weight;
        }

        point.set(x / weightSum, y / weightSum);

        return point;
    }

    /**
     * Calculates the dissimilarity between two fingerprints based on the Euclidean distance of their signal strengths.
     * The dissimilarity is calculated as the square root of the sum of squared differences in signal strengths.
     *
     * @param actual The fingerprint to be compared.
     * @param reference The reference fingerprint against which the actual fingerprint is compared.
     * @return A float value representing the dissimilarity between the two fingerprints.
     */
    public static float dissimilarity(Fingerprint actual, Fingerprint reference) {
        float difference = 0.0f;
        int distanceSq = 0;
        int bssidLevelDiff;

        if (actual == null || reference == null) return Float.MAX_VALUE;

        for (String mac : actual.keySet()) {
            if (reference.containsKey(mac)) {
                bssidLevelDiff = actual.get(mac) - reference.get(mac);
            } else {
                bssidLevelDiff = actual.get(mac) + RSS_OFFSET;
            }

            distanceSq += bssidLevelDiff * bssidLevelDiff;
        }

        for (String mac : reference.keySet()) {
            if (!actual.containsKey(mac)) {
                bssidLevelDiff = reference.get(mac) + RSS_OFFSET;
                distanceSq += bssidLevelDiff * bssidLevelDiff;
            }
        }

        difference = (float) Math.sqrt(distanceSq);
        if (difference == 0.0f) difference = Float.MIN_VALUE;
        return difference;
    }

    /**
     * Filters a list of fingerprints to include only those with a score higher than a specified minimum score.
     * The score is calculated based on the number of Access Points (APs) with a minimum RSSI.
     *
     * @param fingerprints A list of fingerprints to be filtered.
     * @param fingerprint The fingerprint used as a reference for scoring.
     * @return A list of fingerprints that have a score higher than the specified minimum score.
     */
    public static List<Fingerprint> getMarksWithSameAps2(List<Fingerprint> fingerprints, Fingerprint fingerprint) {
        NavigableMap<Integer, List<Fingerprint>> fingerprintsByScore = new TreeMap<>(Collections.<Integer>reverseOrder());

        for (Fingerprint f : fingerprints) {
            final int score = score(fingerprint, f);

            if (score > NEIGHBOUR_MIN_SCORE) {
                List<Fingerprint> list = fingerprintsByScore.get(score);
                if (list == null) {
                    list = new ArrayList<>();
                    fingerprintsByScore.put(score, list);
                }
                list.add(f);
            }
        }

        List<Fingerprint> bestFingerprints = new ArrayList<>();
        for (List<Fingerprint> goodFingerprints : fingerprintsByScore.values()) {
            bestFingerprints.addAll(goodFingerprints);
            if (bestFingerprints.size() > 0) break;
        }

        return bestFingerprints;
    }

    /**
     * Calculates a score for a fingerprint based on the number of Access Points (APs) with a minimum RSSI.
     * The score is calculated as the number of common APs multiplied by 2 minus the number of unique APs in the fingerprint and the reference fingerprint.
     *
     * @param fingerprint The fingerprint for which the score is to be calculated.
     * @param refFp The reference fingerprint used for comparison.
     * @return An integer representing the score of the fingerprint.
     */
    public static int score(Fingerprint fingerprint, Fingerprint refFp) {
        Set<String> fingerprintAps = getApsWithMinRSS(fingerprint, MIN_RSS_TO_COUNT);
        Set<String> refFpAps = getApsWithMinRSS(refFp, MIN_RSS_TO_COUNT);

        Set<String> intersection = new HashSet<>(fingerprintAps);
        intersection.retainAll(refFpAps);

        fingerprintAps.removeAll(intersection);
        refFpAps.removeAll(intersection);

        return intersection.size() * 2 - fingerprintAps.size() - refFpAps.size();
    }

    /**
     * Identifies Access Points (APs) in a fingerprint that have a signal strength (RSSI) above a specified minimum value.
     * APs with RSSI below the minimum are considered weak and are not included in the returned set.
     *
     * @param fp The fingerprint from which APs are to be identified.
     * @param minRSS The minimum RSSI value for an AP to be considered strong.
     * @return A set of MAC addresses of APs with RSSI above the minimum value.
     */
    public static Set<String> getApsWithMinRSS(Fingerprint fp, int minRSS) {
        Set<String> strongAps = new HashSet<>();
        Set<String> weakAps = new HashSet<>();

        if (fp == null)
            return strongAps;

        for (String mac : fp.keySet()) {
            if (fp.get(mac) > minRSS) {
                strongAps.add(mac);
            } else {
                weakAps.add(mac);
            }
        }

        if (strongAps.size() < 3) {
            strongAps.addAll(weakAps);
        }
        return strongAps;
    }

    /**
     * Represents a point in a two-dimensional space with floating-point coordinates.
     * This class is used to store the calculated location of a fingerprint.
     */
    public class PointF {
        private double x;
        private double y;

        /**
         * Gets the x-coordinate of the point.
         *
         * @return The x-coordinate of the point.
         */
        public double getX() {
            return x;
        }

        /**
         * Sets the x-coordinate of the point.
         *
         * @param x The x-coordinate to set.
         */
        public void setX(double x) {
            this.x = x;
        }

        /**
         * Gets the y-coordinate of the point.
         *
         * @return The y-coordinate of the point.
         */
        public double getY() {
            return y;
        }

        /**
         * Sets the y-coordinate of the point.
         *
         * @param y The y-coordinate to set.
         */
        public void setY(double y) {
            this.y = y;
        }

        /**
         * Sets both the x and y coordinates of the point.
         *
         * @param x The x-coordinate to set.
         * @param y The y-coordinate to set.
         */
        public void set(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
