package com.quy.bizcom;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User extends RecursiveTreeObject<User> {
	private StringProperty username;
	private StringProperty hashingPassword;
	private StringProperty saltKey;
	private StringProperty userType;
	private BooleanProperty active;
	private StringProperty createdAt;
	private int studentId;
	private static int studentIdAct = 1;
	public User(StringProperty username, StringProperty hashing_password, StringProperty salt_key,
			StringProperty userType, BooleanProperty active, StringProperty createdAt) {
		studentId = studentIdAct++;
		this.username = username;
		this.hashingPassword = hashing_password;
		this.saltKey = salt_key;
		this.userType = userType;
		this.active = active;
		this.createdAt = createdAt;
	}

	public User(String username, String userType, boolean active, String createdAt) {
		studentId = studentIdAct++;
		this.username = new SimpleStringProperty(username);
		this.userType = new SimpleStringProperty(userType);
		this.active = new SimpleBooleanProperty(active);
		this.createdAt = new SimpleStringProperty(createdAt);
	}

	public StringProperty getUsername() {
		return username;
	}

	public StringProperty getHashingPassword() {
		return hashingPassword;
	}

	public StringProperty getSaltKey() {
		return saltKey;
	}

	public StringProperty getUserType() {
		return userType;
	}

	public BooleanProperty getActive() {
		return active;
	}

	public StringProperty getCreatedAt() {
		return createdAt;
	}
	
	public String getStudentID() {
		return String.valueOf(this.studentId);
	}

}
