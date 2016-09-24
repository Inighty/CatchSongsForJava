package com.uwhile.zervice.spider.music;

import com.uwhile.zervice.spider.common.ConfigReader;

public class MusicService {
	ConfigReader reader = new ConfigReader();
	private int THREAD_NUM = Integer.parseInt(reader.Read("THREAD_NUM"));
	/*private static final int START_SONG_ID = 25000000;*/
	/*private static final int START_SONG_ID = 19802101;  988700,988701*/
	private int START_SONG_ID = Integer.parseInt(reader.Read("START_SONG_ID"));
	private int STOP_SONG_ID = Integer.parseInt(reader.Read("STOP_SONG_ID"));

	public static void main(String[] args) {
		new MusicService().init();
	}

	public void init() {
		MusicDataHandler dataHandler = new MusicDataHandler();
		for (int threadIndex = 0; threadIndex < THREAD_NUM; threadIndex++) {
			int startId = threadIndex + START_SONG_ID;
			int stopId = STOP_SONG_ID - THREAD_NUM + threadIndex;
			Thread thread = new Thread(new MusicRunnable(startId, stopId, dataHandler, THREAD_NUM));
			thread.start();
		}
	}
}
