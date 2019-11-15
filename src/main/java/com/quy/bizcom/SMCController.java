package com.quy.bizcom;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class SMCController extends RecursiveTreeObject<SMCController> {

	private StringProperty serialNumber;
	private StringProperty model;

	private boolean isPassed;

	public SMCController(String controllerBarcode) {
		this.serialNumber = new SimpleStringProperty(controllerBarcode);
	}

	public SMCController(StringProperty serialNumber, StringProperty model) {
		super();
		this.serialNumber = serialNumber;
		this.serialNumber = model;
	}

	public ObservableValue<String> getControllerBarcode() {
		return this.serialNumber;
	}

	public void setFail() {
		this.isPassed = false;
	}

	public void setPassed() {
		this.isPassed = true;
	}

	public boolean getResult() {
		return this.isPassed;
	}

}
