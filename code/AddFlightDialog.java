package flightsim.gui;

import java.awt.*;
import java.awt.event.*;

public class AddFlightDialog extends Dialog implements ActionListener {
    private TextField fromField = new TextField(5);
    private TextField toField = new TextField(5);
    private TextField timeField = new TextField(8);
    private TextField durationField = new TextField(8);
    
    private Button okButton = new Button("OK");
    private Button cancelButton = new Button("Cancel");
    
    private boolean confirmed = false;

    public AddFlightDialog(Frame parent) {
        super(parent, "Add Flight", true);
        setLayout(new BorderLayout());
        
        Panel inputPanel = new Panel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new Label("From:"));
        inputPanel.add(fromField);
        inputPanel.add(new Label("To:"));
        inputPanel.add(toField);
        inputPanel.add(new Label("Time (HH:MM):"));
        inputPanel.add(timeField);
        inputPanel.add(new Label("Duration (min):"));
        inputPanel.add(durationField);
        
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        setSize(300, 180);
        setLocationRelativeTo(parent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            confirmed = true;
            setVisible(false);
        } else if (e.getSource() == cancelButton) {
            confirmed = false;
            setVisible(false);
        }
    }

    public boolean isConfirmed() { 
    	return confirmed; 
    }
    public String getFromCode() { 
    	return fromField.getText().trim();
    }
    public String getToCode() { 
    	return toField.getText().trim(); 
    }
    public String getTime() { 
    	return timeField.getText().trim(); 
    }
    public String getDurationText() { 
    	return durationField.getText().trim(); 
    }
}