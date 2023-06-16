package com.itblee.utils;

import java.util.Random;

public class ArrayUtil {

    public static <T> T getRandom(T[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

}
