package com.quy.controllers;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

public class Controller {
	protected final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	protected final Date date = new Date();
	protected final java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	protected final java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());
	
	public void textFieldFormat(JFXTextField  txt, String warning) {
		txt.setStyle("-fx-text-inner-color: #8e44ad;");
		
		// This code will change all text to Upper Case
//		txt.setTextFormatter(new TextFormatter<>((change) -> {
//		    change.setText(change.getText().toUpperCase());
//		    return change;
//		}));
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage(warning);
		txt.getValidators().add(validator);
		txt.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal)
				txt.validate();
		});
	}
	
	public void textFieldFormat(JFXPasswordField txt,  String warning) {
		txt.setStyle("-fx-text-inner-color: #8e44ad;");
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage(warning);
		txt.getValidators().add(validator);
		txt.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal)
				txt.validate();
		});
	}
}
