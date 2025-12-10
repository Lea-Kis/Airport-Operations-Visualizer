package flightsim.gui;
import java.awt.*;
import java.awt.event.*;

public class AddAirportDialog extends Dialog implements ActionListener{
	private TextField codeField = new TextField(5);
    private TextField nameField = new TextField(15);
    private TextField xField = new TextField(5);
    private TextField yField = new TextField(5);
    
    private Button okButton = new Button("OK");
    private Button cancelButton = new Button("Cancel");
    
    private boolean confirmed = false;
    
    public AddAirportDialog(Frame parent) {
    	super(parent, "Add Airport", true);
    	setLayout(new BorderLayout());
    	
    	Panel inputPanel = new Panel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new Label("Code:"));
        inputPanel.add(codeField);
        inputPanel.add(new Label("Name:"));
        inputPanel.add(nameField); 
        inputPanel.add(new Label("X:"));
        inputPanel.add(xField);
        inputPanel.add(new Label("Y:"));
        inputPanel.add(yField);
        
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
    public String getCode() { 
    	return codeField.getText().trim(); 
    }
    public String getName() { 
    	return nameField.getText().trim();
    }
    public String getXText() { 
    	return xField.getText().trim(); 
    }
    public String getYText() { 
    	return yField.getText().trim(); 
    }
}