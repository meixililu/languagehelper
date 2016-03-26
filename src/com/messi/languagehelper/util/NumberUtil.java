package com.messi.languagehelper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class NumberUtil {

	public static int getRandomNumber(int range){
		return (int)(Math.random()*range);
	}
	
	/**
	 * String转int
	 */
	public static int StringToInt(String str) {
		try {
			int num = Integer.parseInt(str.replace("+", ""));
			return num;
		} catch (Exception e) {
			outputException(e, "StringToInt");
		}
		return 0;
	}

	/**
	 * String转long
	 */
	public static long StringToLong(String str) {
		try {
			long num = Long.parseLong(str);
			return num;
		} catch (Exception e) {
			outputException(e, "StringToLong");
		}
		return 0;
	}

	/**
	 * String转double
	 */
	public static double StringToDouble(String str) {
		try {
			double num = Double.parseDouble(str);
			return num;
		} catch (Exception e) {
			outputException(e, "StringToDouble");
		}
		return 0;
	}

	/**
	 * String转float
	 */
	public static float StringToFloat(String str) {
		try {
			float num = Float.parseFloat(str);
			return num;
		} catch (Exception e) {
			outputException(e, "StringToFloat");
		}
		return 0;
	}
	
	public static int[] getRandomNumberLimit(int max, int star, int size, int limit){
		int[] result = new int[size];
		int i = 0;
		boolean isStop = false;
		while(!isStop){
			int temp = randomNumber(max)+star;
			if(temp != limit){
				result[i] = temp;
				i++;
			}
			if(i == size){
				isStop = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * 不可重复
	 */
	public static int[] getNumberOrderWithoutRepeat(int max, int init, int size, boolean isSort) {
		int[] result = new int[size];
		List<Integer> list = new ArrayList<Integer>();
		Random r = new Random();

		for (int i = init, temSize = max + init; i < temSize; i++) {
			list.add(i);
		}

		for (int j = 0; j < size; j++) {
			int index = r.nextInt(list.size());
			result[j] = list.get(index);
			list.remove(index);
		}
		if(isSort) sort(result);
		return result;
	}
	
	/**
	 * 不可重复
	 */
	public static List<Integer> getNumberOrderNotRepeat(int max, int min) {
		List<Integer> num = new ArrayList<Integer>();
		for(int i=0;i<(max-min+1);i++){
			num.add(i);
		}
		Collections.shuffle(num);
		return num;
	}

	/**
	 * 可重复
	 */
	public static int[] getNumberOrderWithRepeat(int total, int number, int init, boolean isSort) {
		int[] result = new int[number];
		Random r = new Random();
		for (int j = 0; j < number; j++) {
			result[j] = r.nextInt(total + init);
		}
		if(isSort) sort(result);
		return result;
	}
	
	/**
	 * 排序
	 * @param array
	 */
	public static void sort(int[] array) {
		int temp;
		for (int i = 0; i < array.length; i++) {
			boolean flag = false;
			for (int j = 0; j < array.length - 1 - i; j++) {
				if (array[j] > array[j + 1]) {
					temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
					flag = true;
				}
			}
			if (!flag) {
				break;
			}
		}
	}
	
	public static int hundred_mark(float score){
		return (int)(score / 5 * 100);
	}
	
	/**产生一个随机数，不包括max，0到max-1
	 * @param max
	 * @return
	 */
	public static int randomNumber(int max){
		return new Random().nextInt(max);
	}
	
	public static int randomNumberRange(int min,int max){
		return new Random().nextInt(max)%(max-min+1) + min;
	}
	
	/**
	 * 在测试的情况下进行Exception 抛出
	 * @param e 当前相关Exception
	 * @param prompt 提示的内容 用方法名
	 */
	private static void outputException(Exception e, String prompt){
		LogUtil.ExceptionLog("ConversionUtil -- " + prompt);
		e.printStackTrace();
	}
}
