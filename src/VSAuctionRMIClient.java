import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class VSAuctionRMIClient extends VSShell implements VSAuctionEventHandler {

    // The user name provided via command line.
    private final String userName;


    public VSAuctionRMIClient(String userName) {
        this.userName = userName;
    }


    // #############################
    // # INITIALIZATION & SHUTDOWN #
    // #############################

    public void init(String registryHost, int registryPort) {
        /*
         * TODO: Implement client startup code
         */
    }

    public void shutdown() {
        /*
         * TODO: Implement client shutdown code
         */
    }


    // #################
    // # EVENT HANDLER #
    // #################

    @Override
    public void handleEvent(VSAuctionEventType event, VSAuction auction) {
        /*
         * TODO: Implement event handler
         */
    }


    // ##################
    // # CLIENT METHODS #
    // ##################

    public void register(String auctionName, int duration, int startingPrice) {
        /*
         * TODO: Register auction
         */
    }

    public void list() {
        /*
         * TODO: List all auctions that are currently in progress
         */
    }

    public void bid(String auctionName, int price) {
        /*
         * TODO: Place a new bid
         */
    }


    // #########
    // # SHELL #
    // #########

    protected boolean processCommand(String[] args) {
        switch (args[0]) {
            case "help":
            case "h":
                System.out.println("The following commands are available:\n"
                        + "  help\n"
                        + "  bid <auction-name> <price>\n"
                        + "  list\n"
                        + "  register <auction-name> <duration> [<starting-price>]\n"
                        + "  quit"
                );
                break;
            case "register":
            case "r":
                if (args.length < 3)
                    throw new IllegalArgumentException("Usage: register <auction-name> <duration> [<starting-price>]");
                int duration = Integer.parseInt(args[2]);
                int startingPrice = (args.length > 3) ? Integer.parseInt(args[3]) : 0;
                register(args[1], duration, startingPrice);
                break;
            case "list":
            case "l":
                list();
                break;
            case "bid":
            case "b":
                if (args.length < 3) throw new IllegalArgumentException("Usage: bid <auction-name> <price>");
                int price = Integer.parseInt(args[2]);
                bid(args[1], price);
                break;
            case "exit":
            case "quit":
            case "x":
            case "q":
                return false;
            default:
                throw new IllegalArgumentException("Unknown command: " + args[0] + "\nUse \"help\" to list available commands");
        }
        return true;
    }


    // ########
    // # MAIN #
    // ########

    public static void main(String[] args) {
        checkArguments(args);
        createAndExecuteClient(args);
    }

    private static void checkArguments(String[] args) {
        if (args.length < 3) {
            System.err.println("usage: java " + VSAuctionRMIClient.class.getName() + " <user-name> <registry_host> <registry_port>");
            System.exit(1);
        }
    }

    private static void createAndExecuteClient(String[] args) {
        String userName = args[0];
        VSAuctionRMIClient client = new VSAuctionRMIClient(userName);

        String registryHost = args[1];
        int registryPort = Integer.parseInt(args[2]);
        client.init(registryHost, registryPort);
        client.shell();
        client.shutdown();
    }
}
