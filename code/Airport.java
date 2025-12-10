package flightsim.model;
import flightsim.exception.InvalidDataException;

public class Airport {
	private final String code;
	private final String name;
	private final int x;
	private final int y;
	
	public Airport(String code, String name, int x, int y) throws InvalidDataException {
		if(code == null || !code.matches("[A-Z]{3}")) {
			throw new InvalidDataException("Airport code is incorrect");
		}
		if(name == null || name.trim().isEmpty()) {
			throw new InvalidDataException("Airport name is missing");
		}
		if(x<-90 || x>90 || y<-90 || y>90){
			throw new InvalidDataException("Incorrect coordinates.");
		}
		
		this.code = code;
		this.name = name;
		this.x = x;
		this.y = y;
		
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || getClass()!=obj.getClass()) return false;
		Airport other = (Airport) obj;
		return code.equals(other.code);
	}
	
	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public String toString() {
		return name + " - " + code + " (" + x + " , " + y + ")";
	}
	
	
}