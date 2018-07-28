package de.dennisbuerger.learn.parallel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ParallelMain {

	private final class LongRunningGet implements Supplier<Integer> {
		private Integer milliseconds;
		private Integer result;

		public LongRunningGet(final Integer milliseconds, final Integer result) {
			this.milliseconds = milliseconds;
			this.result = result;
		}

		@Override
		public Integer get() {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
			System.out.println(Thread.currentThread());
			return result;
		}
	}

	public static void main(final String[] args) throws Exception {
		ParallelMain main = new ParallelMain();
		main.run();
	}

	private void run() throws Exception {
		// executorServiceExample();

		// async1Example();

		// combineFuturesExample();

		streamWithForkJoin();

		System.out.println("end");

	}

	private void combineFuturesExample() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new LongRunningGet(10000, 10), executorService);
		CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(new LongRunningGet(5000, 10), executorService);

		future1.thenCombine(future2, Integer::sum).thenAccept(i -> System.out.println(i));
		executorService.shutdown();
		// Thread.sleep(20000);
	}

	private void async1Example() {
		CompletableFuture.runAsync(() -> System.out.println("Run async in completable future " + Thread.currentThread()));
		CompletableFuture.supplyAsync(new LongRunningGet(1, 10)).thenApplyAsync(new Function<Integer, Integer>() {
			@Override
			public Integer apply(final Integer i) {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread());
				return i + 1;
			}
		}).thenAcceptAsync(i -> System.out.println(i));
	}

	private void executorServiceExample() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		threadPool.submit(() -> {
			while (true) {
				System.out.println("hello my friend");
				Thread.sleep(1000);
			}
		});
		threadPool.submit(() -> {
			while (true) {
				System.out.println("is this thing on?");
				Thread.sleep(2000);
			}
		});
	}

	private void streamWithForkJoin() throws InterruptedException, ExecutionException {
		new ForkJoinPool(2).submit(() -> IntStream.of(1, 2, 3, 4, 5, 6, 7, 8).parallel().forEach(new IntConsumer() {
			@Override
			public void accept(final int value) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(value);
			}
		})).get();

	}
}
