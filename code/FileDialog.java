package flightsim.gui;

import java.awt.*;
import java.awt.event.*;

public class FileDialog extends Dialog implements ActionListener{
	private TextField fileNameField = new TextField(20);
    private Button okButton = new Button("OK");
    private Button cancelButton = new Button("Cancel");
    private boolean confirmed = false;
    
    public FileDialog(Frame parent, String title) {
        super(parent, title, true);
        setLayout(new BorderLayout());

        Panel inputPanel = new Panel(new FlowLayout());
        inputPanel.add(new Label("File name:"));
        inputPanel.add(fileNameField);

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setSize(300, 120);
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

    public String getFileName() {
        return fileNameField.getText().trim();
    }
}