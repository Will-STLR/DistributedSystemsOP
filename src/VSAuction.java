//TODO: VSAuction needs to implement Serializable
public class VSAuction {

    /* The auction name. */
    private final String name;

    /* The currently highest bid for this auction. */
    private int price;


    public VSAuction(String name, int startingPrice) {
        this.name = name;
        this.price = startingPrice;
    }


    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

}
