package com.yali.test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
/**
 * 
<?php
header("content-type:text/html;charset=utf-8");
$redis = new redis();
$result = $redis->connect('10.10.10.119', 6379);
$mywatchkey = $redis->get("mywatchkey");
$rob_total = 100;   //抢购数量
if($mywatchkey<$rob_total){
    $redis->watch("mywatchkey");
    $redis->multi();
    
    //设置延迟，方便测试效果。
    sleep(5);
    //插入抢购数据
    $redis->hSet("mywatchlist","user_id_".mt_rand(1, 9999),time());
    $redis->set("mywatchkey",$mywatchkey+1);
    $rob_result = $redis->exec();
    if($rob_result){
        $mywatchlist = $redis->hGetAll("mywatchlist");
        echo "抢购成功！<br/>";
        echo "剩余数量：".($rob_total-$mywatchkey-1)."<br/>";
        echo "用户列表：<pre>";
        var_dump($mywatchlist);
    }else{
        echo "手气不好，再抢购！";exit;
    }
}
?>

Redis使用watch完成秒杀抢购功能：
使用redis中两个key完成秒杀抢购功能，mywatchkey用于存储抢购数量和mywatchlist用户存储抢购列表。
它的优点如下：
1. 首先选用内存数据库来抢购速度极快。
2. 速度快并发自然没不是问题。
3. 使用悲观锁，会迅速增加系统资源。
4. 比队列强的多，队列会使你的内存数据库资源瞬间爆棚。
5. 使用乐观锁，达到综合需求。
 * @author henry
 *
 */
public class MyRunnable2 implements Runnable{
	
	String watchkeys = "watchkeys";// 监视keys
    Jedis jedis = new Jedis("10.100.22.78", 6379);
    int total = 10;
    
    public MyRunnable2() {
    	
    }

	@Override
	public void run() {
		try{
    		jedis.watch(watchkeys);// watchkeys
    		
    		String userifo = UUID.randomUUID().toString();
    		System.out.println("userifo:"+userifo);
    		
    		String val = jedis.get("watchkeys");
    		int cnt = Integer.valueOf(val);
    		System.out.println("cnt:"+cnt);
    		if(cnt < total){
    			Transaction tx = jedis.multi();
    			tx.incr("watchkeys");
    			
    			List<Object> list = tx.exec();// 提交事务，如果此时watchkeys被改动了，则返回null
                System.out.println(list.size());
                
                if (list != null) {
                    System.out.println("用户：" + userifo + "抢购成功，当前抢购成功人数:"
                            + (cnt + 1));
                    /* 抢购成功业务逻辑 */
                    jedis.sadd("setsucc", userifo);
                } else {
                    System.out.println("用户：" + userifo + "抢购失败");
                    /* 抢购失败业务逻辑 */
                    jedis.sadd("setfail", userifo);
                }
    			
    		}else{
    			System.out.println("userifo->"+userifo+",抢购失败！");
    		}
    		
    	}catch (Exception e) {
    		jedis.close();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		 final String watchkeys = "watchkeys";
	        ExecutorService executor = Executors.newFixedThreadPool(20);

	        final Jedis jedis = new Jedis("10.100.22.78", 6379);
	        jedis.set(watchkeys, "0");// 重置watchkeys为0
	        jedis.del("setsucc", "setfail");// 清空抢成功的，与没有成功的
	        jedis.close();

	        for (int i = 0; i < 40; i++) {// 测试一万人同时访问
	            executor.execute(new MyRunnable2());
	            Thread.sleep(50);
	        }
	        executor.shutdown();
	}

}
