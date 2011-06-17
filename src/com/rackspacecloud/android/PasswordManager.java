package com.rackspacecloud.android;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.content.SharedPreferences;

public class PasswordManager {
	
	private SharedPreferences settings;
	
	public PasswordManager(SharedPreferences sp){
		settings = sp;
	}
	
	/*
	 * checks the parameter string against the stored
	 * password
	 */
	public boolean verifyEnteredPassword(String password) {
		return Arrays.toString(getHash(password)).equals(getStoredPassword());
	}
	
	/*
	 * return the hash of the password that is stored in
	 * shared preferences
	 */
	private String getStoredPassword(){
		return settings.getString(Preferences.PREF_KEY_PASSCODE_HASH, "");
	}
	
	/*
	 * turns off password requirement 
	 */
	public void turnOffPassword() {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Preferences.PREF_KEY_PASSCODE_HASH, "");
		editor.putBoolean(Preferences.PREF_KEY_PASSWORD_LOCK, false);
		editor.commit();
	}
	
	/*
	 * submits a password change into memory
	 * stores the sha-256 hash of the password
	 */
	private void storeNewPassword(String hashedPassword) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Preferences.PREF_KEY_PASSCODE_HASH, hashedPassword);
		editor.putBoolean(Preferences.PREF_KEY_PASSWORD_LOCK, true);
		editor.commit();
	}
	
	/*
	 * changes the password for
	 */
	public void changePassword(String password) {
		storeNewPassword(Arrays.toString(getHash(password)));
	}
	
	/*
	 * returns the sha-256 hash for a given
	 * string
	 */
	private byte[] getHash(String password) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("SHA-256");
			m.update(password.getBytes());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m.digest();
	}
	
	/*
	 * returns true if their is a password requirement
	 */
	public boolean hasPassword() {
		return settings.getBoolean(Preferences.PREF_KEY_PASSWORD_LOCK, false);
	}

}
