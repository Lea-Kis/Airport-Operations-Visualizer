package flightsim.gui;

import java.awt.*;
import java.awt.event.*;
import flightsim.model.*;
import flightsim.exception.*;
import flightsim.storage.CsvStorage;
import java.io.IOException;
import flightsim.util.*;

import java.util.ArrayList;
import java.util.List;


public class MainFrame extends Frame implements ActivityObserver, ActionListener {
	private final AirTrafficModel model = new AirTrafficModel();
	private ActivityMonitor activityMonitor;
	
    private TextArea airportArea = new TextArea(15, 40);
    private TextArea flightArea = new TextArea(15, 40);
    
    private Button addAirportBtn = new Button("Add Airport");
    private Button addFlightBtn = new Button("Add Flight");
    private Button saveAirportsBtn = new Button("Save Airports");
    private Button saveFlightsBtn = new Button("Save Flights");
    private Button loadAirportsBtn = new Button("Load Airports");
    private Button loadFlightsBtn = new Button("Load Flights");
    
    private Thread currentTimerThread;
    private InactivityTimer currentTimer;
    
    private AirportMapCanvas mapCanvas;
    private Panel filterPanel;
    private List<Checkbox> airportCheckboxes = new ArrayList<>();
    
    private boolean timerPaused = false;
    
    public MainFrame() {
    	setTitle("Flight Simulator");
    	setSize(800, 500);
    	setLayout(new BorderLayout());
    	
    	activityMonitor = new ActivityMonitor();
        activityMonitor.addObserver(this);
    	
        buildDisplayArea();
        buildButtonPanel();
        buildMap();
        buildFilterPanel();
       
        addAirportBtn.addActionListener(this);
        addFlightBtn.addActionListener(this);
        saveAirportsBtn.addActionListener(this);
        saveFlightsBtn.addActionListener(this);
        loadAirportsBtn.addActionListener(this);
        loadFlightsBtn.addActionListener(this);
        
        activityMonitor.addActivityListeners(this);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        updateDisplay();
        setVisible(true);
        startInactivityTimer();
    }
    
    
    @Override
    public void onUserActivity() {
    	startInactivityTimer();
    }
    
    
    private void buildDisplayArea() {
        airportArea.setEditable(false);
        flightArea.setEditable(false);

        Panel airportPanel = new Panel(new BorderLayout());
        airportPanel.add(new Label("Airports:", Label.CENTER), BorderLayout.NORTH);
        airportPanel.add(airportArea, BorderLayout.CENTER);

        Panel flightPanel = new Panel(new BorderLayout());
        flightPanel.add(new Label("Flights:", Label.CENTER), BorderLayout.NORTH);
        flightPanel.add(flightArea, BorderLayout.CENTER);

        Panel combinedPanel = new Panel(new GridLayout(2, 1, 5, 5));
        combinedPanel.setPreferredSize(new Dimension(300, getHeight()));
        combinedPanel.add(airportPanel);
        combinedPanel.add(flightPanel);

        add(combinedPanel, BorderLayout.WEST);
    }
    
    
    private void buildButtonPanel() {
    	Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(addAirportBtn);
        buttonPanel.add(addFlightBtn);
        buttonPanel.add(saveAirportsBtn);
        buttonPanel.add(saveFlightsBtn);
        buttonPanel.add(loadAirportsBtn);
        buttonPanel.add(loadFlightsBtn);
        add(buttonPanel, BorderLayout.NORTH);
    }
    
    
    private void buildMap() {
    	mapCanvas = new AirportMapCanvas();
        add(mapCanvas, BorderLayout.CENTER);
    }
    
