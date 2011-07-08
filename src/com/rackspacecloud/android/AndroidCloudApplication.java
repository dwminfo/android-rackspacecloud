package com.rackspacecloud.android;

import java.util.ArrayList;

import com.rackspace.cloud.files.api.client.ContainerObjects;

import android.app.Application;

public class AndroidCloudApplication extends Application {

	private boolean taskProcessing;
	private boolean deletingObjectProcessing;
	private boolean deletingContainerProcessing;
	private ArrayList<ContainerObjects> curDirFiles;
	
	public void setAddingObject(boolean processing){
		taskProcessing = processing;
	}
	
	public boolean isAddingObject(){
		return taskProcessing;
	}
	
	public void setDeleteingObject(boolean processing){
		deletingObjectProcessing = processing;
	}
	
	public boolean isDeletingObject(){
		return deletingObjectProcessing;
	}
	
	public void setDeletingContainer(boolean processing){
		deletingContainerProcessing = processing;
	}
	
	public boolean isDeletingContainer(){
		return deletingContainerProcessing;
	}
	
	public ArrayList<ContainerObjects> getCurFiles(){
		return curDirFiles;
	}
	
	public void setCurFiles(ArrayList<ContainerObjects> files){
		curDirFiles = new ArrayList<ContainerObjects>();
		for(ContainerObjects obj : files){
			curDirFiles.add(obj);
		}
			
	}
}
