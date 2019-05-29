package me.champ.zm.utils;

import java.util.Random;

public class Util {
	
	public static int getRandomNumberInRange(Random r, int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

}
