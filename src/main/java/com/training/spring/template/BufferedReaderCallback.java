package com.training.spring.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Callback Interface
 */
public interface BufferedReaderCallback {
    int doSomethingWithReader(BufferedReader br) throws IOException;
}

