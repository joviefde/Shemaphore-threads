public class Gasolinera {
    // semaphores
    private Semaphore barber_chair; // the barber chair
    private Semaphore chairs; 		// the waiting room chairs
    private Semaphore barber;		// the actual barber

    private int waiting_customers;	// keeps track of how many customers arw waiting
    private final int number_of_chairs; // how many chairs in the waiting room

    private static final int HAIRCUT_TIME = 1000; // how long does it take for a haircut

    /**
     * Dedault constructor.
     * @param chair_number the number of chairs in the barber shop
     */
    public BarberShop(int chair_number) {
        // 	the barber chair is free - first thread in, can grab it but
        // no thread can get a haircut unless it obtains both barber and
        // barber chair.
        barber_chair = new Semaphore(1, true);
        chairs = new Semaphore(0, true);
        barber = new Semaphore(0, true);

        number_of_chairs = chair_number;
        waiting_customers = 0;
        barberReady();
    }


    /**
     * Indicate that the barber is ready to cut hair. Notify
     * the visitor waiting the longest in the waiting room.
     */

    /**
     * Indicate that the customer c is ready for a haircut. If the
     * barber is busy make the customer sit down. If the barber is free
     * acquire the barber chair and try to get haircut
     *
     * @param c the Customer signaling this
     */
    public void customerReady(Customer c) {
        System.out.println(c + " wants a haircut");
        // if the barber is currently busy, we will sit down
        // sitting down makes you block on the chairs semaphor
        if(barber_chair.availablePermits() <= 0)
            customerSitDown(c);

        // if the customer wants a haircut, he can have one now
        if(c.wantsHaircut()) {
            try {
                // must acquire barber chair first
                barber_chair.acquire();
                haircut(c);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Make the Customer c sit down in the waiting room and block till the barber
     * is avaliable.
     *
     * @param c Customer initiating this action
     */
    public void customerSitDown(Customer c) {
        // if the shop is full (ie. all chairs are taken) the customer gets
        // angry and leaves. The thread does not get blocked and goes back
        // to customerReady()
        if(waiting_customers < number_of_chairs) {
            try {
                waiting_customers++;
                System.out.println(c + " is sitting down in the waiting room. There are " + waiting_customers + " waiting");

                // the thread will be blocked untill barberReady() releases the chair semaphor
                chairs.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(c + " gets angry and leaves because the shop is full");
            c.wantsToLeave();
        }
    }

    /**
     * Perform a haircut. Release the waiting room chair previously occupied
     * Customer c, acquire barbers attention and get the haircut. When done,
     * release the barber chair, and indicate that the barber is ready.
     *
     * @param c
     */
    public void haircut(Customer c) {
        // since the customer is getting a haircut he no longer occupies a chair
        if(waiting_customers > 0)
            waiting_customers--;

        try {
            barber.acquire(); // grab the barber
            System.out.println(c + " is getting a haircut");

            //for(int i=0; i<=HAIRCUT_TIME; i++); // busy wait
            Thread.sleep(HAIRCUT_TIME);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println(c + " is done, checks out his haircut and pays the barber");
        barber_chair.release(); // release the barber chair
        barberReady(); // this will release another chairs allowing for another waiting client to be cut
    }

}

