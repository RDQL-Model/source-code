import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.*; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 

public class RegionRandom { 
	
	
	public HashMap userList = new HashMap();
	public Vector phoneModeVec = new Vector();
	public HashMap phoneBrandUserAgeMap = new HashMap();
	
	double  xCoordinate = 0;
	double  yCoordinate = 0;
	
    public void GetOneCoordinate(double x, double y, double xScale, double yScale){
    	  
    	
    	
    	this.xCoordinate = 0;
    	this.yCoordinate = 0;
    	Random ra =new Random();
    	this.xCoordinate = ra.nextDouble()*xScale + x;
    	this.yCoordinate = ra.nextDouble()*yScale + y;
    	

     	  
      }
	
    
    
    
    
	public static void main(String[] args) {


		RegionRandom test = new RegionRandom();
		
		for(int i=0;i<10;i++){
			
			test.GetOneCoordinate(1.5, 4.5, 2.2, 3.1);
			System.out.println(i+1+" ***************");
			System.out.println("x:"+test.xCoordinate+"  y:"+test.yCoordinate);
			
			
			
		}



 } 

} 

