import java.util.*;

public class Locator
{
    private static int MIN_RSS_TO_COUNT = -75;
    private static int NEIGHBOUR_MIN_SCORE = -1;
    private static int RSS_OFFSET = 100;

    public PointF getLocation(List<Fingerprint> fingerprintsDataSet, Fingerprint fingerprint){

        PointF point = new PointF();

        float x = 0, y = 0;
        float weight;
        float weightSum = 0;

        List<Fingerprint> fingerprints = getMarksWithSameAps2(fingerprintsDataSet, fingerprint);

        for (Fingerprint mark : fingerprints) {
            float distance = dissimilarity(fingerprint, mark);
//            if(distance == Float.MIN_VALUE)
//                weight = 0;
//           else
                weight = 1 / distance;

            x += (float) (weight * mark.center.x);
            y += (float) (weight * mark.center.y);
            weightSum += weight;
        }

        point.set(x / weightSum, y / weightSum);

        return point;
    }

    public static float dissimilarity(Fingerprint actual, Fingerprint reference) {
        float difference = 0.0f;
        int distanceSq = 0;
        int bssidLevelDiff;

        if (actual == null || reference == null) return Float.MAX_VALUE;

        // Calculate dissimilarity between signal strengths
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
        // division by zero handling:
        if (difference == 0.0f) difference = Float.MIN_VALUE;
        return difference;
    }

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

    public static int score(Fingerprint fingerprint, Fingerprint refFp) {
        Set<String> fingerprintAps = getApsWithMinRSS(fingerprint, MIN_RSS_TO_COUNT);
        Set<String> refFpAps = getApsWithMinRSS(refFp, MIN_RSS_TO_COUNT);

        Set<String> intersection = new HashSet<>(fingerprintAps); // use the copy constructor
        intersection.retainAll(refFpAps);

        fingerprintAps.removeAll(intersection);
        refFpAps.removeAll(intersection);

        return intersection.size() * 2 - fingerprintAps.size() - refFpAps.size();
    }

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

        // If not enough strong Aps, use weak as well
        if (strongAps.size() < 3) {
            strongAps.addAll(weakAps);
        }
        return strongAps;
    }

    public class PointF {
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void set(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }


}