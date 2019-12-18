
package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ResultStationController extends Controller implements Initializable {

	@FXML
	private JFXRadioButton rdPassed;

	@FXML
	private ToggleGroup result;

	@FXML
	private JFXRadioButton rdFail;

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private VBox vboxSymptoms;

	@FXML
	private JFXTextArea txtSymptoms;

	@FXML
	private VBox vboxFail;

	@FXML
	private JFXTreeTableView<SMCController> treeviewFail;

	@FXML
	private Text txtFail;

	@FXML
	private VBox vboxPassed;

	@FXML
	private JFXTreeTableView<SMCController> treeviewPassed;

	@FXML
	private Text txtPass;
	@FXML
	private JFXRadioButton rdBurnInFail;
	@FXML
	private JFXRadioButton rdAssemblyFail;
	@FXML
	private ToggleGroup failResult;
	@FXML
	private JFXRadioButton rdFirmwareUpdateFail;

	@FXML
	private Label txtRemain;

	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcodePassed = FXCollections.observableArrayList();
	private ObservableList<SMCController> barcodeFail = FXCollections.observableArrayList();
	private ObservableList<SMCController> burnInDoneList = FXCollections.observableArrayList();
	private boolean resultChoosen;
	private String stationFail;
	private int currentPass;
	private int currentFail;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dbHandler = new DBHandler();
		textFieldFormat(txtControllerBarcode, "Controller Barcode is required", true);
		currentPass = currentFail = 0;
		rdPassed.setToggleGroup(result);
		rdFail.setToggleGroup(result);
		rdPassed.setUserData("PASSED");
		rdFail.setUserData("FAIL");
		stationFail = "Burn In Fail";
		rdBurnInFail.setUserData(BURN_IN_STATION);
		rdAssemblyFail.setUserData(ASSEMBLY_STATION);
		rdFirmwareUpdateFail.setUserData(FIRMWARE_UPDATE_STATION);

		result.selectToggle(rdPassed);
		treeviewTableBuilder(treeviewPassed, barcodePassed);
		result.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (result.getSelectedToggle() != null) {
					resultChoosen = result.getSelectedToggle().getUserData().toString().equalsIgnoreCase("PASSED");
					if (!resultChoosen) {
						// show fail objects
						vboxSymptoms.setVisible(true);
						vboxFail.setVisible(true);
						vboxPassed.setVisible(false);
						treeviewTableBuilder(treeviewFail, barcodeFail);

					} else {
						vboxSymptoms.setVisible(false);
						vboxFail.setVisible(false);
						vboxPassed.setVisible(true);
						treeviewTableBuilder(treeviewPassed, barcodePassed);
					}
				} else {
					txtControllerBarcode.setDisable(true);
					warningAlert("PLEASE SELECT PASSED or FAIL options");
				}

			}
		});

		failResult.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (failResult.getSelectedToggle() != null) {
					if (!oldValue.equals(newValue)) {
						stationFail = newValue.getUserData().toString();
					}
				}

			}
		});

		burnInDoneList.addAll(dbHandler.getAllBurning());

		txtRemain.setVisible(false);

		Platform.runLater(() -> txtControllerBarcode.requestFocus());
	}

	@FXML
	public String isValidInput() {
		String resultInput = "";

		resultInput = isBarcodeValid(txtControllerBarcode);

		if (resultInput.isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStatus = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			if (!dbHandler.isBarcodeExist(serialNumber)) {
				resultInput = "\r\n Serial Number does not exist!";
			} else {
				if (currentStatus.equalsIgnoreCase(SHIPPING_STATION)) {
					resultInput = "\r\n Controller has been shipped. Please ask manager intermediately.";
				} else {
					switch (currentStatus) {

					case FIRMWARE_UPDATE_STATION:
					case BURN_IN_STATION:
					case RESULT_STATION:
					case RE_WORK_STATION:
						resultInput = "";
						break;
//					case RESULT_STATION:
//						resultInput = "Controller was set result.";
//						break;
					case RECEIVING_STATION:
					case ASSEMBLY_STATION:
						resultInput = "Controller doesn't burn in. Please burn-in before set result.";
						break;
					case WAIT_TO_BURN_IN:
					case PACKING_STATION:
					case REPAIR_STATION:
						resultInput = "\r\n Cannot set result this controller. Please check with manager.";
						break;

					default:

						LOGGER.info("There is nothing here.");
					}
				}
			}
		}
		return resultInput;
	}

	@FXML
	void resultAction(ActionEvent event) {

		if (isValidInput().isEmpty()) {
			String timeStamp = getCurrentTimeStamp();
			resultChoosen = result.getSelectedToggle().getUserData().toString().equalsIgnoreCase("PASSED");
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);
			String getResult = dbHandler.getStatusDone(COL_BURN_IN_RESULT_CONTROLER, serialNumber);
			// RESULT PASSED
			if (resultChoosen) {
				if (getResult.equalsIgnoreCase("PASS") && dbHandler
						.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber).equalsIgnoreCase(RESULT_STATION)) {
					warningAlert("Result was set PASS!");

				} else {
					currentPass++;
					String resultAc = dbHandler.setResultPass(serialNumber, timeStamp, true, "No Trouble Found.");
					if (resultAc.equals(serialNumber)) {
						addBarcodeToTable(barcodePassed, serialNumber, model, currentPass);

						txtPass.textProperty().bind(Bindings.format("PASSED : %d", barcodePassed.size()));
						String history = dbHandler.addToHistoryRecord(currentUser, RESULT_STATION, timeStamp,
								serialNumber, "Marked Passed!",false);
						if (!history.equalsIgnoreCase(serialNumber)) {
							warningAlert(history);
						} else {
							notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
									"Set Result PASSED Successfully", 2);
							notification.showInformation();

						}
					} else {
						warningAlert(resultAc);
					}

					if (!barcodeFail.isEmpty()) {
						for (SMCController sss : barcodeFail) {
							if (sss.getControllerBarcode().getValue().equalsIgnoreCase(serialNumber)) {
								barcodeFail.remove(sss);
								break;
							}
						}
					}
				}
			}
			// RESULT FAIL
			else {

				if (getResult.equalsIgnoreCase("FAIL") && dbHandler
						.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber).equalsIgnoreCase(RESULT_STATION)) {
					warningAlert("Result was set FAIL!");
				} else {

					if (!txtSymptoms.getText().isEmpty()) {
						String tempText = "Fail at " + stationFail + " (" + txtSymptoms.getText() + ")";
						currentFail++;
						String resultQuery = "";
						String currentStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
						switch (currentStation) {
						case ASSEMBLY_STATION:
						case FIRMWARE_UPDATE_STATION:
							resultQuery = dbHandler.setResultFail(serialNumber, null, false, false, tempText);
							break;
						case BURN_IN_STATION:
							resultQuery = dbHandler.setResultFail(serialNumber, timeStamp, false, true, tempText);
							break;
						case RESULT_STATION:
							resultQuery = dbHandler.setResult(serialNumber, timeStamp, false, tempText);

							if (!barcodePassed.isEmpty()) {

								for (SMCController sss : barcodePassed) {
									if (sss.getControllerBarcode().getValue().equalsIgnoreCase(serialNumber)) {
										barcodePassed.remove(sss);
										break;
									}
								}
							}
							break;
						default:
							resultQuery = "FAIL TO UPDATE RESULT!";
						}

						if (resultQuery.equals(serialNumber)) {

							addBarcodeToTable(barcodeFail, serialNumber, model, currentFail);

							txtFail.textProperty().bind(Bindings.format("FAIL : %d", barcodeFail.size()));
							String history = dbHandler.addToHistoryRecord(currentUser, RESULT_STATION, timeStamp,
									serialNumber, "Marked Fail: " + tempText,false);
							if (!history.equalsIgnoreCase(serialNumber)) {
								warningAlert(history);
							} else {
								notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,

										"Set Result FAIL Successfully", 2);
								notification.showInformation();

							}
						} else {
							warningAlert(resultQuery);
						}
					} else {
						warningAlert("PLEASE ENTER SYMPTOMS");
						txtSymptoms.clear();
						txtSymptoms.requestFocus();
					}
				}
			}

		} else {
			warningAlert(isValidInput());
		}

//		txtRemain.textProperty().bind(Bindings.format("%d", currentRemainBurnIn));
		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
	}

}
