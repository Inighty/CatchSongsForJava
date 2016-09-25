package com.uwhile.zervice.spider.music;

public class MusicService {
	// ConfigReader reader = new ConfigReader();
	// private int THREAD_NUM = Integer.parseInt(reader.Read("THREAD_NUM"));
	private int THREAD_NUM = 100;
	private static final int START_SONG_ID = 60000;
	private int STOP_SONG_ID = 480000000;
	// private static final int START_SONG_ID = 19802101;
	// private int START_SONG_ID =
	// Integer.parseInt(reader.Read("START_SONG_ID"));
	// private int STOP_SONG_ID = Integer.parseInt(reader.Read("STOP_SONG_ID"));

	public static void main(String[] args) {
		new MusicService().init();
	}

	public void init() {

		MusicDataHandler dataHandler = new MusicDataHandler();
		// dataHandler.selectSongId();
		Thread threadUpdate = new Thread(new MusicRunnable(0, 0, dataHandler, THREAD_NUM, true));
		threadUpdate.start();

		for (int threadIndex = 0; threadIndex < THREAD_NUM; threadIndex++) {
			int startId = threadIndex + START_SONG_ID;
			int stopId = STOP_SONG_ID - THREAD_NUM + threadIndex;
			Thread thread = new Thread(new MusicRunnable(startId, stopId, dataHandler, THREAD_NUM, false));
			thread.start();
		}
	}
}
