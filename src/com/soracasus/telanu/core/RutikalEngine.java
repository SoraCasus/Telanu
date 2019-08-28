package com.soracasus.telanu.core;

import com.soracasus.telanu.renderEngine.DisplayManager;
import org.lwjgl.opengl.Display;

public class RutikalEngine implements Runnable {

	public static final boolean DEBUG = true;
	public static final String VERSION = "alpha 0.0.1a";

	private static final int TARGET_UPS = 75;

	private final Thread thread;

	public RutikalEngine() {
		DisplayManager.createDisplay();
		thread = new Thread(this, "RUTIKAL_ENGINE_THREAD");
	}

	public synchronized void start() {
		String osName = System.getProperty("os.name");
		if(osName.contains("Mac")) {
			thread.run();
		} else {
			thread.start();
		}
	}

	@Override
	public void run() {
		init();
		gameLoop();
	}

	private void init() {

	}

	private void gameLoop() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1e9 / (double)TARGET_UPS;
		double delta = 0;

		while(!Display.isCloseRequested()) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			input();

			while(delta >= 1) {
				update((float)delta);
				delta--;
			}

			render();
		}
	}

	private void input() {

	}

	private void render() {

	}

	private void update(float dt) {

	}

}
