package flightsim.util;

import java.awt.*;
import flightsim.gui.MainFrame;

public class InactivityTimer implements Runnable{
	private final MainFrame frame;
	private volatile boolean cancelled = false;
	
	public InactivityTimer(MainFrame frame) {
		this.frame = frame;
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(55000);
			if(cancelled) return;
			
			EventQueue.invokeLater(() -> {
				if(!cancelled) {
					frame.askUserToContinue();
				}
			});
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}