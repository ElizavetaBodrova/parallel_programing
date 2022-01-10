package main;

import java.util.LinkedList;
import java.util.Queue;

public class BarberShop {
    public static final int NUM_CHAIRS = 3;

    // Количеcтво обслуженных клиентов
    public int customersCount;

    // Парикмахер
    private Barber barberMan;

    // Места в приемной
    private Queue<Customer> customerList = new LinkedList<Customer>();

    public BarberShop() {
        customersCount = 0;
        barberMan = new Barber();
    }

    public Barber getBarber() {
        return barberMan;
    }

    // Занять место в приемной посетителем если есть свободные,
    // возвращает true если удалось
    public synchronized void sitInWaitingRoom(Customer customer) {
        if( customerList.size() < NUM_CHAIRS ) {
            customerList.add(customer);
            System.out.println(customer.getCustomerName() + " занял место в приемной\n");
            notify();
            try {
                if(customersCount<20) wait();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(customer.getCustomerName() + " ушел из парикмахерской, так как нет" +
                    " мест" + "\n");
            notify();
            try {
                if(customersCount<20) wait();
            }
            catch(InterruptedException e) {
                Thread.currentThread().interrupt();
               // e.printStackTrace();
            }
        }

    }

    // Разбудить парикмахера и сесть на рабочее место если парикмахер спит
    public synchronized void sitInWorkspace(Customer customer) {
        System.out.println(customer.getCustomerName() + " разбудил парикмахера и сел на стрижку\n");
        barberMan.work(customer);
        notify();
        try {
            wait();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Проверка наличия посетителей со стороны парикмахера
    public synchronized boolean checkCustomers() {
        System.out.printf("Парикмахер проверяет наличие клиентов: В очереди %d из %d\n\n", customerList.size(), NUM_CHAIRS);
        return !customerList.isEmpty();
    }

    // Проверка состояния парикмахера со стороны клиента
    // 0 - sleep
    // 1 - work
    public synchronized int checkBarber(Customer customer) {
        if(barberMan.getStateFlag()==3) Thread.currentThread().interrupt();
        else {
        System.out.print(customer.getCustomerName() + " проверяет состояние парикмахера:");

        if( barberMan.getStateFlag() == 0 ){
            System.out.println(" парикмахер спит\n");
        } else {

                System.out.println(" парикмахер занят работой\n");
            }
        }

        return barberMan.getStateFlag();
    }

    // Позвать клиента из очереди в приемной если есть, иначе спать, возвращает true если удалось
    public synchronized void callCustomer() {
        if( !customerList.isEmpty() ) {
            barberMan.work(customerList.poll());
        } else {
            barberMan.sleep();
        }
    }

    public class Barber implements Runnable {
        // Время одной стрижки в мс
        public static final int WORK_TIME = 1000;
        //План барбера на день
        public static final int DAY_PLAN = 20;
        // barber`s state flag
        // 0 - sleep
        // 1 - work
        // 2 - check
        int stateFlag;

        public static String[] hairstyles= new String[]{"полубокс", "маллет", "боб", "каре", "ирокез"};

        public Barber() {
            stateFlag = 2;
        }

        @Override
        public void run() {
            while(customersCount<DAY_PLAN) {
                if( (stateFlag!=0) && (checkCustomers()) ) {
                    callCustomer();
                } else {
                    barberMan.sleep();
                }
                System.out.printf("Обслужено посетителей %d\n\n",customersCount);
            }
            stateFlag=3;
            System.out.println("Парикмахерская закрыта\n");

        }

        public int getStateFlag() {
            return stateFlag;
        }

        public boolean setStateFlag(int value) {
            if( (value == 0)||(value == 1) ) {
                stateFlag = value;
                return true;
            }

            return false;
        }

        public synchronized void work(Customer customer) {
            stateFlag = 1;

            System.out.printf("Парикмахер стрижет посетителя: %s\n\n", customer.getCustomerName());
            try {
                Thread.sleep(WORK_TIME);
                customer.setHairstyle(hairstyles[(int) (Math.random() * 5)]);
                System.out.printf("Парикмахер закончил стричь посетителя: %s, посетитель " +
                                "уходит со стрижкой: %s\n\n",
                        customer.getCustomerName(),customer.getHairstyle());
                customersCount++;
                stateFlag = 2;
                notify();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void sleep() {
            if( stateFlag!=0 ) {
                stateFlag = 0;

                System.out.println("Парикмахер спит\n");
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            notifyAll();
        }

    }
}