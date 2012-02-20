package observer;

import java.util.HashSet;
import java.util.Set;

import spreadsheet.api.observer.Observer;

public class Alarm {

    private final Set<Observer<Alarm>> observers = new HashSet<Observer<Alarm>>();
    private int timeLeft = 0;

    public void addObserver(Observer<Alarm> alarm) {
        this.observers.add(alarm);
    }

    public void setCountDown(int i) {
        this.timeLeft = i;
    }

    public void tick() {
        System.out.println("TICK");
        timeLeft--;
        if (timeLeft == 0) {
            for (Observer<Alarm> o : observers) {
                o.update(this);
            }
        }
    }

}