    private void buildFilterPanel() {
        filterPanel = new Panel(new GridLayout(0, 1));
        Panel wrapper = new Panel(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(250, getHeight()));
        wrapper.add(new Label("Airports Visible:", Label.CENTER), BorderLayout.NORTH);
        wrapper.add(filterPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.EAST);
    }
    
    
    private void updateDisplay() {
        StringBuilder airports = new StringBuilder();
        for (Airport a : model.getAirports()) {
            airports.append(a.toString()).append("\n");
        }
        airportArea.setText(airports.toString());
        
        StringBuilder flights = new StringBuilder();
        for (Flight f : model.getFlights()) {
            flights.append(f.toString()).append("\n");
        }
        flightArea.setText(flights.toString());
        mapCanvas.setAirports(model.getAirports());
        
        filterPanel.removeAll();
        airportCheckboxes.clear();
        
        List<Airport> Checkairports = model.getAirports();
        for (int i = 0; i < Checkairports.size(); i++) {
            Airport ap = Checkairports.get(i);
            Checkbox cb = new Checkbox(ap.getCode() + " – " + ap.getName() + " (" + ap.getX() + " , " + ap.getY() + ")", true);
            final int index = i;
            cb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    mapCanvas.setVisibility(index, cb.getState());
                }
            });
            filterPanel.add(cb);
            airportCheckboxes.add(cb);
        }
        filterPanel.validate();
        filterPanel.repaint();
    }
    
    
    private void showError(String message) {
        Dialog error = new Dialog(this, "Error", true);
        error.setLayout(new BorderLayout());
        error.add(new Label(message, Label.CENTER), BorderLayout.CENTER);
        Button ok = new Button("OK");
        ok.addActionListener(e -> error.dispose());
        error.add(ok, BorderLayout.SOUTH);
        error.setSize(400, 120);
        error.setLocationRelativeTo(this);
        error.setVisible(true);
    }
    
    
    public void askUserToContinue() {
        Dialog dialog = new Dialog(this, "Inactivity Warning", true);
        Label countdownLabel = new Label("Closing in 5 seconds. Do you want to continue?", Label.CENTER);
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        
        dialog.setLayout(new BorderLayout());
        dialog.add(countdownLabel, BorderLayout.CENTER);
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(yesBtn);
        buttonPanel.add(noBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(300, 120);
        dialog.setLocationRelativeTo(this);

        Thread countdownThread = new Thread(() -> {
            for (int i = 4; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                    final int sec = i;
                    EventQueue.invokeLater(() -> {
                        if (dialog.isVisible()) {
                            countdownLabel.setText("Closing in " + sec + " seconds. Do you want to continue?");
                        }
                    });
                } catch (InterruptedException e) {
                    return;
                }
            }
            
            EventQueue.invokeLater(() -> {
                if (dialog.isVisible()) {
                    dialog.dispose();
                    System.exit(0);
                }
            });
        });
        countdownThread.start();
        
        yesBtn.addActionListener(e -> {
        	countdownThread.interrupt();
            dialog.dispose();
            startInactivityTimer();
        });
        noBtn.addActionListener(e -> {
        	countdownThread.interrupt();
            dialog.dispose();
            System.exit(0);
        });

        dialog.setVisible(true);
    }
    
    
    public boolean isAirportSelected() {
        return mapCanvas.getSelectedAirport() != null;
    }
    
    
    public void startInactivityTimer() {
    	if(timerPaused) return;
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        if (currentTimerThread != null && currentTimerThread.isAlive()) {
            currentTimerThread.interrupt();
        }

        currentTimer = new InactivityTimer(this);
        currentTimerThread = new Thread(currentTimer);
        currentTimerThread.setDaemon(true);
        currentTimerThread.start();
    }
    
    
    public void pauseInactivityTimer() {
        timerPaused = true;
        if (currentTimer != null) currentTimer.cancel();
        if (currentTimerThread != null) currentTimerThread.interrupt();
    }
    
    
    public void resumeInactivityTimer() {
        timerPaused = false;
        startInactivityTimer();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	startInactivityTimer();
        try {
            if (e.getSource() == addAirportBtn) {
                AddAirportDialog dialog = new AddAirportDialog(this);
                activityMonitor.registerDialog(dialog);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                	try {
                		int x = Integer.parseInt(dialog.getXText());
                        int y = Integer.parseInt(dialog.getYText());
	                    Airport a = new Airport(dialog.getCode(), dialog.getName(), x, y);
	                    model.addAirport(a);
	                    updateDisplay();
	                } catch (NumberFormatException ex) {
	                	showError("Coordinates must be numbers.");
	                } catch (InvalidDataException | DuplicateAirportCodeException ex) {
	                	showError(ex.getMessage());
	                }
                }
            } 
            else if (e.getSource() == addFlightBtn) {
                AddFlightDialog dialog = new AddFlightDialog(this);
                activityMonitor.registerDialog(dialog);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                	try {
                        String fromCode = dialog.getFromCode();
                        String toCode = dialog.getToCode();
                        Airport from = model.findAirportByCode(fromCode);
                        Airport to = model.findAirportByCode(toCode);
                        int duration = Integer.parseInt(dialog.getDurationText());
                        Flight f = new Flight(from, to, dialog.getTime(), duration);
                        model.addFlight(f);
                        updateDisplay();
                    } catch (NumberFormatException ex) {
                        showError("Duration must be a whole number.");
                    } catch (InvalidDataException ex) {
                        showError(ex.getMessage());
                    }
                }
            }
            else if (e.getSource() == saveAirportsBtn) {
                FileDialog dialog = new FileDialog(this, "Save Airports");
                activityMonitor.registerDialog(dialog);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    String fileName = dialog.getFileName();
                    if (fileName.isEmpty()) {
                        showError("Please enter file name.");
                        return;
                    }
                    try {
                        CsvStorage.saveAirports(fileName, model.getAirports());
                        showError("Airports saved to: " + fileName);
                    } catch (IOException ex) {
                        showError("Save error: " + ex.getMessage());
                    }
                }
            }
            else if (e.getSource() == saveFlightsBtn) {
                FileDialog dialog = new FileDialog(this, "Save Flights");
                activityMonitor.registerDialog(dialog);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    String fileName = dialog.getFileName();
                    if (fileName.isEmpty()) {
                        showError("Please enter file name.");
                        return;
                    }
                    try {
                        CsvStorage.saveFlights(fileName, model.getFlights());
                        showError("Flights saved to: " + fileName);
                    } catch (IOException ex) {
                        showError("Save error: " + ex.getMessage());
                    }
                }
            }
            else if (e.getSource() == loadAirportsBtn) {
                FileDialog dialog = new FileDialog(this, "Load Airports");
                activityMonitor.registerDialog(dialog);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    String fileName = dialog.getFileName();
                    if (fileName.isEmpty()) {
                        showError("Please enter a file name");
                        return;
                    }
                    try {
                        model.clearAirports();
                        CsvStorage.loadAirports(fileName, model);
                        updateDisplay();
                        showError("Airports loaded successfully");
                    } catch (IOException ex) {
                        showError("File not found: " + ex.getMessage());
                    } catch (InvalidDataException ex) {
                        showError("CSV error: " + ex.getMessage());
                    }
                }
            }
            else if (e.getSource() == loadFlightsBtn) {
                FileDialog dialog = new FileDialog(this, "Load Flights");
                activityMonitor.registerDialog(dialog);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    String fileName = dialog.getFileName();
                    if (fileName.isEmpty()) {
                        showError("Please enter a file name");
                        return;
                    }
                    try {
                        model.clearFlights();
                        CsvStorage.loadFlights(fileName, model);
                        updateDisplay();
                        showError("Flights loaded successfully");
                    } catch (IOException ex) {
                        showError("File not found: " + ex.getMessage());
                    } catch (InvalidDataException ex) {
                        showError("CSV error: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
        	showError("Unexpected error: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        new MainFrame();
    }
    
}
