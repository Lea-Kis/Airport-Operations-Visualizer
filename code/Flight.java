package flightsim.model;
import flightsim.exception.InvalidDataException;

public class Flight {
	private final Airport from;
	private final Airport to;
	private final int hour;
	private final int minute;
    private final int duration;
    
	public Flight(Airport from, Airport to, String departureTime, int duration) throws InvalidDataException {
		if(from == null || to == null) {
			throw new InvalidDataException("Missing airport. Please enter code of both existing airports");
		}
		if(from.equals(to)) {
			throw new InvalidDataException("Airports cannot be same");
		}
		if(duration <= 0) {
			throw new InvalidDataException("Please enter valid flight duration");
		}
		if(departureTime == null || !departureTime.trim().matches("\\d{1,2}:\\d{2}")) {
			throw new InvalidDataException("Departure time must be in format HH:MM");
		}
		
		String parts[] = departureTime.split(":");
		int hour, minute;
		
		try {
			hour = Integer.parseInt(parts[0]);
			minute = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InvalidDataException("Time contains non-numeric values.");
		}
		
		if (hour < 0 || hour > 23) {
            throw new InvalidDataException("Hours must be between 00 and 23.");
        }
        if (minute < 0 || minute > 59) {
            throw new InvalidDataException("Minutes must be between 00 and 59.");
        }
        
		this.from = from;
		this.to = to;
		this.hour = hour;
		this.minute = minute;
		this.duration = duration;
	}

	public Airport getFrom() {
		return from;
	}

	public Airport getTo() {
		return to;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getDuration() {
		return duration;
	}
	
	public String getDepartureTime(){
		return String.format("%02d:%02d", hour, minute);
	}
	
	@Override
	public String toString(){
		return from.getCode() + " -> " + to.getCode() + " at " + getDepartureTime() + ", duration: " + duration + " min";
	}
    
    
}