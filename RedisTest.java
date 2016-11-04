package com.yali.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;

public class RedisTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		 final String watchkeys = "watchkeys";
	        ExecutorService executor = Executors.newFixedThreadPool(20);

	        final Jedis jedis = new Jedis("10.100.22.78", 6379);
	        jedis.set(watchkeys, "0");// 重置watchkeys为0
	        jedis.del("setsucc", "setfail");// 清空抢成功的，与没有成功的
	        jedis.close();

	        for (int i = 0; i < 10; i++) {// 测试一万人同时访问
	            executor.execute(new MyRunnable());
	        }
	        executor.shutdown();
	}

}
