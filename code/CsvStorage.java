package flightsim.storage;

import java.io.*;
import java.util.List;
import flightsim.model.Airport;
import flightsim.model.Flight;
import flightsim.model.AirTrafficModel;
import flightsim.exception.InvalidDataException;

public class CsvStorage {

	public static void saveAirports(String filename, List<Airport> airports) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Airport a : airports) {
                writer.write(String.format("%s,%s,%d,%d\n", 
                		a.getCode(), a.getName(), a.getX(), a.getY()));
            }
        }
    }
	
	public static void saveFlights(String filename, List<Flight> flights) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Flight f : flights) {
                writer.write(String.format("%s,%s,%s,%d\n",
                    f.getFrom().getCode(),
                    f.getTo().getCode(),
                    f.getDepartureTime(),
                    f.getDuration()));
            }
        }
    }
	
	public static void loadAirports(String filename, AirTrafficModel model) throws IOException, InvalidDataException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line; 
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new InvalidDataException("Line " + lineNumber + ": expected 4 columns (Code,Name,X,Y).");
                }
                try {
                    String code = parts[0].trim();
                    String name = parts[1].trim();
                    int x = Integer.parseInt(parts[2].trim());
                    int y = Integer.parseInt(parts[3].trim());
                    Airport a = new Airport(code, name, x, y);
                    model.addAirport(a);
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("Line " + lineNumber + ": invalid number format for coordinates.");
                } catch (InvalidDataException | flightsim.exception.DuplicateAirportCodeException e) {
                    throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
    }
	
	public static void loadFlights(String filename, AirTrafficModel model) throws IOException, InvalidDataException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new InvalidDataException("Line " + lineNumber + ": expected 4 columns (FromCode,ToCode,DepartureTime,DurationMin).");
                }
                String fromCode = parts[0].trim();
                String toCode = parts[1].trim();
                String time = parts[2].trim();
                String durationStr = parts[3].trim();

                Airport from = model.findAirportByCode(fromCode);
                Airport to = model.findAirportByCode(toCode);
                if (from == null) {
                    throw new InvalidDataException("Line " + lineNumber + ": departure airport '" + fromCode + "' not found.");
                }
                if (to == null) {
                    throw new InvalidDataException("Line " + lineNumber + ": arrival airport '" + toCode + "' not found.");
                }

                try {
                    int duration = Integer.parseInt(durationStr);
                    Flight f = new Flight(from, to, time, duration);
                    model.addFlight(f);
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("Line " + lineNumber + ": invalid duration (must be integer).");
                } catch (InvalidDataException e) {
                    throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
    }
}