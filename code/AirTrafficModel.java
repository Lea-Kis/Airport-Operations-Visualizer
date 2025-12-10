package flightsim.model;

import java.util.ArrayList;
import java.util.List;
import flightsim.exception.*;

public class AirTrafficModel {
	private final List<Airport> airports = new ArrayList<>();
	private final List<Flight> flights = new ArrayList<>();
	
	public void addAirport(Airport airport) throws DuplicateAirportCodeException {
		if(airport == null) {
			throw new IllegalArgumentException("Airport cannot be null");
			// Dodano za svaki slucaj;
		}
		
		String code = airport.getCode();
		
		if(findAirportByCode(code) != null) {
			throw new DuplicateAirportCodeException(code);
		}
		airports.add(airport);
	}
	
	public void addFlight(Flight flight) throws InvalidDataException {
		if(flight == null) {
			throw new IllegalArgumentException("Flight cannot be null");
			//Takode samo za sigurnost;
		}
		Airport from = flight.getFrom();
		Airport to = flight.getTo();
		
		if(findAirportByCode(from.getCode()) == null) {
			throw new InvalidDataException("Departure airport " + from.getCode() + " does not exist");
		}
		
		if(findAirportByCode(to.getCode()) == null) {
			throw new InvalidDataException("Arrival airport " + from.getCode() + " does not exist");
		}
		
		flights.add(flight);
	}
	
	public Airport findAirportByCode(String code) {
		for(Airport a : airports)
		{
			if(a.getCode().equals(code)) return a;
		}
		return null;
	}
	
	public void clearAirports() {
		airports.clear();
	}
	
	public void clearFlights() {
		flights.clear();
	}

	public List<Airport> getAirports() {
		return new ArrayList<>(airports);
	}

	public List<Flight> getFlights() {
		return new ArrayList<>(flights);
	}
	
	
}