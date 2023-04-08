package com.smart.rct.exception;

public class RctException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String exceptionMsg;

	/**
	 * 
	 * @param exceptionMsg
	 * @return
	 */
	public RctException(String exceptionMsg) {
		super(exceptionMsg);
		this.exceptionMsg = exceptionMsg;

	}

	/**
	 * purpose : This method is used to get Exception Msg
	 * 
	 * @return
	 */

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	/**
	 * purpose : This method is used to set Exception Msg
	 * 
	 * @param exceptionMsg
	 * @return
	 */

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
}
