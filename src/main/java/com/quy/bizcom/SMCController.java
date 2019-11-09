package com.quy.bizcom;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class SMCController extends RecursiveTreeObject<SMCController> {

	private StringProperty controllerBarcode;
	private StringProperty controllerModel;
	private boolean isPassed;

	public SMCController(String controllerBarcode) {
		this.controllerBarcode = new SimpleStringProperty(controllerBarcode);
	}

	public SMCController(StringProperty controllerBarcode, StringProperty controllerModel) {
		super();
		this.controllerBarcode = controllerBarcode;
		this.controllerModel = controllerModel;
	}

	public ObservableValue<String> getControllerBarcode() {
		return this.controllerBarcode;
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
