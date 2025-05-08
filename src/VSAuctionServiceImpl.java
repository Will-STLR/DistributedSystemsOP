import java.net.http.WebSocket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public class VSAuctionServiceImpl implements VSAuctionService {
    HashMap<VSAuction, VSAuctionEventHandler[]> auctionList = new HashMap<>();

    @Override
    public void registerAuction(VSAuction auction, int duration, VSAuctionEventHandler handler) throws VSAuctionException, RemoteException {
        if (duration < 0) throw new IllegalArgumentException("Duration must be positive");

        for (VSAuction a : auctionList.keySet()) {
            if (a.getName().equals(auction.getName())) {
                throw new VSAuctionException("Auction with name " + auction.getName() + " already exists");
            }
        }
        /*
        First index: Highest Bid
        Second index: Previous Bidder
        Third index: Auction greater
        */
        VSAuctionEventHandler[] eventHandlers = new VSAuctionEventHandler[2];
        auctionList.put(auction, eventHandlers);

        new Thread(() -> {
           try {
                //millis will never be negative because of row 11
                Thread.sleep(duration * 1000L);

                VSAuctionEventHandler winner = auctionList.get(auction)[0];

                winner.handleEvent(VSAuctionEventType.AUCTION_WON, auction);
                handler.handleEvent(VSAuctionEventType.AUCTION_END, auction);

                auctionList.remove(auction);
            }catch (InterruptedException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public VSAuction[] getAuctions() throws RemoteException {
        VSAuction[] auctions = new VSAuction[auctionList.size()];

        int i = 0;
        for (VSAuction a : auctionList.keySet()) {
            auctions[i++] = a;
        }
        return auctions;
//        auctionList.keySet().toArray(new VSAuction[0]);
    }

    @Override
    public boolean placeBid(String userName, String auctionName, int price, VSAuctionEventHandler handler) throws VSAuctionException, RemoteException {
        if (price < 0) throw new VSAuctionException("Price must be positive");

        for(VSAuction a : auctionList.keySet()) {
            if(a.getName().equals(auctionName)) {
                if (price > a.getPrice()) {
                    // Information for the previous highest bidder that they have been outbid
                    auctionList.get(a)[0].handleEvent(VSAuctionEventType.HIGHER_BID, a);
                    VSAuctionEventHandler[] eventHandlers = {
                            handler,                            //  New highest bidder
                            auctionList.get(a)[1],              //  Previous highest bidder
                    };
                    auctionList.put(a, eventHandlers);
                    return true;
                }
                return false;
            }
        }
        throw new VSAuctionException("ERROR: No such action available");
    }
}