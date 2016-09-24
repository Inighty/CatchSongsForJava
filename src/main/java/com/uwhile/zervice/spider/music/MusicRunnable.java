package com.uwhile.zervice.spider.music;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MusicRunnable implements Runnable {

	private long startSongId;
	private long stopSongId;

	private MusicDataHandler musicDataHandler;
	private Client client = Client.create();
	private static String comment_url = "http://music.163.com/weapi/v1/resource/comments/R_SO_4_#songId#/?csrf_token=";
	private static String info_url = "http://music.163.com/api/song/detail/?ids=%5B#songIds#%5D";
	ObjectMapper mapper = new ObjectMapper();

	// private int visited = 0;
	// private int tryTimes = 100; //失败后重试次数
	private int total_thread_num;
	private int get_info_once = 100;
	private DateFormat dateFormat = DateFormat.getDateTimeInstance();

	public MusicRunnable(int startSongId, int stopSongId, MusicDataHandler musicDataHandler, int total_thread_num) {
		this.startSongId = startSongId;
		this.stopSongId = stopSongId;
		this.musicDataHandler = musicDataHandler;
		this.total_thread_num = total_thread_num;
	}

	@SuppressWarnings("unchecked")
	public void run() {

		for (Long songId = startSongId; songId <= stopSongId;) {
			// System.out.println(visited+"\n");
			// for (int i = 0; i < tryTimes + 1; i++) {
			// //抛出异常后重试
			// if(i > 0)
			// {
			//// System.out.println(Thread.currentThread().getName().replace("-","")
			// + "" + songId + "第" + i + "次重试\n");
			// try {
			// MusicDao.fileAllOutputStream(Thread.currentThread().getName().replace("-","")
			// + "" + songId + "第" + i + "次重试\n");
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			try {

				// try {
				// if (visited % 100001 == 0) {
				// System.out.println(dateFormat.format(new Date()) + ":" +
				// Thread.currentThread().getName() + "--->" + songId);
				//// MusicDao.fileAllOutputStream(dateFormat.format(new
				// Date())+" 已经有"+MusicDao.totalNum()+"条评论超过一千的数据了\n");
				// }
				StringBuilder onceGet = new StringBuilder();
				for (int index = 0; index < get_info_once; index++) {
					onceGet.append(songId + index * total_thread_num).append(",");
				}
				MusicDao.fileAllOutputStream(
						(String) Thread.currentThread().getName().replace("-", "") + " 取这些songId:" + onceGet + "\n");
				MusicDao.fileOutputStream(
						(String) Thread.currentThread().getName().replace("-", "") + " 取这些songId:" + onceGet + "\n");
				songId += get_info_once * total_thread_num;
				onceGet.deleteCharAt(onceGet.length() - 1);
				String toGet = info_url.replaceAll("#songIds#", onceGet.toString());
				// WebResource webResource = client.resource(toGet);
				// webResource.setProperty("accept", "application/json");
				// ClientResponse response = webResource.header("origin",
				// "http://music.163.com").post(ClientResponse.class);
				// String info = response.getEntity(String.class);
				//
				// Map<String, Object> detail_info = gson.fromJson(info,
				// HashMap.class);
				// List<LinkedTreeMap<String, Object>> songs =
				// (List<LinkedTreeMap<String, Object>>)
				// detail_info.get("songs");
				//
				//
				WebResource webResource = client.resource(toGet);
				webResource.setProperty("accept", "application/json");
				ClientResponse response = webResource.header("origin", "http://music.163.com")
						.post(ClientResponse.class);
				// System.out.println(response+"\n");
				if (response.equals(null)) {
					continue;
				}
				String info = response.getEntity(String.class);
				// MusicDao.fileErrorOutputStream(dateFormat.format(new
				// Date())+":"+info+"\n");
				// Map<String, Object> detail_info = gson.fromJson(info,
				// HashMap.class);

				ObjectMapper objectMapper = new ObjectMapper();
				// Map<String, Map<String, Object>> detail_info =
				// objectMapper.readValue(info, Map.class);
				// Map<String,Object> detail_info =
				// mapper.readValue(info,Map.class);
				// Iterator<String> = detail_info.keySet().iterator();
				JsonNode detail_info = objectMapper.readTree(info); // 将Json串以树状结构读入内存
				JsonNode songs = detail_info.get("songs");// 得到results这个节点下的信息
				// System.out.println("detailinfo="+ detail_info+"\n");
				// List<LinkedTreeMap<String, Object>> songs =
				// (List<LinkedTreeMap<String, Object>>)
				// detail_info.get("songs");

				// List<LinkedTreeMap<String, Object>> songs =
				// (List<LinkedTreeMap<String, Object>>)
				// detail_info.get("songs");
				// System.out.println("songs=" + songs + "\n");
				// MusicDao.fileErrorOutputStream(String.valueOf(songs.get("id")));
				if (songs.toString().isEmpty()) {
					// System.out.println(Thread.currentThread().getName()+":songId为"+onceGet+"无数据\n");
					// MusicDao.fileErrorOutputStream(temp);
					continue;
				}

				// for(int j=0;j<geoNumber.size();j++) //循环遍历子节点下的信息
				// {
				// System.out.println(geoNumber.get(j).get("x").getDoubleValue()+"
				// "+geoNumber.get(j).get("y").getDoubleValue());
				// }
				//

				for (JsonNode songInfo : songs) {
					// Long existSongId = ((Double)
					// songInfo.get("id")).longValue();
					Long existSongId = songInfo.get("id").longValue();

					MusicDao.fileErrorOutputStream(existSongId.toString() + " 有数据！\n");
					// MusicDao.fileErrorOutputStream(onceGet.toString().replace(existSongId.toString()+",",
					// "") + " 无数据！");
					String name = songInfo.get("name").toString().replaceAll("\"", "");
					// List<LinkedTreeMap<String, Object>> artists =
					// (List<LinkedTreeMap<String, Object>>)
					// songInfo.get("artists");
					JsonNode artists = songInfo.get("artists");

					// Object hMusic = (Object) songInfo.get("hMusic");
					// Object bMusic = (Object) songInfo.get("bMusic");
					String artist = (String) artists.get(0).get("name").toString().replaceAll("\"", "");
					String toGetComment = comment_url.replaceAll("#songId#", existSongId.toString());
					WebResource commentResource = client.resource(toGetComment);
					ClientResponse commentResponse = null;// post
					try {

						commentResponse = commentResource
								.header("Referer", "http://music.163.com/song?id=" + existSongId.toString())
								.entity("params=MjdcjJ0FbCPJR90VfRep5nnE4NMItX4f3xjdJPlbvatfoUbt6g4RBELo7JcOqRU176SC343L%2BLsFPRwEVdVFrlJf0tE2ATMx%2FtnCNjbdgroyG4OQO4GblXM51c5%2F45yoDigdNTiejTFu%2Fq7wOffZ1NhKJmWbfBlPDPpJ%2B07ijVergSaH4cq6QKrpSivU7Qp7&encSecKey=6ba5031c2f2d3559a45787c7d4b8dada1c154de42576a5ae137e7e5c04096c303cfb6d19a9fe46391906007cdc714c9a7043e76d2bef9320a2a3494b3b86a5880261480486400e717cd1f71141db78e0fb39727c5b30b80fb6f62a1b03bccc59923207f125f1fa4edd07172733e14480d68c3ecdee9613235e9c30bc5f712b11",
										MediaType.APPLICATION_FORM_URLENCODED_TYPE) // 密码
								.post(ClientResponse.class);

					} catch (Exception e) {

						MusicDao.fileErrorOutputStream(existSongId + "," + e.toString() + "\n");
						// 休息2s重试一次看看
						Thread.sleep(2000);
						commentResponse = commentResource
								.header("Referer", "http://music.163.com/song?id=" + existSongId.toString())
								.entity("params=MjdcjJ0FbCPJR90VfRep5nnE4NMItX4f3xjdJPlbvatfoUbt6g4RBELo7JcOqRU176SC343L%2BLsFPRwEVdVFrlJf0tE2ATMx%2FtnCNjbdgroyG4OQO4GblXM51c5%2F45yoDigdNTiejTFu%2Fq7wOffZ1NhKJmWbfBlPDPpJ%2B07ijVergSaH4cq6QKrpSivU7Qp7&encSecKey=6ba5031c2f2d3559a45787c7d4b8dada1c154de42576a5ae137e7e5c04096c303cfb6d19a9fe46391906007cdc714c9a7043e76d2bef9320a2a3494b3b86a5880261480486400e717cd1f71141db78e0fb39727c5b30b80fb6f62a1b03bccc59923207f125f1fa4edd07172733e14480d68c3ecdee9613235e9c30bc5f712b11",
										MediaType.APPLICATION_FORM_URLENCODED_TYPE) // 密码
								.post(ClientResponse.class);
						String commentInfo = IOUtils.toString(commentResponse.getEntityInputStream());
						Map<String, Object> key_value = mapper.readValue(commentInfo, HashMap.class);
						Double commentCountDbl = (Double) key_value.get("total");
						// if (commentCountDbl == null || name == null || songId
						// == null || artist == null
						// || (hMusic == null && bMusic == null))
						if (commentCountDbl == null || name == null || songId == null || artist == null) {
							// MusicDao.fileAllOutputStream(dateFormat.format(new
							// Date()) + ":" +
							// (String)Thread.currentThread().getName().replace("-",
							// "") + " 有空值不插入:"+ songId +
							// ","+name+","+artist+","+commentCountDbl+","+hMusic+","+bMusic+"\n");
							MusicDao.fileOutputStream(dateFormat.format(new Date()) + ":"
									+ (String) Thread.currentThread().getName().replace("-", "") + " 有空值不插入:" + songId
									+ "," + name + "," + artist + "," + commentCountDbl + "\n");
						}
						Integer commentCount = commentCountDbl.intValue();
						if (commentCount >= 1000) {
							musicDataHandler.putCount(existSongId, commentCount, name, artist);

						} else {
							// MusicDao.fileAllOutputStream(dateFormat.format(new
							// Date()) + ":" +
							// (String)Thread.currentThread().getName().replace("-",
							// "") + "
							// 评论数小于1K，不插入:"+existSongId+","+name+","+artist+","+commentCount+"\n");
							MusicDao.fileOutputStream(dateFormat.format(new Date()) + ":"
									+ (String) Thread.currentThread().getName().replace("-", "") + " 评论数小于1K，不插入:"
									+ existSongId + "," + name + "," + artist + "," + commentCount + "\n");

						}
					}

					/*
					 * System.out.println(commentResource .header("Referer",
					 * "http://music.163.com/song?id=" + existSongId.toString())
					 * .entity(
					 * "params=MjdcjJ0FbCPJR90VfRep5nnE4NMItX4f3xjdJPlbvatfoUbt6g4RBELo7JcOqRU176SC343L%2BLsFPRwEVdVFrlJf0tE2ATMx%2FtnCNjbdgroyG4OQO4GblXM51c5%2F45yoDigdNTiejTFu%2Fq7wOffZ1NhKJmWbfBlPDPpJ%2B07ijVergSaH4cq6QKrpSivU7Qp7&encSecKey=6ba5031c2f2d3559a45787c7d4b8dada1c154de42576a5ae137e7e5c04096c303cfb6d19a9fe46391906007cdc714c9a7043e76d2bef9320a2a3494b3b86a5880261480486400e717cd1f71141db78e0fb39727c5b30b80fb6f62a1b03bccc59923207f125f1fa4edd07172733e14480d68c3ecdee9613235e9c30bc5f712b11",
					 * MediaType.APPLICATION_FORM_URLENCODED_TYPE) // 密码
					 * .post(ClientResponse.class));
					 */

					// 没有异常继续往下走
					String commentInfo = IOUtils.toString(commentResponse.getEntityInputStream());
					Map<String, Object> key_value = mapper.readValue(commentInfo, HashMap.class);
					Object commentCountDbl = key_value.get("total");
					if (commentCountDbl == null || name == null || songId == null || artist == null) {
						// MusicDao.fileAllOutputStream(dateFormat.format(new
						// Date()) + ":" +
						// (String)Thread.currentThread().getName().replace("-",
						// "") + " 有空值不插入:"+ songId +
						// ","+name+","+artist+","+commentCountDbl+","+hMusic+","+bMusic+"\n");
						MusicDao.fileOutputStream(dateFormat.format(new Date()) + ":"
								+ (String) Thread.currentThread().getName().replace("-", "") + " 有空值不插入:" + songId + ","
								+ name + "," + artist + "," + commentCountDbl + "\n");
					}
					Integer commentCount = ((Integer) commentCountDbl).intValue();
					if (commentCount >= 1000) {
						musicDataHandler.putCount(existSongId, commentCount, name, artist);

					} else {
						// MusicDao.fileAllOutputStream(dateFormat.format(new
						// Date()) + ":" +
						// (String)Thread.currentThread().getName().replace("-",
						// "") + "
						// 评论数小于1K，不插入:"+existSongId+","+name+","+artist+","+commentCount+"\n");
						// MusicDao.fileOutputStream(dateFormat.format(new
						// Date()) + ":"
						// + (String)
						// Thread.currentThread().getName().replace("-", "") + "
						// 评论数小于1K，不插入:"
						// + existSongId + "," + name + "," + artist + "," +
						// commentCount + "\n");

					}

					Thread.sleep(5000);
				}

			}

			catch (Exception e1) {

				System.out.println(dateFormat.format(new Date()) + ":"
						+ (String) Thread.currentThread().getName().replace("-", "") + "," + songId + " throw err:\n");
				e1.printStackTrace();
				try {
					MusicDao.fileErrorOutputStream(e1.toString() + "\n");
				} catch (IOException e) {

					e.printStackTrace();
				}
				// Thread.sleep(interval);
			}

			// } catch (InterruptedException e) {
			// }
			// }
		}
	}
}
