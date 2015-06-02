import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;


public class displayClass extends JDialog
        implements ActionListener {

	private static final long serialVersionUID = 6227185700883851501L;
	private JButton startButton;
    private JButton stopButton;
    private JButton quitButton;
    private volatile static JLabel uartTitleStsString = new JLabel("UART STATUS:", JLabel.CENTER);
    private volatile static JLabel modeTitleStsString = new JLabel("Mode:", JLabel.CENTER);
    public volatile static JLabel modeStsString = new JLabel("", JLabel.CENTER);
    private volatile static JLabel uartStsString = new JLabel("UART NOT INITIALIZED", JLabel.CENTER);
    private JPanel display;
    public static int red=0,green=0,blue=0;
    
//    public volatile static int ball_x_pos = Main.x_init_pos;
//    public volatile static int ball_y_pos = Main.y_init_pos;

    public displayClass() {
        initUI();
    }

    private void initUI() {

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel();
        leftPanel.setFocusable(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        startButton = new JButton("Start");
        startButton.setFocusPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	try {
					serialPortClass.outputStream.write((char)Main.hstStartChar);
				} catch (IOException e) {
					e.printStackTrace();
				}
           }
        });

        stopButton = new JButton("Stop");
        stopButton.setFocusPainted(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	startButton.setText("Resume");
            	try {
					serialPortClass.outputStream.write((char)Main.hstStopChar);
				} catch (IOException e) {
					e.printStackTrace();
				}
           }
        });

        quitButton = new JButton("Quit");
        quitButton.setFocusPainted(false);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
				if(Main.mode == 0)
				{
					for(int i=0;i<Main.no_of_balls;i++)
					{
						Main.writer.println(String.valueOf(i) + " " + String.valueOf(Main.cur_x_pos.get(i)) + " " + String.valueOf(Main.cur_y_pos.get(i)));
					}
					Main.writer.close();
					
				}
            	try {
					serialPortClass.outputStream.write((char)Main.hstStopChar);
				} catch (IOException e) {
					e.printStackTrace();
				}
                System.exit(0);
           }
        });

        startButton.setMaximumSize(new Dimension(125,22));
        stopButton.setMaximumSize(new Dimension(125,22));
        quitButton.setMaximumSize(new Dimension(125,22));
        startButton.setMinimumSize(new Dimension(125,22));
        stopButton.setMinimumSize(new Dimension(125,22));
        quitButton.setMinimumSize(new Dimension(125,22));

        leftPanel.add(modeTitleStsString);
        leftPanel.add(modeStsString);
        leftPanel.add(Box.createRigidArea(new Dimension(25, 21)));
        leftPanel.add(uartTitleStsString);
        uartStsString.setForeground(Color.RED);
        leftPanel.add(uartStsString);
        leftPanel.add(Box.createRigidArea(new Dimension(25, 21)));
        leftPanel.add(startButton);
        leftPanel.add(Box.createRigidArea(new Dimension(25, 7)));
        leftPanel.add(stopButton);
        leftPanel.add(Box.createRigidArea(new Dimension(25, 7)));
        leftPanel.add(quitButton);
        leftPanel.add(Box.createRigidArea(new Dimension(25, 56)));

        bottom.add(leftPanel);
        bottom.add(Box.createRigidArea(new Dimension(20, 0)));

        display = new panel();

        bottom.add(display);
        add(bottom);
        pack();
        
        setTitle("Ball Bouncing GUI");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {


    }
    
    public static void updateUartStsString(){
    	uartStsString.setForeground(Color.GREEN);
    	uartStsString.setText("UART INITIALIZED");
    }
}
class panel extends JPanel
{
	private static final long serialVersionUID = -2184467143511359816L;
	private int dmnsn = 500;

	public panel(){
		super.setMinimumSize(new Dimension(dmnsn,dmnsn));
		super.setPreferredSize(new Dimension(dmnsn, dmnsn));
		super.setMaximumSize(new Dimension(dmnsn,dmnsn));
	    super.setBorder(LineBorder.createBlackLineBorder());
	}
    
	@Override
	public void paintComponent(Graphics g){
		if(Main.mode == 1)
		{
		    super.paintComponent(g);
		    this.setOpaque(true);
		    this.setBackground(Color.white);
	
		    /* Move from 4th Quadrant to 1st Quadrant */
		    Graphics2D g2 = (Graphics2D)g;
		    AffineTransform at = g2.getTransform();
	        at.translate(0, getHeight());
	        at.scale(1, -1);
	        g2.setTransform(at);
	        
	        // Drawing thicker lines defined by input.txt
	        g2.setStroke(new BasicStroke(3));
	        g2.setColor(Color.red);
		    g2.drawLine(Main.l1_x1, Main.l1_y1, Main.l1_x2, Main.l1_y2);
		    g2.drawLine(Main.l2_x1, Main.l2_y1, Main.l2_x2, Main.l2_y2);
	
	        for(int i=0;i<Main.no_of_balls;i++){
	        	Color test = Color.decode(Main.cur_ball_color.get(i));
	        	g.setColor(Color.BLACK);
	    	    g.drawOval(Main.cur_x_pos.get(i), Main.cur_y_pos.get(i), 20, 20);
	        	g.setColor(test);
	    	    g.fillOval(Main.cur_x_pos.get(i), Main.cur_y_pos.get(i), 20, 20);
	        }
		}
		else if(Main.mode == 0)
		{
		    super.paintComponent(g);
		    this.setOpaque(true);
		    this.setBackground(Color.black);
		}
	}
}