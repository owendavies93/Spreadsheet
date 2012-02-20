package observer;

public class ObserverDemo {

    public static void main(String[] args) {

        // A person implements Observer<Alarm>
        Person bill = new Person("Phil Connors");
        Person neo = new Person("Thomas Anderson");

        // An alarm holds onto its observers
        Alarm alarm = new Alarm();

        alarm.addObserver(bill);
        alarm.setCountDown(3);

        for (int i = 0; i < 3; i++) {
            // Tick will update it's observers if the alarm goes off.
            alarm.tick();
        }

        alarm.addObserver(neo);
        alarm.setCountDown(3);

        for (int i = 0; i < 3; i++) {
            alarm.tick();
        }
    }
}
