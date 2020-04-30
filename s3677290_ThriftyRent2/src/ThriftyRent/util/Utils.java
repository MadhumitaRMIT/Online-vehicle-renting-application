package ThriftyRent.util;

import javafx.scene.control.TextField;
/*
 * class for holding static helper methods
 * that can be used in other classes
 *  */
public class Utils {
	/**
	 * method to check if TextField contain text or not
	 * 
	 * @param textField TextField to check it contains text or not
	 *
	 * @return true if it doesn't\t contain text and false if it contain
	 **/
	public static boolean isEmpty(TextField textField) {
		return textField == null || textField.getText() == null || "".trim().equals(textField.getText());
	}

	/**
	 * method to check if String contain text or empty
	 * 
	 * @param text String to check it contains text or not
	 *
	 * @return true if it doesn't\t contain text and false if it contain
	 **/
	public static boolean isEmpty(String text) {
		return text == null || "".equalsIgnoreCase(text.trim().replace("\\s", ""));
	}
//get new line character as it is different for each operating system
	public static String getNewLine() {
		return System.getProperty("line.separator");
	}

	public static double toDouble(String string) {
		try {
			return Double.parseDouble(string);
		} catch (Exception e) {
			//System.err.println(e.getMessage());
		}
		// TODO Auto-generated method stub
		return 0;
	}
}
