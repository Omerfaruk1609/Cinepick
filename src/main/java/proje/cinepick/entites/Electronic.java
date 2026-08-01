package proje.cinepick.entites;

import java.time.LocalDateTime;

public class Electronic extends Assest{
	private String serialNumber;
	private String FirmWareVersion;
	private String Model;
	
	public Electronic(int id, String name, LocalDateTime createdDate, String status, String serialNumber,
			String firmWareVersion, String model) {
		super(id, name, createdDate, status);
		this.serialNumber = serialNumber;
		FirmWareVersion = firmWareVersion;
		Model = model;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getFirmWareVersion() {
		return FirmWareVersion;
	}
	public void setFirmWareVersion(String firmWareVersion) {
		FirmWareVersion = firmWareVersion;
	}
	public String getModel() {
		return Model;
	}
	public void setModel(String model) {
		Model = model;
	}

}
