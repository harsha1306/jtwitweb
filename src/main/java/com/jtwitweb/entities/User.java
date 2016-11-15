package com.jtwitweb.entities;

import twitter4j.ResponseList;
import twitter4j.Status;

public class User {
	Long userID;
	twitter4j.User userProfile;
	public ResponseList<Status> userTimeline;
	public ResponseList<Status> homeTimeline;

	public ResponseList<Status> getUserTweets() {
		return userTimeline;
	}

	public void setUserTweets(ResponseList<Status> userTweets) {
		this.userTimeline = userTweets;
	}

	public ResponseList<Status> getHomeTimeline() {
		return homeTimeline;
	}

	public void setHomeTimeline(ResponseList<Status> homeTimeline) {
		this.homeTimeline = homeTimeline;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public void setUserProfile(twitter4j.User showUser) {
		this.userProfile=showUser;
	}

	public twitter4j.User getUserProfile(){
		return userProfile;
	}
	
}
