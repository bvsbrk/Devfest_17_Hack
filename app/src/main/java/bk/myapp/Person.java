package bk.myapp;

/**
 * Created by koteswarao
 * ${CLASS}
 */

public class Person {
    public String name;
    public String reason;

    public Person(String name, String reason) {
        this.name = name;
        this.reason = reason;
    }

    public Person() {

    }

    @Override
    public String toString() {
        return this.name + " " + this.reason;
    }
}
