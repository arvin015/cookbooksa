package com.sky.cookbooksa.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author arvin.li
 *
 */
public class RandomUtil {

	public RandomUtil() {}

	/**
	 * 获取制定范围的一个随机数
	 * @param range 随机数范围[0-range)
	 * @return
	 */
	public static int randomNum(int range) {
		return (int)(Math.random() * range);
	}

	/**
	 * 获取制定范围内的一组随机数---集合
	 * @param num   随机数个数
	 * @param range 随机数范围[0-range)
	 * @return
	 */
	public static ArrayList<Integer> notrepetitionNumList(int num, int range) {
		ArrayList<Integer> randomList = new ArrayList<Integer>();
		Random random = new Random();
		boolean[] bool = new boolean[range];
		int rand;
		for (int i = 0; i < num; i++) {
			do {
				rand = random.nextInt(range);
			} while (bool[rand]);
			bool[rand] = true;
			randomList.add(rand);
		}
		return randomList;
	};

	/**
	 * 获取制定范围内的一组随机数---数组
	 * @param num   随机数个数
	 * @param range 随机数范围[0-range)
	 * @return
	 */
	public static int[] notrepetitionNums(int num, int range){
		ArrayList<Integer> list = notrepetitionNumList(num, range);

		int[] rans = null;

		if(list != null){
			rans = new int[list.size()];
		}

		for(int i = 0; i < list.size(); i++){
			rans[i] = list.get(i);
		}

		return rans;
	}
}
