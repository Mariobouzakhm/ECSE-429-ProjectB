import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Hooks {
    @Before
    public void startApplication() {
        System.out.println("Starting Application");
        Utils.startApplication();
    }

    @After
    public void stopApplication() {
        System.out.println("Stopping Application");
        Utils.startApplication();
    }
}
