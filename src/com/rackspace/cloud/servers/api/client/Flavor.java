/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

/**
 * @author mike
 *
 */
public class Flavor extends Entity {

	//<flavors xmlns="http://docs.rackspacecloud.com/servers/api/v1.0">
	//<flavor disk="10" ram="256" name="256 server" id="1"/>
	//<flavor disk="20" ram="512" name="512 server" id="2"/><flavor disk="40" ram="1024" name="1GB server" id="3"/><flavor disk="80" ram="2048" name="2GB server" id="4"/><flavor disk="160" ram="4096" name="4GB server" id="5"/><flavor disk="320" ram="8192" name="8GB server" id="6"/><flavor disk="620" ram="15872" name="15.5GB server" id="7"/></flavors>
	
	private String disk;
	private String ram;
	
	/**
	 * @return the disk
	 */
	public String getDisk() {
		return disk;
	}
	/**
	 * @param disk the disk to set
	 */
	public void setDisk(String disk) {
		this.disk = disk;
	}
	/**
	 * @return the ram
	 */
	public String getRam() {
		return ram;
	}
	/**
	 * @param ram the ram to set
	 */
	public void setRam(String ram) {
		this.ram = ram;
	}
}
