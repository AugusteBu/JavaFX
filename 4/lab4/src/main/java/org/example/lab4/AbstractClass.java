package org.example.lab4;

public abstract class AbstractClass {

    public static abstract class AbstractStudentMessage {
        public abstract void printInfo();
    }

    public static class StudentFilter extends AbstractStudentMessage {
        @Override
        public void printInfo() {
            System.out.println("Cannot add students while the filter is applied.");
        }
    }
}
