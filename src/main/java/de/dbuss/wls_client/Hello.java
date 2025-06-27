package de.dbuss.wls_client;

public interface Hello extends java.rmi.Remote {
    default String sayHello()
    {
        return "Hello";
    };
}