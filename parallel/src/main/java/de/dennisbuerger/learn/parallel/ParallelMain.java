package de.dennisbuerger.learn.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(final String[] args) {
		Main main = new Main();
		main.run();
	}

	private void run() {
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
}
