package com.rackspace.cloud.servers.api.client;

public class Backup {
	
	private static final String[] weeklyBackupValues = {"DISABLED", "SUNDAY", "MONDAY", 
			"TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
	
	private static final String[] dailyBackupValues = {"DISABLED", "H_0000_0200", "H_0200_0400", "H_0400_0600", "H_0600_0800",
					"H_0800_1000", "H_1000_1200", "H_1200_1400", "H_1400_1600", "H_1600_1800", "H_1800_2000",
					"H_2000_2200", "H_2200_0000"};
	
	public static String getWeeklyValue(int i){
		return weeklyBackupValues[i];
	}
	
	public static String getDailyValue(int i){
		return dailyBackupValues[i];
	}

}
