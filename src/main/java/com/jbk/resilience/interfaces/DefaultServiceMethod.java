package com.jbk.resilience.interfaces;

import java.util.concurrent.Callable;

public interface DefaultServiceMethod<T> {

    T execute();

}
