package proje.cinepick.entites;

import java.time.LocalDateTime;

public class Vehicle extends Assest{
    private String VIN;
    private String LicensePlate;
    private int Odometer;
    
	public Vehicle(int id, String name, LocalDateTime createdDate, String status, String vIN, String licensePlate,
			int odometer) {
		super(id, name, createdDate, status);
		VIN = vIN;
		LicensePlate = licensePlate;
		Odometer = odometer;
	}
	
	public String getVIN() {
		return VIN;
	}
	public void setVIN(String vIN) {
		VIN = vIN;
	}
	public String getLicensePlate() {
		return LicensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		LicensePlate = licensePlate;
	}
	public int getOdometer() {
		return Odometer;
	}
	public void setOdometer(int odometer) {
		Odometer = odometer;
	}
    
}
