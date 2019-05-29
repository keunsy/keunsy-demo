package com.keunsy.demo.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(8)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class StringBuilderBenchmark {
  @Benchmark
  public void testStringAdd() {
    String a = "";
    for (int i = 0; i < 10; i++) {
      a += i;
    }
    print(a);
  }

  @Benchmark
  public void testStringBuilderAdd() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      sb.append(i);
    }
    print(sb.toString());
  }

  private void print(String a) {
  }


  public static void main(String[] args) throws RunnerException {
    //    Options options = new OptionsBuilder().include(StringBuilderBenchmark.class.getSimpleName()).output("E:/Benchmark.log").build();
    Options options = new OptionsBuilder().include(StringBuilderBenchmark.class.getSimpleName()).build();
    new Runner(options).run();
  }
}