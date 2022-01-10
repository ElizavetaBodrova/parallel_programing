package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String args[]) throws InterruptedException {
        BarberShop barberShopSim = new BarberShop();
        System.out.println("Парикмахерская открыта");
        Thread barberThread = new Thread(barberShopSim.getBarber());
        Thread[] customers = new Thread[30];
        barberThread.start();

        for (int i = 0; i < 30; i++) {
            customers[i] = new Thread(new Customer(barberShopSim,"Посетитель "));
            customers[i].start();
        }
        barberThread.join();
        for (int i = 0; i < 30; i++) {
            customers[i].interrupt();
            customers[i].join();
        }
        System.out.println("Город засыпает");
       /* // Создаем парикмахерскую с парикмахером
        BarberShop barberShopSim = new BarberShop();
        System.out.printf("Парикмахерская открыта\n");
        ExecutorService executorService = Executors.newFixedThreadPool(31);
        executorService.submit(barberShopSim.getBarber());

        for (int i = 0; i < 30; i++) {
            Customer customer = new Customer(barberShopSim, "Посетитель ");
            executorService.submit(customer);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);

       // System.exit(0);*/
    }
}
