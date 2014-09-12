package com.messi.languagehelper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.messi.languagehelper.R;


public class ColorUtil {
	
	public static int styleColor1[] = {
		R.color.style1_color1, R.color.style1_color2, R.color.style1_color3, R.color.style1_color4, R.color.style1_color5, 
		R.color.style2_color1, R.color.style2_color2, R.color.style2_color3, R.color.style2_color4, 
		R.color.style3_color1, R.color.style4_color1, R.color.style4_color3};
		
	public static int getRadomColor(){
		int size = styleColor1.length;
		int position = (int)(Math.random() * size);
		LogUtil.DefalutLog("position:"+position);
		return styleColor1[position];
	}
	
	public static void setColor(int size, int[] colors){
		int[] positions = repeatNo(styleColor1.length, size, 0);
		for(int i=0; i<size; i++){
			colors[i] = styleColor1[positions[i]];
		}
	}
	
	private static int[] repeatNo(int total, int number, int init) {
		int[] result = new int[number];
		List<Integer> list = new ArrayList<Integer>();
		Random r = new Random();

		for (int i = init, size = total + init; i < size; i++) {
			list.add(i);
		}

		for (int j = 0; j < number; j++) {
			int index = r.nextInt(list.size());
			result[j] = list.get(index);
			list.remove(index);
		}
		return result;
	}
}
