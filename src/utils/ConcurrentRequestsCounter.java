package utils;

public class ConcurrentRequestsCounter {
	private static final int BUF_SIZE = 10;
	
	private volatile int concurrentRequests = 0;
	private long startTime = 0L;
	private long endTime = 0L;
	private long elapsedTime = 0L;
	
	private long lastTimes[] = new long[BUF_SIZE];
	private int lastTimesIdx = 0;
	
	public synchronized void increment() {
		startTime = System.currentTimeMillis();	
		concurrentRequests++;
	}
	
	public synchronized void decrement() {
		
		if (concurrentRequests > 0)
			concurrentRequests--;
		
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;	
		// Discard requests for favicon - are immediately returned and spoil avg result
		
		if (elapsedTime <= 1)
			return;
		
		lastTimesIdx = lastTimesIdx > BUF_SIZE-1 ? 0 : lastTimesIdx;
		lastTimes[lastTimesIdx++] = elapsedTime;	
	}	
	
	public synchronized int getRequestNum() {
		return concurrentRequests;
	}
	
	public synchronized long getLastRequestTime() {
		return elapsedTime;
	}
	
	public synchronized long getAverageRequestTime() {
		long sum = 0L;
		long cnt = 0L;
				
		for (long t : lastTimes)
			if (t > 0L ) {
				sum += t;
				cnt++;
			}		
		if (cnt == 0) cnt = 1;		
		return sum/cnt;
	}
	

}
