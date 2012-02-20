package observer;

import spreadsheet.api.observer.Observer;

public class Person implements Observer<Alarm> {

    private boolean awake = false;
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public void update(Alarm changed) {
        if (!awake) {
            awake = true;
            System.out.println(name + " is now awake!");
        } else {
            System.out.println(name + " is now annoyed!");
        }
    }

}
