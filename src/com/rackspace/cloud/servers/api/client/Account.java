/**
 * 
 */
package com.rackspace.cloud.servers.api.client;



/**
 * @author Mike Mayo - mike.mayo@rackspace.com - twitter.com/greenisus
 *
 */
public class Account {
	
	private String username;
	private String apiKey;
	private String authToken;
	private String authServer;
	private String serverUrl;
	private String storageUrl;
	private String storageToken;
	private String cdnManagementUrl;
	private static Account currentAccount;
	
	
	public static Account getAccount(){
		return currentAccount;
	}
	
	public static void setAccount(Account account){
		Account.currentAccount = account;
	}
	
	/**
	 * @return the serverUrl
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * @return the storageUrl
	 */
	public String getStorageUrl() {
		return storageUrl;
	}
	/**
	 * @return the storageToken
	 */
	public String getStorageToken() {
		return storageToken;
	}
	/**
	 * @param storageUrl the storageUrl to set
	 */
	public void setStorageUrl(String storageUrl) {
		this.storageUrl = storageUrl;
	}

	/**
	 * @return the cdnManagementUrl
	 */
	public String getCdnManagementUrl() {
		return cdnManagementUrl;
	}

	/**
	 * @param cdnManagementUrl the cdnManagementUrl to set
	 */
	public void setCdnManagementUrl(String cdnManagementUrl) {
		this.cdnManagementUrl = cdnManagementUrl;
	}

	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * @return the authToken
	 */
	public String getAuthServer() {
		return authServer;
	}

	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthServer(String authServer) {
		this.authServer = authServer;
	}

	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}
	
	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
   /**
    */
	public void setStorageToken(String storageToken) {
		this.storageToken = storageToken;
		
	}
	
}

