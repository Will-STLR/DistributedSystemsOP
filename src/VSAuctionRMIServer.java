import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VSAuctionRMIServer {
    public static void main(String[] args) throws Exception {
        //Remote object
        VSAuctionService serviceImpl = new VSAuctionServiceImpl();
        //Remote object at Port 0 export
        VSAuctionService service = (VSAuctionService) UnicastRemoteObject.exportObject(serviceImpl, 0);
        //Bind a remote object at registry
        Registry registry = LocateRegistry.createRegistry(123);
        registry.bind("remoteVSAuctionService", service);

        //Continue running a process
        Thread.sleep(Long.MAX_VALUE);
    }
}
