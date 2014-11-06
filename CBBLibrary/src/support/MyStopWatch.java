package support;

/*
Copyright (c) 2005, Corey Goldberg

StopWatch.java is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
*/
//I have modified this class from the original

public class MyStopWatch {
	private long startTime = 0;
	private long totalRunningTime=0;
	private boolean running = false;
	
//	public MyStopWatch(){
//	    this.startTime = System.currentTimeMillis();
//	}
	public void start() {
	    this.startTime = System.currentTimeMillis();
	    this.running = true;
	} 
	
	public void pause() {
	    totalRunningTime+=System.currentTimeMillis() - startTime;
	    this.running = false;
	}
	
	//elaspsed time in milliseconds
	public long getElapsedTime() {
	    if (running) {
	    	totalRunningTime+=(System.currentTimeMillis() - startTime);
	    	start();
	    }
	    return totalRunningTime;
	}
	public void reset(){
		totalRunningTime=0;
		this.running = false;
	}
}
