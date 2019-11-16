package com.quy.bizcom;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class SMCController extends RecursiveTreeObject<SMCController> {

	private StringProperty serialNumber;
	private StringProperty model;
	private StringProperty lotId;
	private StringProperty currentStation;
	private StringProperty receivingTime;
	private StringProperty assemblyTime;
	private StringProperty burnInStart;
	private StringProperty burnInEnd;
	private StringProperty packingTime;
	private StringProperty shippingTime;
	private StringProperty burnInResult;
	private StringProperty firmwareUpdateTime;
	private StringProperty repairTime;
	private BooleanProperty isReceived;
	private BooleanProperty isAssembly;
	private BooleanProperty isFirmwareUpdated;
	private BooleanProperty isBurnInProcessing;
	private BooleanProperty isBurnInDone;
	private BooleanProperty isPacked;
	private BooleanProperty isShipped;
	private BooleanProperty isRepaired;
	private BooleanProperty isRework;
	private BooleanProperty isPassed;
	private StringProperty symptomsFails;
	private IntegerProperty reWorkCount;

	public SMCController(StringProperty serialNumber, StringProperty model, StringProperty lotId,
			StringProperty currentStation, StringProperty receivingTime, StringProperty assemblyTime,
			StringProperty burnInStart, StringProperty burnInEnd, StringProperty packingTime,
			StringProperty shippingTime, StringProperty burnInResult, StringProperty firmwareUpdateTime,
			StringProperty repairTime, BooleanProperty isReceived, BooleanProperty isAssembly,
			BooleanProperty isFirmwareUpdated, BooleanProperty isBurnInProcessing, BooleanProperty isBurnInDone,
			BooleanProperty isPacked, BooleanProperty isShipped, BooleanProperty isRepaired, BooleanProperty isRework,
			BooleanProperty isPassed, StringProperty symptomsFails, IntegerProperty reWorkCount) {
		super();
		this.serialNumber = serialNumber;
		this.model = model;
		this.lotId = lotId;
		this.currentStation = currentStation;
		this.receivingTime = receivingTime;
		this.assemblyTime = assemblyTime;
		this.burnInStart = burnInStart;
		this.burnInEnd = burnInEnd;
		this.packingTime = packingTime;
		this.shippingTime = shippingTime;
		this.burnInResult = burnInResult;
		this.firmwareUpdateTime = firmwareUpdateTime;
		this.repairTime = repairTime;
		this.isReceived = isReceived;
		this.isAssembly = isAssembly;
		this.isFirmwareUpdated = isFirmwareUpdated;
		this.isBurnInProcessing = isBurnInProcessing;
		this.isBurnInDone = isBurnInDone;
		this.isPacked = isPacked;
		this.isShipped = isShipped;
		this.isRepaired = isRepaired;
		this.isRework = isRework;
		this.isPassed = isPassed;
		this.symptomsFails = symptomsFails;
		this.reWorkCount = reWorkCount;
	}

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
		this.isPassed.setValue(false);
	}

	public void setPassed() {
		this.isPassed.setValue(true);
	}

	public StringProperty getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(StringProperty serialNumber) {
		this.serialNumber = serialNumber;
	}

	public StringProperty getModel() {
		return model;
	}

	public void setModel(StringProperty model) {
		this.model = model;
	}

	public StringProperty getLotId() {
		return lotId;
	}

	public void setLotId(StringProperty lotId) {
		this.lotId = lotId;
	}

	public StringProperty getCurrentStation() {
		return currentStation;
	}

	public void setCurrentStation(StringProperty currentStation) {
		this.currentStation = currentStation;
	}

	public StringProperty getReceivingTime() {
		return receivingTime;
	}

	public void setReceivingTime(StringProperty receivingTime) {
		this.receivingTime = receivingTime;
	}

	public StringProperty getAssemblyTime() {
		return assemblyTime;
	}

	public void setAssemblyTime(StringProperty assemblyTime) {
		this.assemblyTime = assemblyTime;
	}

	public StringProperty getBurnInStart() {
		return burnInStart;
	}

	public void setBurnInStart(StringProperty burnInStart) {
		this.burnInStart = burnInStart;
	}

	public StringProperty getBurnInEnd() {
		return burnInEnd;
	}

	public void setBurnInEnd(StringProperty burnInEnd) {
		this.burnInEnd = burnInEnd;
	}

	public StringProperty getPackingTime() {
		return packingTime;
	}

	public void setPackingTime(StringProperty packingTime) {
		this.packingTime = packingTime;
	}

	public StringProperty getShippingTime() {
		return shippingTime;
	}

	public void setShippingTime(StringProperty shippingTime) {
		this.shippingTime = shippingTime;
	}

	public StringProperty getBurnInResult() {
		return burnInResult;
	}

	public void setBurnInResult(StringProperty burnInResult) {
		this.burnInResult = burnInResult;
	}

	public StringProperty getFirmwareUpdateTime() {
		return firmwareUpdateTime;
	}

	public void setFirmwareUpdateTime(StringProperty firmwareUpdateTime) {
		this.firmwareUpdateTime = firmwareUpdateTime;
	}

	public StringProperty getRepairTime() {
		return repairTime;
	}

	public void setRepairTime(StringProperty repairTime) {
		this.repairTime = repairTime;
	}

	public BooleanProperty getIsReceived() {
		return isReceived;
	}

	public void setIsReceived(BooleanProperty isReceived) {
		this.isReceived = isReceived;
	}

	public BooleanProperty getIsAssembly() {
		return isAssembly;
	}

	public void setIsAssembly(BooleanProperty isAssembly) {
		this.isAssembly = isAssembly;
	}

	public BooleanProperty getIsFirmwareUpdated() {
		return isFirmwareUpdated;
	}

	public void setIsFirmwareUpdated(BooleanProperty isFirmwareUpdated) {
		this.isFirmwareUpdated = isFirmwareUpdated;
	}

	public BooleanProperty getIsBurnInProcessing() {
		return isBurnInProcessing;
	}

	public void setIsBurnInProcessing(BooleanProperty isBurnInProcessing) {
		this.isBurnInProcessing = isBurnInProcessing;
	}

	public BooleanProperty getIsBurnInDone() {
		return isBurnInDone;
	}

	public void setIsBurnInDone(BooleanProperty isBurnInDone) {
		this.isBurnInDone = isBurnInDone;
	}

	public BooleanProperty getIsPacked() {
		return isPacked;
	}

	public void setIsPacked(BooleanProperty isPacked) {
		this.isPacked = isPacked;
	}

	public BooleanProperty getIsShipped() {
		return isShipped;
	}

	public void setIsShipped(BooleanProperty isShipped) {
		this.isShipped = isShipped;
	}

	public BooleanProperty getIsRepaired() {
		return isRepaired;
	}

	public void setIsRepaired(BooleanProperty isRepaired) {
		this.isRepaired = isRepaired;
	}

	public BooleanProperty getIsRework() {
		return isRework;
	}

	public void setIsRework(BooleanProperty isRework) {
		this.isRework = isRework;
	}

	public StringProperty getSymptomsFails() {
		return symptomsFails;
	}

	public void setSymptomsFails(StringProperty symptomsFails) {
		this.symptomsFails = symptomsFails;
	}

	public IntegerProperty getReWorkCount() {
		return reWorkCount;
	}

	public void setReWorkCount(IntegerProperty reWorkCount) {
		this.reWorkCount = reWorkCount;
	}

	public boolean getResult() {
		return this.isPassed.getValue().booleanValue();
	}

}
