package com.github.adrior.roborally.utility;

/**
 * A generic class to represent a pair of values.
 *
 * <p> This class provides a simple way to store a pair of related objects. The
 * pair can consist of any two types, specified by the generic parameters {@code K}
 * and {@code V}. This class is useful for storing two related objects together
 * without having to create a separate class for each use case.
 *
 * @param <K> the type of the first element in the pair (the key)
 * @param <V> the type of the second element in the pair (the value)
 */
public record Pair<K, V>(K key, V value) {}
