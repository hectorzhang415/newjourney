package com.reader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class CalResult {

	public CalResult() {}
	
	private Map<String, Map<String, DateMaxData>> clientRNCData;

	public Map<String, Map<String, DateMaxData>> getClientRNCData() {
		return clientRNCData;
	}

	public void setClientRNCData(Map<String, Map<String, DateMaxData>> clientRNCData) {
		this.clientRNCData = clientRNCData;
	}

}

class DateMaxData {
	private Double pmCapacityLimit;
	private Double pmSumCapacity;


	public Double getPmCapacityLimit() {
		return pmCapacityLimit;
	}

	public void setPmCapacityLimit(Double pmCapacityLimit) {
		this.pmCapacityLimit = pmCapacityLimit;
	}

	public Double getPmSumCapacity() {
		BigDecimal b = new BigDecimal(pmSumCapacity);
		double value = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
		return value;
	}

	public void setPmSumCapacity(Double pmSumCapacity) {
		this.pmSumCapacity = pmSumCapacity;
	}

	public String getUtilizeRate() {
		if (pmCapacityLimit.doubleValue() > 0) {
			Double value = 100 * pmSumCapacity / pmCapacityLimit;
			BigDecimal b = new BigDecimal(value);
			String val = String.valueOf(b.setScale(2, RoundingMode.HALF_UP).doubleValue());
			return val + "%";
		} else {
			return "N/A";
		}
	}

}
