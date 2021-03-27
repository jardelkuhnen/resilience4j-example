package com.jbk.resilience.interfaces;

@FunctionalInterface
public interface DefaultServiceMethod<T> {

    T execute();

}
