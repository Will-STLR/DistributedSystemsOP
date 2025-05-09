import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class VSAuctionRMIClient extends VSShell implements VSAuctionEventHandler {

    // The user name provided via command line.
    private final String userName;
    private VSAuctionService service;
    private VSAuctionEventHandler handler;

    public VSAuctionRMIClient(String userName) {
        this.userName = userName;
    }


    // #############################
    // # INITIALIZATION & SHUTDOWN #
    // #############################

    public void init(String registryHost, int registryPort) throws RemoteException, NotBoundException {
        /*
         * TODO: Implement client startup code
         */
        try {
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            service = (VSAuctionService) registry.lookup("remoteVSAuctionService");
            //Makes handler available to receive incoming calls on port 0
            handler = new VSAuctionRMIClient(userName);
            UnicastRemoteObject.exportObject(handler, 0);
        }catch (RemoteException e) {
            throw new RemoteException("Could not connect to registry at " + registryHost + ":" + registryPort);
        }catch (NotBoundException e) {
            throw new NotBoundException("Could not find remote service");
        }
    }

    public void shutdown() throws NoSuchObjectException {
        /*
         * TODO: Implement client shutdown code
         */
        //close all remote objects from client side - in this case only our handler
        try {
            UnicastRemoteObject.unexportObject(handler, true);
        }catch (NoSuchObjectException e) {
            throw new NoSuchObjectException("Could not unexport handler");
        }
    }


    // #################
    // # EVENT HANDLER #
    // #################

    @Override
    public void handleEvent(VSAuctionEventType event, VSAuction auction) {
        /*
         * TODO: Implement event handler
         */
        switch (event) {
            case HIGHER_BID -> {
                System.out.println("Your bid on the auction " + auction.getName() + " was overbidden.");
            }
            case AUCTION_END -> {
                System.out.println("Your auction " + auction.getName() + " has ended.");
            }
            case AUCTION_WON -> {
                System.out.println("Congratulations! You won the auction " + auction.getName() + ".");
            }
        }
    }


    // ##################
    // # CLIENT METHODS #
    // ##################

    public void register(String auctionName, int duration, int startingPrice) {
        /*
         * TODO: Register auction
         */
        try {
            service.registerAuction(new VSAuction(auctionName, startingPrice), duration, handler);
        }catch (VSAuctionException | RemoteException e) {
            System.err.println("Could not register auction: " + e.getMessage());
        }
    }

    public void list() throws RemoteException {
        /*
         * TODO: List all auctions that are currently in progress
         */
        try {
            VSAuction[] auctions = service.getAuctions();
            for (VSAuction a : auctions) { System.out.println(a.getName());}
        }catch (RemoteException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public void bid(String auctionName, int price) {
        /*
         * TODO: Place a new bid
         */
        try {
            service.placeBid(userName, auctionName, price, handler);
        }catch (VSAuctionException | RemoteException e) {
            System.err.println("Could not place bid: " + e.getMessage());
        }
    }


    // #########
    // # SHELL #
    // #########

    protected boolean processCommand(String[] args) throws RemoteException {
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

    public static void main(String[] args) throws NotBoundException, RemoteException {
        checkArguments(args);
        createAndExecuteClient(args);
    }

    private static void checkArguments(String[] args) {
        if (args.length < 3) {
            System.err.println("usage: java " + VSAuctionRMIClient.class.getName() + " <user-name> <registry_host> <registry_port>");
            System.exit(1);
        }
    }

    private static void createAndExecuteClient(String[] args) throws NotBoundException, RemoteException {
        String userName = args[0];
        VSAuctionRMIClient client = new VSAuctionRMIClient(userName);

        String registryHost = args[1];
        int registryPort = Integer.parseInt(args[2]);
        client.init(registryHost, registryPort);
        client.shell();
        client.shutdown();
    }
}
