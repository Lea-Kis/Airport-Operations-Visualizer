package flightsim.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import flightsim.model.Airport;

public class AirportMapCanvas extends Canvas{
	private List<Airport> airports;
	private boolean[] visible;
	private Airport selected = null;
	private boolean blinkState = true;
	private Thread blinkThread;
	
	
	public AirportMapCanvas() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
	}
	
	
	public void setAirports(List<Airport> airports) {
		this.airports = airports;
		if(airports!=null) {
			this.visible = new boolean[airports.size()];
			for(int i=0; i<visible.length; i++)
			{
				visible[i] = true;
			}
		}
		else {
			this.visible = null;
		}
		repaint();
	}
	
	
	public void setVisibility(int index, boolean visible) {
        if (index >= 0 && index < this.visible.length) {
            this.visible[index] = visible;
            repaint();
        }
    }
	
	
	public void setSelectedAirport(Airport airport) {
        if(blinkThread!=null) {
        	blinkThread.interrupt();
        	blinkThread = null;
        }
        
        this.selected = airport;
        
        if(airport != null)
        {
        	BlinkRunnable blinkRunnable = new BlinkRunnable();
        	blinkThread = new Thread(blinkRunnable);
            blinkThread.setDaemon(true);
            blinkThread.start();
        }
        else {
        	blinkState = true;
        	repaint();
        }
        
        if(getParent() instanceof MainFrame) {
        	MainFrame main = (MainFrame) getParent();
        	if(airport!=null) main.pauseInactivityTimer();
        	else main.resumeInactivityTimer();
        }
    }
	
	
	public Airport getSelectedAirport() {
		return selected;
	}
	
	
	private void handleMouseClick(int x, int y) {
		if (getParent() instanceof MainFrame) {
	        ((MainFrame) getParent()).startInactivityTimer();
	    }
        Airport clicked = findAirport(x, y);
        if (clicked != null) {
            if (selected == clicked) {
                setSelectedAirport(null);
            } 
            else {
                setSelectedAirport(clicked);
            }
        }
    }
	
	private Airport findAirport(int x, int y) {
		if(airports == null) return null;
		for(int i=0; i< airports.size(); i++) {
			if(!visible[i]) continue;
			Airport a = airports.get(i);
			Rectangle r = getAirportRectangle(a);
			if(r.contains(x, y)) {
				return a;
			}
		}
		return null;
	}
	
	
	private Rectangle getDrawingBounds() {
	    Dimension size = getSize();
	    int margin = 20;
	    int drawWidth = size.width - 2 * margin;
	    int drawHeight = size.height - 2 * margin;
	    return new Rectangle(margin, margin, drawWidth, drawHeight);
	}
	
	
	private Rectangle getAirportRectangle(Airport a) {
		Rectangle bounds = getDrawingBounds();
		int px = bounds.x + (int) ((a.getX() + 90) * bounds.width/180.0);
		int py = bounds.y + bounds.height - (int) ((a.getY() + 90) * bounds.height / 180.0);
		return new Rectangle(px -6, py -6, 12, 12);
	}
	
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(airports == null) return;
		
		for(int i=0; i<airports.size(); i++) 
		{
			if(!visible[i]) continue;
			Airport a = airports.get(i);
			Rectangle r = getAirportRectangle(a);
			if(a == selected && blinkState)
			{
				g.setColor(Color.RED);
			}
			else g.setColor(Color.GRAY);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(Color.BLACK);
			g.drawRect(r.x, r.y, r.width, r.height);
			g.drawString(a.getCode(), r.x + r.width + 4, r.y + r.height -2);
			for(int j=0; j<airports.size(); j++) {
				if(!visible[j]) continue;
				Airport b = airports.get(j);
				Rectangle p = getAirportRectangle(b);
				g.setColor(Color.PINK);
				g.drawLine(10, 10, 50, 50);
				g.drawLine(r.x, r.y, p.x, p.y);
			}
		}
	}
	
	private class BlinkRunnable implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted() && selected != null) 
                {
                    blinkState = !blinkState;
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            repaint();
                        }
                    });
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
	
}