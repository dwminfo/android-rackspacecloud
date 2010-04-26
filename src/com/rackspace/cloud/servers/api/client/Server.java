/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

/**
 * @author mike
 *
 */
public class Server extends Entity {

	private String status;
	private String progress;
	private String hostId;
	private String flavorId;
	private String imageId;
	private String[] publicIpAddresses;
	private String[] privateIpAddresses;
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the progress
	 */
	public String getProgress() {
		return progress;
	}
	/**
	 * @param progress the progress to set
	 */
	public void setProgress(String progress) {
		this.progress = progress;
	}
	/**
	 * @return the hostId
	 */
	public String getHostId() {
		return hostId;
	}
	/**
	 * @param hostId the hostId to set
	 */
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	/**
	 * @return the flavorId
	 */
	public String getFlavorId() {
		return flavorId;
	}
	/**
	 * @param flavorId the flavorId to set
	 */
	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}
	/**
	 * @return the imageId
	 */
	public String getImageId() {
		return imageId;
	}
	/**
	 * @param imageId the imageId to set
	 */
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	/**
	 * @return the publicIpAddresses
	 */
	public String[] getPublicIpAddresses() {
		return publicIpAddresses;
	}
	/**
	 * @param publicIpAddresses the publicIpAddresses to set
	 */
	public void setPublicIpAddresses(String[] publicIpAddresses) {
		this.publicIpAddresses = publicIpAddresses;
	}
	/**
	 * @return the privateIpAddresses
	 */
	public String[] getPrivateIpAddresses() {
		return privateIpAddresses;
	}
	/**
	 * @param privateIpAddresses the privateIpAddresses to set
	 */
	public void setPrivateIpAddresses(String[] privateIpAddresses) {
		this.privateIpAddresses = privateIpAddresses;
	}
	
	
}
