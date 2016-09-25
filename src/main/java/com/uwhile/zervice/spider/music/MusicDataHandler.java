package com.uwhile.zervice.spider.music;

import java.util.List;

public class MusicDataHandler {

	private MusicDao dao = new MusicDao();

	////获取数据库所有歌曲ID
	public List<Long> selectSongId(){
		return dao.selectSongId();
	}
	
	public void putCount(Long songId, Integer count, String songName, String artist) {
		dao.insert(songId, count, songName, artist);
	}

	public void updateInfo(Long songId, String songName, String artist) {
		dao.updateInfo(songId, songName, artist);
	}
	
	public void updateCount(Long existSongId, Integer commentCount) {
		dao.updateCount(existSongId, commentCount);
	}
	
}
