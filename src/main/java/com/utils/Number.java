package com.utils;

public sealed interface Number permits Number.FourImpl, Number.FiveImpl {

    //By declaring String item() in the interface, you're requiring all implementations to have it
    String item();


    //Records automatically generate public accessor methods matching their component names
    public record FourImpl(String item) implements Number { }
    public record FiveImpl(String item) implements Number { }

}
