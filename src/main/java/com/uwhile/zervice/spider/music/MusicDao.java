package com.uwhile.zervice.spider.music;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicDao {

	private static String folderPath = "E:\\my163\\";
	private static String fileThreadPath = folderPath + (String) Thread.currentThread().getName().replace("-", "")
			+ ".txt";
	private static String filePath = folderPath + ".txt";
	private static String ErrorPath = "E:\\my163\\Error.txt";
	// private static final String url =
	// "jdbc:mysql://192.168.1.70:3306/test?characterEncoding=utf-8";
	private static final String url = "jdbc:mysql://localhost:3306/test?characterEncoding=utf-8";
	private static final String user = "root";
	private static final String password = "mc0321..";
	// private static final String user = "writeuser";
	// private static final String password = "writeuser";

	// private static final String SELECT =
	// "SELECT `songId` FROM music_v2 WHERE `count`>9000";
	private static final String SELECT = "SELECT `songId` FROM music_v2";
	private static final String INSERT = "REPLACE INTO `music_v2`(`songId`,`songName`,`artist`,`count`) VALUE(?,?,?,?)";
	private static final String UPDATE = "REPLACE INTO `music_v2`(`songId`,`songName`,`artist`) VALUE(?,?,?)";
	private static final String UPDATE_COUNT = "UPDATE `music_v2` SET `count`=? WHERE `songId`=?";
	private static final String TOTALNUM = "SELECT count(*) FROM `music_v2`";
	private DateFormat dateFormat = DateFormat.getDateTimeInstance();

	protected List<Long> selectSongId() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Long> res = new ArrayList<Long>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			Statement statement = conn.createStatement();
			rs = statement.executeQuery(SELECT);
			while (rs.next()) {
				res.add(rs.getLong("songId"));
			}
		} catch (Exception e) {
		} finally {
			// 关闭数据库
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
		return res;
	}

	protected void insert(Long songId, Integer count, String songName, String artist) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			PreparedStatement ps = conn.prepareStatement(INSERT);
			ps.setLong(1, songId);
			ps.setString(2, songName);
			ps.setString(3, artist);
			ps.setInt(4, count);
			ps.execute();
			/* System.out.println("插入数据成功:"+songId+","+songName+","+artist); */
			String temp = dateFormat.format(new Date()) + ":"
					+ (String) Thread.currentThread().getName().replace("-", "") + "插入数据成功:" + songId + "," + songName
					+ "," + artist + "," + count + "\n";
			fileOutputStream(temp);
		} catch (Exception e) {
			String temp = dateFormat.format(new Date()) + ":"
					+ (String) Thread.currentThread().getName().replace("-", "") + "插入数据失败:" + songId + "," + songName
					+ "," + artist + "," + count + "\n";
			try {
				fileOutputStream(temp);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} finally {
			// 关闭数据库
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
	}

	protected void updateInfo(Long songId, String songName, String artist) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			PreparedStatement ps = conn.prepareStatement(UPDATE);
			ps.setLong(1, songId);
			ps.setString(2, songName);
			ps.setString(3, artist);
			ps.execute();
			String temp = "更新歌曲信息成功:" + songId + "," + songName + "," + artist + "\n";
			fileOutputStream(temp);
		} catch (Exception e) {
			String temp = "更新歌曲信息失败:" + songId + "," + songName + "," + artist + "\n";
			try {
				fileOutputStream(temp);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} finally {
			// 关闭数据库
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
	}

	// 没有路径就创建一个啊
	public static void CreateFolder() {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public void updateCount(Long existSongId, Integer commentCount) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			PreparedStatement ps = conn.prepareStatement(UPDATE_COUNT);
			ps.setInt(1, commentCount);
			ps.setLong(2, existSongId);
			ps.execute();
			String temp = "更新评论数成功:" + existSongId + "," + commentCount + "\n";
			fileOutputStream(temp);
		} catch (Exception e) {
			String temp = "更新评论数失败:" + existSongId + "," + commentCount + "\n";
			try {
				fileOutputStream(temp);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} finally {
			// 关闭数据库
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
	}

	public static int totalNum() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int res = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			Statement statement = conn.createStatement();
			rs = statement.executeQuery(TOTALNUM);

			while (rs.next()) {
				res = rs.getInt(1);
			}
		} catch (Exception e) {
		} finally {
			// 关闭数据库
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
		return res;
	}
	
	// public static void fileOutputStream(String thread,String temp,boolean
	// flag) throws IOException {
	public static void fileOutputStream(String temp) throws IOException {
		/* String temp="Hello world!\n"; */
		CreateFolder();

		// if(!flag){
		fileThreadPath = folderPath + Thread.currentThread().getName().replace("-", "") + ".txt";

		// if(!thread.equals(Thread.currentThread().getName())){
		// System.out.println("Catch!");
		// }
		FileOutputStream fos = new FileOutputStream(fileThreadPath, true);
		//
		// if(file.exists()){
		// if(file.length() > 10*1024*1024){
		// ++count;
		// file.createNewFile();
		// fos(fileThreadPath,true);//true表示在文件末尾追加
		// }}
		// else{
		// fos.close();
		// return;
		// }
		fos.write(temp.getBytes());
		fos.close();
		// }
		// else if (flag){
		// filePath = folderPath +".txt";

		// FileOutputStream fos = new
		// FileOutputStream(filePath,true);//true表示在文件末尾追加
		// if(file.exists()){
		// if(file.length() > 2*1024*1024){
		// ++count;
		// file.createNewFile();
		// }
		// }
		// else{
		// fos.close();
		// return;
		// }
		// fos.write(temp.getBytes());
		// fos.close();
		// }
	}

	public static void fileAllOutputStream(String temp) throws IOException {
		/* String temp="Hello world!\n"; */

		CreateFolder();
		// if(!flag){
		filePath = folderPath + ".txt";

		// if(!thread.equals(Thread.currentThread().getName())){
		// System.out.println("Catch!");
		// }
		FileOutputStream fos = new FileOutputStream(filePath, true);
		//
		// if(file.exists()){
		// if(file.length() > 10*1024*1024){
		// ++count;
		// file.createNewFile();
		// fos(fileThreadPath,true);//true表示在文件末尾追加
		// }}
		// else{
		// fos.close();
		// return;
		// }
		fos.write(temp.getBytes());
		fos.close();
		// }
		// else if (flag){
		// filePath = folderPath +".txt";

		// FileOutputStream fos = new
		// FileOutputStream(filePath,true);//true表示在文件末尾追加
		// if(file.exists()){
		// if(file.length() > 2*1024*1024){
		// ++count;
		// file.createNewFile();
		// }
		// }
		// else{
		// fos.close();
		// return;
		// }
		// fos.write(temp.getBytes());
		// fos.close();
		// }
	}

	public static void fileErrorOutputStream(String temp) throws IOException {
		/* String temp="Hello world!\n"; */
		CreateFolder();
		// if(!flag){

		// if(!thread.equals(Thread.currentThread().getName())){
		// System.out.println("Catch!");
		// }
		FileOutputStream fos = new FileOutputStream(ErrorPath, true);
		//
		// if(file.exists()){
		// if(file.length() > 10*1024*1024){
		// ++count;
		// file.createNewFile();
		// fos(fileThreadPath,true);//true表示在文件末尾追加
		// }}
		// else{
		// fos.close();
		// return;
		// }

		fos.write(temp.getBytes());
		fos.close();
		// }
		// else if (flag){
		// filePath = folderPath +".txt";

		// FileOutputStream fos = new
		// FileOutputStream(filePath,true);//true表示在文件末尾追加
		// if(file.exists()){
		// if(file.length() > 2*1024*1024){
		// ++count;
		// file.createNewFile();
		// }
		// }
		// else{
		// fos.close();
		// return;
		// }
		// fos.write(temp.getBytes());
		// fos.close();
		// }
	}

}
