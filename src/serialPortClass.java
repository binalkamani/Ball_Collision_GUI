import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommDriver;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;


public class serialPortClass implements SerialPortEventListener{

	static String initStrTempCopy = "";
    SerialPort port = null;
	InputStream inputStream;
	static OutputStream outputStream;
	
	char[] rx_buff = new char[10];
	int rx_cnt=0;
	int rx_param=0;
	double val=0;
	int i=0;
	String s = "";
	int dispBallNo;
	double dispXPos;
	double dispYPos;
	
	public void SerialPort_Init(String portname){

        String wantedPortName = portname;
        System.setSecurityManager(null);
        String driverName = "com.sun.comm.Win32Driver"; // or get as a JNLP property
        CommDriver commDriver = null;
        CommPortIdentifier portId = null;  // will be set if port found
        try{commDriver = (CommDriver)Class.forName(driverName).newInstance();}catch(Exception e){}
        commDriver.initialize();
 
        //
        // Get an enumeration of all ports known to JavaComm
        //
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        //
        // Check each port identifier if 
        //   (a) it indicates a serial (not a parallel) port, and
        //   (b) matches the desired name.
        //

        while (portIdentifiers.hasMoreElements())
        {
            CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
            if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL &&
               pid.getName().equals(wantedPortName)) 
            {
                System.out.println("Port used : " + pid.getName());
                portId = pid;
                break;
            }
        }
        if(portId == null)
        {
            System.err.println("Could not find serial port " + wantedPortName);
            System.exit(1);
        }

        try {
            port = (SerialPort) portId.open(
                "SerialPort_Display", // Name of the application asking for the port 
                10000   // Wait max. 10 sec. to acquire port
            );
        } catch(PortInUseException e) {
            System.err.println("Port already in use: " + e);
            System.exit(1);
        }
        try{port.setSerialPortParams(
            115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);
            }catch(UnsupportedCommOperationException e){}
		try {
            inputStream = port.getInputStream();
        } catch (IOException e) {}
        try {
            outputStream = port.getOutputStream();
        } catch (IOException e) {}
		try {
            port.addEventListener(this);
		} catch (TooManyListenersException e) {}
        port.notifyOnDataAvailable(true);
        System.out.println("Inputstream : " + inputStream);
        System.out.println("Outputstream : " + outputStream);
    }
	
	public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
	        case SerialPortEvent.BI:
	        case SerialPortEvent.OE:
	        case SerialPortEvent.FE:
	        case SerialPortEvent.PE:
	        case SerialPortEvent.CD:
	        case SerialPortEvent.CTS:
	        case SerialPortEvent.DSR:
	        case SerialPortEvent.RI:
	        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	            break;
	        case SerialPortEvent.DATA_AVAILABLE:
		        char c = 0;
	
		        try {
		            while (inputStream.available() > 0) {
		                c = (char)inputStream.read();
		                System.out.println(c);
		                if(Main.tgtInitSts == false){
		                	initStrTempCopy += c;
		                	// Check if init string received,
		                	// If received, update the UI, send ball params
		                	uartInithdlr();
		                }
		                else
		                {
		                	// Target is initialized, the character received should be ball_params
		                	switch (c)
		            		{
		            			case '<':
		            			{
//		            				System.out.println("<");
		            				rx_cnt=0;
		            				break;
		            			}
		            			case '>':
		            			{
//		            				System.out.println(">");
		            			}
		            			case ',':
		            			{
		            				s = String.valueOf(rx_buff);
		            				s = s.replaceAll("[^\\d.]", "");
		            				switch(rx_param)
		            				{
		            				    // Ball number
		            					case 0:
		            						dispBallNo = Integer.parseInt(s);
		            						break;
		            					// X - Position of Ball Number
		            					case 1:
				            				val = Double.parseDouble(s);
//				            				System.out.println(val);
		            						dispXPos = val;
		            						break;
		            					// Y - Position of Ball Number
		            					case 2:
				            				val = Double.parseDouble(s);
//				            				System.out.println(val);
		            						dispYPos = val;
		            						break;
		            				}
		            				if(c == '>')
		            				{
		            					rx_param =0;
		            					Arrays.fill(rx_buff, ' ');
			            				Main.cur_x_pos.set(dispBallNo, (int)Math.round(dispXPos));
			            				Main.cur_y_pos.set(dispBallNo, (int)Math.round(dispYPos));
		            					if(Main.mode == 1)
		            					{
				            				Main.ex.repaint();
		            					}
		            				}
		            				else
		            					rx_param++;
		            				rx_cnt=0;
		            				val=0;
		            				break;
		            			}
		            			default:
		            			{
		            				rx_buff[rx_cnt] = c;
		            				rx_cnt++;
		            				break;
		            			}
		            		}
		                }
		            }
		        } catch (IOException e) {}
		        break;
	    }
    }
	
	public void uartInithdlr(){
    	if(initStrTempCopy.equals(Main.tgtInitStr)){
    		Main.tgtInitSts = true;
        	displayClass.updateUartStsString();
        	System.out.println("UART Initialized");
        	try {
                outputStream.write((char)Main.hstInitStartStr);
                char [] chars = String.valueOf(Main.no_of_balls).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.period).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.x_init_pos).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.y_init_pos).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l1_x1).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l1_y1).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l1_x2).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l1_y2).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l2_x1).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l2_y1).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l2_x2).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write(',');
                chars = String.valueOf(Main.l2_y2).toCharArray();
                for (char charValue : chars) {
                    outputStream.write(charValue);
                }
                outputStream.write((char)Main.hstInitStopStr);
            } catch (IOException e) {}
    	}

	}
}
