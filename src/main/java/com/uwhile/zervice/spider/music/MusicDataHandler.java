package com.uwhile.zervice.spider.music;


public class MusicDataHandler {

	private MusicDao dao = new MusicDao();

	public void putCount(Long songId, Integer count, String songName, String artist) {
		dao.insert(songId, count, songName, artist);
	}

	public void updateInfo(Long songId, String songName, String artist) {
		dao.updateInfo(songId, songName, artist);
	}

}
