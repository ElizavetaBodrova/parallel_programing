package main;

public class Customer implements Runnable {
    private static int id = 1;
    private String customerName;
    private BarberShop barberShop;
    private String hairstyle;

    public Customer(BarberShop bShop, String name) {
        customerName = name + id;
        barberShop = bShop;
        id++;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int check() {
        return barberShop.checkBarber(this);
    }

    public String getHairstyle() {
        return hairstyle;
    }

    public void setHairstyle(String hairstyle) {
        this.hairstyle = hairstyle;
    }

    @Override
    public void run() {
        while(barberShop.customersCount<20) {
            if (check() == 0) {
                barberShop.sitInWorkspace(this);
            } else {
                barberShop.sitInWaitingRoom(this);
            }
            try {
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
