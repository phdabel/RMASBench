package RSLBench;

import RSLBench.Helpers.Logging.Markers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rescuecore2.components.ComponentLauncher;
import rescuecore2.components.TCPComponentLauncher;
import rescuecore2.components.ComponentConnectionException;
import rescuecore2.connection.ConnectionException;
import rescuecore2.registry.Registry;
import rescuecore2.misc.CommandLineOptions;
import rescuecore2.config.Config;
import rescuecore2.config.ConfigException;
import rescuecore2.Constants;

import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardPropertyFactory;
import rescuecore2.standard.messages.StandardMessageFactory;
/**
 * Launcher for the agents.
 * This will launch as many instances of each of the
 * agents as possible, all using one connection.
 */
public final class Launcher {
    private static final Logger Logger = LogManager.getLogger(Launcher.class);

    private static final String FIRE_BRIGADE_FLAG = "-fb";
    private static final String POLICE_FORCE_FLAG = "-pf";
    private static final String AMBULANCE_TEAM_FLAG = "-at";
  //  private static final String CIVILIAN_FLAG = "-cv";

    private Launcher() {}


    /**
     *  Launches the instances of the agents.
     *  @param args The following arguments are understood: -p <port>, -h <hostname>, -fb <fire brigades>, -pf <police forces>, -at <ambulance teams>
     */
    public static void main(String[] args) {

        Logger.info("RSLB2 Started!");
        try {
            Registry.SYSTEM_REGISTRY.registerEntityFactory(StandardEntityFactory.INSTANCE);
            Registry.SYSTEM_REGISTRY.registerMessageFactory(StandardMessageFactory.INSTANCE);
            Registry.SYSTEM_REGISTRY.registerPropertyFactory(StandardPropertyFactory.INSTANCE);
            Config config = new Config();
            args = CommandLineOptions.processArgs(args, config);
            int port = config.getIntValue(Constants.KERNEL_PORT_NUMBER_KEY, Constants.DEFAULT_KERNEL_PORT_NUMBER);
            String host = config.getValue(Constants.KERNEL_HOST_NAME_KEY, Constants.DEFAULT_KERNEL_HOST_NAME);
            int fb = -1;
            int pf = -1;
            int at = -1;
            // CHECKSTYLE:OFF:ModifiedControlVariable

            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals(FIRE_BRIGADE_FLAG)) {
                    fb = Integer.parseInt(args[++i]);
                    Logger.debug(Markers.GREEN, "fb="+fb);
                }
                else if (args[i].equals(POLICE_FORCE_FLAG)) {
                    pf = Integer.parseInt(args[++i]);
                    Logger.debug(Markers.GREEN, "pf="+pf);
                }
                else if (args[i].equals(AMBULANCE_TEAM_FLAG)) {
                    //at = Integer.parseInt(args[++i]);
                	Logger.warn("Ambulances are not supported yet!");
                }
                else {
                    Logger.warn("Unrecognised option: " + args[i]);
                }
            }

            // CHECKSTYLE:ON:ModifiedControlVariable
            ComponentLauncher launcher = new TCPComponentLauncher(host, port, config);
            connect(launcher, fb, pf, at, config);
        }
        catch (IOException | ConnectionException | InterruptedException e) {
            Logger.error("Error connecting agents", e);
        }
        catch (ConfigException e) {
            Logger.error("Configuration error", e);
        }
        catch (OutOfMemoryError e) {
            Logger.error("Memory exhausted!", e);
            System.exit(0);
        }
    }

    /**
     * It connects the agents to the kernel.
     * @param launcher: the launcher
     * @param fb: number of fire brigades
     * @param pf: number of police forces
     * @param at: number of ambulances
     * @param config: configuration file
     * @throws InterruptedException
     * @throws ConnectionException
     */
    private static void connect(ComponentLauncher launcher, int fb, int pf, int at, Config config) throws InterruptedException, ConnectionException {
        List<PlatoonFireAgent> fireAgents = new ArrayList<>();
        List<PlatoonPoliceAgent> policeAgents = new ArrayList<>();

        int i = 0;
        try {
            while (fb-- != 0) {
                Logger.info("Connecting fire brigade " + (i++) + "...");
                PlatoonFireAgent fireAgent = new PlatoonFireAgent();
                launcher.connect(fireAgent);
                fireAgents.add(fireAgent);

                // Wait a bit, allowing the last agent to receive map info and
                // so. This is added because it seems that multiple agents
                // initializing at the same time increases the memory requirements
                // of the kernel substantially.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        }
        catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
        try {
            while (pf-- != 0) {
                Logger.info("Connecting police force " + (i++) + "...");
                PlatoonPoliceAgent policeAgent = new PlatoonPoliceAgent();
                launcher.connect(policeAgent);
                policeAgents.add(policeAgent);

                // Wait a bit, allowing the last agent to receive map info and
                // so. This is added because it seems that multiple agents
                // initializing at the same time increases the memory requirements
                // of the kernel substantially.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        }
        catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
/*
        try {
            while (at-- != 0) {
                Logger.info("Connecting ambulance team " + (i++) + "...");
                launcher.connect(new SampleAmbulanceTeam());
                Logger.info("success");
            }
        }
        catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
*/
        try {
            while (true) {
                Logger.info("Connecting center " + (i++) + "...");
                launcher.connect(new CenterAgent(fireAgents, policeAgents));
                Logger.info("success");
            }
        }
        catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
        try {
            while (true) {
                Logger.info("Connecting dummy agent " + (i++) + "...");
                launcher.connect(new DummyAgent());
                Logger.info("success");
            }
        }
        catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
    }
}
