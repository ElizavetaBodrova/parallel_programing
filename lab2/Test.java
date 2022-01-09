package main;

public class Test {
    public static int customerPlan=20;
    public static void main(String args[]) throws InterruptedException {
        // Создаем парикмахерскую с парикмахером
        BarberShop barberShopSim = new BarberShop();
        System.out.println("Парикмахерская открыта");
        Thread barberThread = new Thread(barberShopSim.getBarber());
        Thread[] customers = new Thread[30];
        barberThread.start();

        for (int i = 0; i < 30; i++) {
            customers[i] = new Thread(new Customer(barberShopSim,"Посетитель "));
            customers[i].start();
        }
        /*barberThread.join();

        for (int i = 0; i < 30; i++) {
            customers[i].join();
        }*/
        System.out.println("Парикмахерская закрыта");
    }
}
