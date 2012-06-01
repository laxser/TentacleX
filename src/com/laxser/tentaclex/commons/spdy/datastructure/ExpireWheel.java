package com.laxser.tentaclex.commons.spdy.datastructure;

/**
 * 
 * 存储等步长增长的id以及其对应的value，存储空间是定长的，空间用完之后，会从最老的数据开始覆盖，
 * 也就是说老的数据会过期(Expire)，实现的时候使用的循环数组，就像一个轮子滚滚向前，于是叫做ExpireWheel。
 * 
 * 这个数据结构的put个get操作的时间复杂度都是O(1)，且没有锁，非常高效。
 * 
 * 但是这个数据结构是一个弱约束的结构，需要外部调用程序来保证传入的id是等步长增长的，即
 * 1,2,3,5...或2,4,6,7,8...或1,3,5,7...等。
 * 
 *@author laxser  Date 2012-6-1 上午9:03:27
@contact [duqifan@gmail.com]
@ExpireWheel.java

 * @param <T>
 */
public class ExpireWheel<T> {

	/**
	 * 可选的一种CAPACITY设置，2^18即16384
	 */
	public static final int CAPACITY_2P14 = (int)Math.pow(2, 14);
	
	/**
	 * 可选的一种CAPACITY设置，2^18即65536
	 */
	public static final int CAPACITY_2P16 = (int)Math.pow(2, 16);
	
	/**
	 * 可选的一种CAPACITY设置，2^18即262144
	 */
	public static final int CAPACITY_2P18 = (int)Math.pow(2, 18);
	
	/**
	 * 可选的一种CAPACITY设置，2^19即524288
	 */
	public static final int CAPACITY_2P19 = (int)Math.pow(2, 19);
	
	/**
	 * id自增的步长
	 */
	private final int step;

	/**
	 * 循环数组
	 */
	private Entry<T>[] wheel;

	/**
	 * @param capacity 容量
	 * @param step id自增步长
	 */
	@SuppressWarnings("unchecked")
	public ExpireWheel(int capacity, int step) {
		if (step < 1) {
			throw new IllegalArgumentException();
		}
		this.step = step;
		wheel = new Entry[capacity];
	}

	/**
	 * 存入一个对象
	 * @param id
	 * @param obj
	 */
	public void put(int id, T obj) {
		int index = calculateIndex(id);
		wheel[index] = new Entry<T>(id, obj);
	}

	/**
	 * 取出一个对象，如果对象不存在，则返回null
	 * @param id
	 * @return
	 */
	public T get(int id) {
		int index = calculateIndex(id);
		Entry<T> entry = wheel[index];
		
		//需要比较目标位置存放的对象的id与指定的id是否相同
		//如果id不同的话则说明要找的对象不存在或者已经过期了
		if (entry != null && entry.id == id) {
			return entry.obj;
		} else {
			return null;
		}
	}
	
	/**
	 * 删除一个对象
	 * 
	 * @param id
	 * @return
	 */
	public T remove(int id) {
		int index = calculateIndex(id);
		Entry<T> entry = wheel[index];
		if (entry != null && entry.id == id) {
			wheel[index] = null;
			return entry.obj;
		} else {
			return null;
		}
	}
	
	/**
	 * 计算指定id存放于数组的index
	 * 
	 * @param id
	 * @return
	 */
	private int calculateIndex(int id) {
		return id / step % wheel.length;
	}
	
	private static class Entry<T> {
		
		private int id;
		private T obj;
		
		Entry(int id, T obj) {
			this.id = id;
			this.obj = obj;
		}
		
		public String toString() {
			return id + ":" + obj.toString();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(CAPACITY_2P14);
		System.out.println(CAPACITY_2P16);
		System.out.println(CAPACITY_2P18);
		System.out.println(CAPACITY_2P19);
	}
}
