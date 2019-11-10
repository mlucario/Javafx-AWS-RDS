package com.quy.database;

public class Configs {
	protected static String dbHost = "database-bizcom.cvrt884ix9ft.us-west-1.rds.amazonaws.com";
	protected static String dbPort = "3306";
	protected static String dbUsername = "admin";
	protected static String dbPassword = "bizcom1171";
	protected static String dbName = "bizcom";

	// Local database
	// TODO: Remove duplicated line codes
	protected static String dbhost = "localhost";
	protected static String dbport = "3306";
	protected static String dbuser = "root";
	protected static String dbpass = "1234567Aa@";
//	protected static String dbpass = "Bizcom@1171";
	protected static String dbname = "bizcom_smc";
//	protected static String dbname = "testing";

	// History Table
	protected static String TABLE_HISTORY = "history";
	protected static String COL_QA_HISTORY = "QA";
	protected static String COL_TIME_HISTORY = "Time";
	protected static String COL_STATION_HISTORY = "Station";
	protected static String COL_SERIAL_NUMBER_HISTORY = "Controller_Serial_Number";
	protected static String COL_NOTE_HISTORY = "Note";

	// Controler Table
	protected static String TABLE_CONTROLER = "controllers";
	protected static String COL_MODEL_CONTROLER = "Model";
	protected static String COL_SERIAL_NUMBER_CONTROLER = "Serial_Number";
	protected static String COL_CURRENT_STATION_CONTROLER = "Current_Station";
	protected static String COL_RECEIVING_TIME_CONTROLER = "Receiving_Time";
	protected static String COL_ASSEMBLY_TIME_CONTROLER = "Assembly_Time";
	protected static String COL_RE_ASSEMBLY_TIME_CONTROLER = "Re_Assembly_Time";
	protected static String COL_BURN_IN_START_TIME_CONTROLER = "Burn_In_Start";
	protected static String COL_BURN_IN_END_TIME_CONTROLER = "Burn_In_End";
	protected static String COL_PACKING_TIME_CONTROLER = "Packing_Time";
	protected static String COL_SHIPPING_TIME_CONTROLER = "Shipping_Time";
	protected static String COL_REPAIR_TIME_CONTROLER = "Repair_Time";
	protected static String COL_BURN_IN_RESULT_CONTROLER = "Burn_In_Result";
	protected static String COL_LOT_ID_CONTROLER = "Lot_ID";
	protected static String COL_IS_RECEIVIING_CONTROLER = "Is_Received";
	protected static String COL_IS_ASSEMBLY_DONE_CONTROLER = "Is_Assembly_Done";
	protected static String COL_IS_RE_ASSEMBLY_DONE_CONTROLER = "Is_Re_Assembly_Done";
	protected static String COL_IS_BURIN_IN_DONE_CONTROLER = "Is_Burn_In_Done";
	protected static String COL_IS_BURIN_IN_PROCESSING_CONTROLER = "Is_Burn_In_Processing";
	protected static String COL_IS_PACKING_DONE_CONTROLER = "Is_Packing_Done";
	protected static String COL_IS_SHIPPING_DONE_CONTROLER = "Is_Shipping_Done";
	protected static String COL_IS_REPAIR_DONE_CONTROLER = "Is_Repaired_Done";
	protected static String COL_IS_PASSED_CONTROLER = "Is_Passed";
	protected static String COL_SYMPTOM_FAIL_CONTROLER = "Symptoms_Fail";
	protected static String COL_REWORK_COUNT_CONTROLER = "Re_work_count";

	// User Table
	protected static String TABLE_USER = "users";
	protected static String COL_USERNAME_USER = "username";
	protected static String COL_HASHING_PASSWORD_USER = "hashing_password";
	protected static String COL_SALT_KEY_USER = "salt_key";
	protected static String COL_TYPE_USER = "user_type";
	protected static String COL_IS_ACTIVE_USER = "active";
	protected static String COL_CREATE_AT_USER = "created_at";

}
