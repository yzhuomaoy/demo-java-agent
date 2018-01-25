package com.example.demo;

import org.junit.Test;
public class AgentTest {

  @Test
  public void shouldInstantiateSleepingInstance() throws InterruptedException {

    Sleeping sleeping = new Sleeping();
    sleeping.randomSleep();
  }


  public static void main(String[] args) {
    System.out.println("test.timeout=2000".split("test.timeout=")[1]);
  }
}
