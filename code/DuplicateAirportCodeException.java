package flightsim.exception;

public class DuplicateAirportCodeException extends Exception {
	private final String code;
	
	public DuplicateAirportCodeException(String code) {
		super("Airport with code: " + code + " already exists.");
		this.code = code;
	}
	public String getCode() {
		return code;
	}
}
