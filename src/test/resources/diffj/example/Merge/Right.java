package org.example;

public class Main {
 private static int x;

 public static void main(String[] args) {
  r2(); // R
 }
 public static void r() {
  x = 1; // R
 }

 public static void r2() {
  r(); // R
 }

}