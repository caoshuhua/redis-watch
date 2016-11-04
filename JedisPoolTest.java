package com.yali.test;

import redis.clients.jedis.Jedis;

public class JedisPoolTest {
	
	public static void main(String[] args) {
		/**
		 * 池中获取一个Jedis对象
		 */
		Jedis jedis = JedisConnPool.getJedisResource();
		String keys = "name";
		/**
		 * 删数据
		 */
		jedis.del(keys);
		/**
		 * 存数据
		 */
		jedis.set(keys, "yali");
		/**
		 * 取数据
		 */
		String value = jedis.get(keys);
		System.out.println(value);
		JedisConnPool.returnJedisResource(jedis);  

	}
}
