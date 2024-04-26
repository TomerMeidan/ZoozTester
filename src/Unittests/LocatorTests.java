import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class LocatorTests {

    private Locator locator;
    private List<Fingerprint> fingerprintsDataSet;
    private Fingerprint testFingerprint;

    @Before
    public void setUp() {
        locator = new Locator();
        testFingerprint = new Fingerprint();

        // Mocking a simple Fingerprint setup
        testFingerprint.instance.macsAndValues.add(new MacAndValue("mac1", -70));
        testFingerprint.instance.macsAndValues.add(new MacAndValue("mac2", -60));
        testFingerprint.center.x = 10.0;
        testFingerprint.center.y = 20.0;

        Fingerprint fp1 = new Fingerprint();
        fp1.instance.macsAndValues.add(new MacAndValue("mac1", -65));
        fp1.instance.macsAndValues.add(new MacAndValue("mac2", -55));
        fp1.center.x = 10.5;
        fp1.center.y = 20.5;

        Fingerprint fp2 = new Fingerprint();
        fp2.instance.macsAndValues.add(new MacAndValue("mac1", -75));
        fp2.instance.macsAndValues.add(new MacAndValue("mac2", -65));
        fp2.center.x = 9.5;
        fp2.center.y = 19.5;

        fingerprintsDataSet = new ArrayList<>();
        fingerprintsDataSet.add(fp1);
        fingerprintsDataSet.add(fp2);
    }

    @Test
    public void testGetLocation() {
        Locator.PointF location = locator.getLocation(fingerprintsDataSet, testFingerprint);
        assertNotNull("Computed location should not be null", location);
        assertEquals("Check X coordinate", 10.0, location.getX(), 0.1);
        assertEquals("Check Y coordinate", 20.0, location.getY(), 0.1);
    }

    @Test
    public void testDissimilarity() {
        float dissimilarityScore = Locator.dissimilarity(testFingerprint, fingerprintsDataSet.get(0));
        assertTrue("Dissimilarity score should be a positive value", dissimilarityScore > 0);
    }

    @Test
    public void testGetMarksWithSameAps2() {
        List<Fingerprint> filteredList = locator.getMarksWithSameAps2(fingerprintsDataSet, testFingerprint);
        assertNotNull("Filtered list should not be null", filteredList);
        assertFalse("Filtered list should not be empty", filteredList.isEmpty());
    }

    @Test
    public void testScore() {
        int score = Locator.score(testFingerprint, fingerprintsDataSet.get(0));
        assertTrue("Score should be non-negative", score >= 0);
    }

    @Test
    public void testGetApsWithMinRSS() {
        Set<String> aps = Locator.getApsWithMinRSS(testFingerprint, -75);
        assertNotNull("Access points set should not be null", aps);
        assertFalse("Access points set should not be empty", aps.isEmpty());
    }
}
