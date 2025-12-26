package com.example.test;

public class TestInput {

    //The issue is that the compiler plugin
    // configuration only applies to the main
    // compilation phase, not the test compilation phase.

    static int BAD = 42; // should fail

    public static void main(String[] args) {
        System.out.println(BAD);
    }
}
