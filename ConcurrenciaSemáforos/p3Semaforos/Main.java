public class Main {
    public static void main(String[] args) {
        // Sorry, no command line argument parser
        // please edit the line below to change the
        // number of visiting customers
        int customer_number = 100;
        BarberShop sh = new BarberShop(5);
        Thread[] cust = new Thread[customer_number];

        for(int i=0; i<customer_number; i++)
            cust[i] = new Customer(sh, "" + i);

        for(int i=0; i<customer_number ; i++)
            cust[i].start();
    }
}
