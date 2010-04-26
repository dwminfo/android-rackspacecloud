/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

import com.rackspacecloud.android.R;

/**
 * @author mike
 *
 */
public class Image extends Entity {

	//<images xmlns="http://docs.rackspacecloud.com/servers/api/v1.0">
	//<image status="ACTIVE" updated="2009-08-26T14:59:51-05:00" name="Gentoo 2008.0" id="3"/>
	//<image status="ACTIVE" updated="2009-08-26T14:59:52-05:00" name="Debian 5.0 (lenny)" id="4"/><image status="ACTIVE" updated="2009-08-26T14:59:52-05:00" name="CentOS 5.3" id="7"/><image status="ACTIVE" updated="2009-12-07T16:22:14-06:00" name="Ubuntu 9.04 (jaunty)" id="8"/><image status="ACTIVE" updated="2009-08-26T14:59:54-05:00" name="Arch 2009.02" id="9"/><image status="ACTIVE" updated="2009-08-26T14:59:54-05:00" name="Ubuntu 8.04.2 LTS (hardy)" id="10"/><image status="ACTIVE" updated="2009-08-26T14:59:54-05:00" name="Ubuntu 8.10 (intrepid)" id="11"/><image status="ACTIVE" updated="2010-03-05T13:40:32-06:00" name="Red Hat EL 5.3" id="12"/><image status="ACTIVE" updated="2009-12-08T13:50:45-06:00" name="Fedora 11 (Leonidas)" id="13"/><image status="ACTIVE" updated="2009-12-15T15:37:22-06:00" name="Red Hat EL 5.4" id="14"/><image status="ACTIVE" updated="2009-12-15T15:43:59-06:00" name="Fedora 12 (Constantine)" id="17"/><image status="ACTIVE" updated="2009-12-15T15:43:39-06:00" name="Gentoo 10.1" id="19"/><image status="ACTIVE" updated="2010-01-26T12:05:53-06:00" name="Windows Server 2003 R2 SP2 x64" id="23"/><image status="ACTIVE" updated="2010-01-26T12:07:04-06:00" name="Windows Server 2008 SP2 x64" id="24"/><image status="ACTIVE" updated="2010-01-26T12:07:17-06:00" name="Windows Server 2008 R2 x64" id="28"/><image status="ACTIVE" updated="2010-01-26T12:07:32-06:00" name="Windows Server 2003 R2 SP2 x86" id="29"/><image status="ACTIVE" updated="2010-01-26T12:07:44-06:00" name="Windows Server 2008 SP2 x86" id="31"/><image status="ACTIVE" updated="2010-04-03T04:16:12-05:00" name="Oracle EL Server Release 5 Update 4" id="40"/><image status="ACTIVE" updated="2010-04-06T13:10:55-05:00" name="Oracle EL JeOS Release 5 Update 3" id="41"/><image status="ACTIVE" updated="2009-11-06T05:09:40-06:00" name="Ubuntu 9.10 (karmic)" id="14362"/><image status="ACTIVE" updated="2009-12-16T01:02:17-06:00" name="CentOS 5.4" id="187811"/></images>
	
	private String status;
	private String updated;
	
	public int iconResourceId() {
		int iconResourceId = R.drawable.cloudservers_icon; // default if unknown
		
		if ("2".equals(getId())) {
			iconResourceId = R.drawable.centos_icon;
		} else if ("3".equals(getId())) {
			iconResourceId = R.drawable.gentoo_icon;
		} else if ("4".equals(getId())) {
			iconResourceId = R.drawable.debian_icon;
		} else if ("5".equals(getId())) {
			iconResourceId = R.drawable.fedora_icon;
		} else if ("7".equals(getId())) {
			iconResourceId = R.drawable.centos_icon;
		} else if ("8".equals(getId())) {
			iconResourceId = R.drawable.ubuntu_icon;
		} else if ("9".equals(getId())) {
			iconResourceId = R.drawable.arch_icon;
		} else if ("10".equals(getId())) {
			iconResourceId = R.drawable.ubuntu_icon;
		} else if ("11".equals(getId())) {
			iconResourceId = R.drawable.ubuntu_icon;
		} else if ("12".equals(getId())) {
			iconResourceId = R.drawable.redhat_icon;
		} else if ("13".equals(getId())) {
			iconResourceId = R.drawable.fedora_icon;
		} else if ("4056".equals(getId())) {
			iconResourceId = R.drawable.fedora_icon;
		} else if ("14362".equals(getId())) {
			iconResourceId = R.drawable.ubuntu_icon;
		} else if ("23".equals(getId())) {
			iconResourceId = R.drawable.windows_icon;
		} else if ("24".equals(getId())) {
			iconResourceId = R.drawable.windows_icon;
		} else if ("28".equals(getId())) {
			iconResourceId = R.drawable.windows_icon;
		} else if ("29".equals(getId())) {
			iconResourceId = R.drawable.windows_icon;
		} else if ("31".equals(getId())) {
			iconResourceId = R.drawable.windows_icon;
		} else if ("14".equals(getId())) {
			iconResourceId = R.drawable.redhat_icon;
		} else if ("17".equals(getId())) {
			iconResourceId = R.drawable.fedora_icon;
		} else if ("19".equals(getId())) {
			iconResourceId = R.drawable.gentoo_icon;
		} else if ("187811".equals(getId())) {
			iconResourceId = R.drawable.centos_icon;
		}
		
		return iconResourceId;
	}
	
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
	 * @return the updated
	 */
	public String getUpdated() {
		return updated;
	}
	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(String updated) {
		this.updated = updated;
	}
}
