package flightsim.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityMonitor {
	private List<ActivityObserver> observers = new ArrayList<>();
	private List<Dialog> activeDialogs = new ArrayList<>();
	
	public void addObserver(ActivityObserver observer) {
		observers.add(observer);
	}
	
	public void removeObserver(ActivityObserver observer) {
		observers.remove(observer);
	}
	
	public void notifyActivity() {
		for(ActivityObserver observer  : observers) {
			observer.onUserActivity();
		}
	}
	
	public void addActivityListeners(Container container) {
		for(Component comp : container.getComponents()) {
			comp.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					notifyActivity();
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					notifyActivity();
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
					notifyActivity();
				}
			});
			
			comp.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    notifyActivity();
                }
                
                @Override
                public void keyPressed(KeyEvent e) {
                    notifyActivity();
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                    notifyActivity();
                }
            });
			
			if (comp instanceof Container) {
                addActivityListeners((Container) comp);
            }
		}
	}
	
	public void registerDialog(Dialog dialog) {
		addActivityListeners(dialog);
		activeDialogs.add(dialog);
		
		dialog.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) {
				activeDialogs.remove(dialog);
			}
		});
	}
	
	public void registerContainer(Container container) {
		addActivityListeners(container);
	}
}