package com.quy.bizcom;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class SMCController extends RecursiveTreeObject<SMCController> {

	StringProperty controllerBarcode;

	public SMCController(String controllerBarcode) {
		this.controllerBarcode = new SimpleStringProperty(controllerBarcode);
	}

	public ObservableValue<String> getControllerBarcode() {
		return this.controllerBarcode;
	}

}
