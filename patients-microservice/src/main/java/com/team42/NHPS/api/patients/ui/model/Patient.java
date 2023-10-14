package com.team42.NHPS.api.patients.ui.model;

public class Patient {
	private String userId;
	private String albumId;
	private String title;
	
    public Patient() {
    	
    }

	public Patient(String userId, String albumId, String title) {
		this.userId = userId;
		this.albumId = albumId;
		this.title = title;
	}

	public String getUserId() {
		return userId;
	}

	public String getAlbumId() {
		return albumId;
	}

	public String getTitle() {
		return title;
	}
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
}
