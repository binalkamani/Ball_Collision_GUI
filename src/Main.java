import java.io.*;
import java.util.*;

public class Main {

	public static displayClass ex = new displayClass();
	static String tgtInitStr = "CM4";
	static char hstInitStartStr = '<';
	static char hstInitStopStr = '>';
	static char hstStartChar = 'S';
	static char hstStopChar = '.';
	public static int maxDimension = 500;
	static boolean tgtInitSts = false;
	static List<Integer> num = new ArrayList<Integer>();
	public volatile static int mode, no_of_balls, period, x_init_pos, y_init_pos, line_1, l1_x1, l1_y1, l1_x2, l1_y2, line_2, l2_x1, l2_y1, l2_x2, l2_y2;
	public volatile static List<String> cur_ball_color = new ArrayList<String>();
	public volatile static List<Integer> cur_x_pos = new ArrayList<Integer>();
	public volatile static List<Integer> cur_y_pos = new ArrayList<Integer>();
	static PrintWriter writer = null;
	static String[] ColourValues = new String[] { 
        "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", "#000000", 
        "#800000", "#008000", "#000080", "#808000", "#800080", "#008080", "#808080", 
        "#C00000", "#00C000", "#0000C0", "#C0C000", "#C000C0", "#00C0C0", "#C0C0C0", 
        "#400000", "#004000", "#000040", "#404000", "#400040", "#004040", "#404040", 
        "#200000", "#002000", "#000020", "#202000", "#200020", "#002020", "#202020", 
        "#600000", "#006000", "#000060", "#606000", "#600060", "#006060", "#606060", 
        "#A00000", "#00A000", "#0000A0", "#A0A000", "#A000A0", "#00A0A0", "#A0A0A0", 
        "#E00000", "#00E000", "#0000E0", "#E0E000", "#E000E0", "#00E0E0", "#E0E0E0", 
    };
	
    public static void main(String[] args) {
        Main main = new Main();

        // Reading file
        num = main.readFile("input.txt");
        main.setFileParams();
        if(mode == 1)
        	displayClass.modeStsString.setText("Visualization");
        else if(mode == 0)
        {
        	displayClass.modeStsString.setText("Computation");
			try {
				writer = new PrintWriter("output.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        //Initializing Display shared variables memory
        for(int i=0;i<Main.no_of_balls;i++){
        	cur_ball_color.add(ColourValues[i%56]);
        	cur_x_pos.add(Main.x_init_pos);
            cur_y_pos.add(Main.y_init_pos);
        }
        
        /* Serial Port operations */
       	serialPortClass serial = new serialPortClass();
        serial.SerialPort_Init("COM11");
       	
        /* Display operations */
        ex.setVisible(true);

//        /* Sample animation test */
//        for(int i=3; i<15; i++){
//            try {
//    			Thread.sleep(100);
//    		} catch (InterruptedException e) {
//    			e.printStackTrace();
//    		}
//        	cur_x_pos.set(0, 25*i);
//        	cur_y_pos.set(0, 25*i);
//            ex.repaint();
//        }

    }

	/* Reading the numbers from file */
	public List<Integer> readFile(String filename)
	{
		List<Integer> num_loc = new ArrayList<Integer>();
		try {    
            Scanner fileScanner = new Scanner(new FileReader(filename));    
            while (fileScanner.hasNextLine()) {    
				 while (fileScanner.hasNext()) { //Check if there is anything in the line  
					 if (fileScanner.hasNextInt()) { //Extract just numbers from the current line  
				         num_loc.add(fileScanner.nextInt());
				     }  
				     else {  
				         fileScanner.next();  
				     }  
				 }           
            }
            fileScanner.close();
	    }    
	    catch (FileNotFoundException e){    
	        System.out.println("Sorry! This file is not found");    
	    }  
		return num_loc;
	}	
	
	/* Set static values read from the file */
	public void setFileParams(){
       	mode		=(int)(num.get(0));
       	no_of_balls	=(int)(num.get(1));
       	period		=(int)(num.get(2));
       	x_init_pos	=(int)(num.get(3));
       	y_init_pos	=(int)(num.get(4));
       	line_1		=(int)(num.get(5));
       	l1_x1		=(int)(num.get(6));
       	l1_y1		=(int)(num.get(7));
       	l1_x2		=(int)(num.get(8));
       	l1_y2		=(int)(num.get(9));
       	line_2		=(int)(num.get(10));
       	l2_x1		=(int)(num.get(11));
       	l2_y1		=(int)(num.get(12));
       	l2_x2		=(int)(num.get(13));
       	l2_y2		=(int)(num.get(14));
	}
}