package com.jsc;

public class psvm {

    public static void main(String[] args) {
        Subject subject = new Subject();
        subject.attach(new ObverseA());
        subject.attach(new ObverseB());
        subject.update("1");
    }
}
