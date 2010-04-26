/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

import java.util.ArrayList;

/**
 * @author mike
 *
 */
public class EntityList {
	
	private long lastModified;
	private Entity entities[];
	private int index;

	public EntityList(ArrayList<Entity> entities) {
		this.entities = (Entity[]) entities.toArray();
		index = 0;
	}
		
	public EntityList(Entity entities[]) {
		this.entities = entities;
		index = 0;
	}
	
	public boolean isEmpty() {
		return entities == null || entities.length == 0;
	}
	
	public boolean hasNext() {
		if (isEmpty()) {
			return false;
		} else {
			if (index == entities.length - 1) {
				// TODO: try to get more
				return false;
			} else {
				return true;
			}
		}
	}
	
	public Entity next() {
		if (hasNext()) {
			return entities[index++];
		} else {
			return null;
		}
	}
	
	public void reset() {
		entities = null;
		index = 0;
	}
	
	/**
	 * @return the entities
	 */
	public Entity[] getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(Entity[] entities) {
		this.entities = entities;
	}

	public void delta() {
		
	}
	
	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}
