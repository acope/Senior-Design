package odrive;

import helper.DataConversion;
import helper.DateTime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 *
 * @author Dana
 * @version 1.1
 */
public class OFile {

    private String workBookName;  
    private NPOIFSFileSystem fis;
    private HSSFWorkbook hssf;
    private HSSFWorkbook workbook;
    private HSSFSheet HSSFSheet;
    private FileOutputStream newBook;
    private HSSFWorkbook wb; 
    private FileOutputStream out;
    private final DataConversion convert;
    private DateTime dt;
    
    private int datastamp;
    private int motorRPM;
    private int inputRPM;
    private int outputRPM;
    private double voltage;
    private final int VOLTAGE_DIVIDER_RESISTANCE = 1500000;
    
    private final String[] readData = new String[10]; 
    /**
     * Default Constructor
     */
    public OFile(){
        convert = new DataConversion();
        dt = new DateTime();
    }
    
    /**
     * Returns the file name of workbook
     * @return 
     */
    public String GetFileName(){
        return workBookName; 
    }
    
    /**
     * Takes in raw data from Arduino and writes to Excel sheet
     * rawdata is in form of Arduino Timestamp,MotorRPM,Input RPM, Output RPM, Voltage
     * Other calculations for Power, Current, ect are performed as well
     * @param rawdata 
     */
    public void ExcelWrite(String rawdata){
        DecimalFormat df = new DecimalFormat("##.##E0");
        //Timestamp from Java 
        String date = dt.getDate();
        String time = dt.getTime();
        readData[0] = date;
        readData[1] = time;
        //Separate raw data by commas
        String rawdelimit = "[,]+"; 
        String[] separated = rawdata.split(rawdelimit); 
        //copy separated data, with Java date and time first, into array
       // System.arraycopy(separated, 0, readData, 2, separated.length);
        for (int i=0; i<separated.length; i++){
          readData[i+2] = separated[i];
          if(i==0){
              datastamp = Integer.parseInt(separated[i]);
          }
          if(i==1){
              motorRPM = Integer.parseInt(separated[i]);
          }
          if(i==2){
              inputRPM = Integer.parseInt(separated[i]);
          }
          if(i==3){
              outputRPM = Integer.parseInt(separated[i]);
          }
          if(i==4){
              int rawVoltage = Integer.parseInt(separated[i]);
              voltage = (((double)rawVoltage * 5 * 14.5)  / 1024); //255 = range, 5 = 0v to 5v 14.5 = voltage divider ratio
          }
        }
        readData[7] = df.format(convert.calculateCurrent(voltage, VOLTAGE_DIVIDER_RESISTANCE)); //Current
        readData[8] = df.format(convert.calculatePower(voltage, VOLTAGE_DIVIDER_RESISTANCE)); //Power
        readData[9] = "-";//Need to calculate efficiency
        wb = readFile(workBookName);
        HSSFSheet = wb.getSheetAt(0); 
        Row newRow = HSSFSheet.createRow(HSSFSheet.getPhysicalNumberOfRows());
 
        for(int i=0; i<readData.length-1; i++){   
            Cell c = newRow.createCell(i); 
            c.setCellValue(readData[i]);
        } 
        
        try { 
            out = new FileOutputStream(workBookName);
            wb.write(out);
            //Close output stream
            out.close();
            wb.close(); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, "File not found", ex);
        } catch (IOException ex){
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }  
    } 

    /**
    * Creates a new workbook
    * @param filename
    * @return workbook name
    */
    public HSSFWorkbook readFile(String filename){
	     
        try {
            fis = new NPOIFSFileSystem(new File(filename));
           hssf = new HSSFWorkbook(fis);
        } catch (IOException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        
        return hssf;
    } 
        
        /**
         * Creates Excel workbook
         * Must be created before writing to excel sheet
         */
    public void CreateWBook(){
        String[] labels = {"Date", "Time", "Arduino Datastamp", "Motor RPM", "Input RPM", "Output RPM", "Voltage", "Current", "Power"}; 
        
        String date = dt.getDateNumOnly();
        String time = dt.getTimeNumOnly();         
        
        workBookName = ("WaveWaterWorks_" + date + time + ".xls"); //Create new excel file with name and date stamp 
        
        try {
            newBook = new FileOutputStream(workBookName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        workbook = new HSSFWorkbook(); 
        Sheet sheet = workbook.createSheet("Data Logging"); 
        
        Row labelRow = sheet.createRow(0);  
        for(int i=0; i<labels.length; i++){
            Cell c = labelRow.createCell(i); 
            c.setCellValue(labels[i]);
            CellStyle labelStyle = sheet.getWorkbook().createCellStyle(); 
            
            Font font = sheet.getWorkbook().createFont(); 
            font.setBold(true); 
            font.setFontHeightInPoints((short)14);
            
            labelStyle.setFont(font); 
            labelStyle.setAlignment(HorizontalAlignment.CENTER);
            labelStyle.setVerticalAlignment(VerticalAlignment.CENTER);    
            c.setCellStyle(labelStyle);
            
            sheet.setDefaultColumnWidth(30);
            sheet.createFreezePane(0, 1);
        }
        
        
        try {
            workbook.write(newBook);
            newBook.close();
            workbook.close();
            
        } catch (IOException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}