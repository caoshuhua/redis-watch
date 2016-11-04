package com.yali.test;

import redis.clients.jedis.Jedis;

public class JedisTest {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("10.100.22.100");
		String keys = "cloud_store_orgCode";
//		/**
//		 * 删数据
//		 */
//		jedis.del(keys);
//		/**
//		 * 存数据
//		 */
//		jedis.set(keys, "abc");
		/**
		 * 取数据
		 */
		String value = jedis.get(keys);

		System.out.println(value);
	}
}
