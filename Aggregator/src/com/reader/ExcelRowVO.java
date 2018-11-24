package com.reader;

public class ExcelRowVO {

	private String clientRNC;
	private String calDate;
	private String hourId;
	private Double pmCapacityLimit;
	private Double pmSumCapacity;

	public String getClientRNC() {
		return clientRNC;
	}

	public void setClientRNC(String clientRNC) {
		this.clientRNC = clientRNC;
	}

	public String getCalDate() {
		return calDate;
	}

	public void setCalDate(String calDate) {
		this.calDate = calDate;
	}

	public String getHourId() {
		return hourId;
	}

	public void setHourId(String hourId) {
		this.hourId = hourId;
	}

	public Double getPmCapacityLimit() {
		return pmCapacityLimit;
	}

	public void setPmCapacityLimit(Double pmCapacityLimit) {
		this.pmCapacityLimit = pmCapacityLimit;
	}

	public Double getPmSumCapacity() {
		return pmSumCapacity;
	}

	public void setPmSumCapacity(Double pmSumCapacity) {
		this.pmSumCapacity = pmSumCapacity;
	}

}
