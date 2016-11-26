package odrive.model;

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
    private final DateTime dt;
    
    private int datastamp;
    private int samplingRate;
    private int motorRPM;
    private double inputRPM;
    private double outputRPM;
    private double voltage;
    private final int VOLTAGE_DIVIDER_RESISTANCE = 16; //In OHMs
    
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
     * @return work book name
     */
    public String GetFileName(){
        return workBookName; 
    }
    
    /**
     * Takes in raw data from Arduino and writes to Excel sheet
     * rawdata is in form of Arduino Timestamp,MotorRPM,Input RPM, Output RPM, Voltage
     * Other calculations for Power, Current, ect are performed as well
     * @param rawdata string of raw data
     */
    public void ExcelWrite(String rawdata){
        DecimalFormat df = new DecimalFormat("####.####");
        //Timestamp from Java 
        String date = dt.getDate();
        String time = dt.getTime();
        //Separate raw data by commas
        String rawdelimit = "[,]+"; 
        String[] separated = rawdata.split(rawdelimit); 

        for (int i=0; i<separated.length; i++){
          //readData[i+2] = separated[i];
          if(i==0){
              samplingRate = Integer.parseInt(separated[i]);
          }
          if(i==1){
              datastamp = Integer.parseInt(separated[i]);
          }
          if(i==2){
              motorRPM = Integer.parseInt(separated[i]);
          }
          if(i==3){
              inputRPM = convert.convertRawRPM(Integer.parseInt(separated[i]), samplingRate)*3; //Multiply by 3 for ratio between gears
          }
          if(i==4){
              outputRPM = convert.convertRawRPM(Integer.parseInt(separated[i]), samplingRate);
          }
          if(i==5){
              voltage = convert.convertRawVoltage(Integer.parseInt(separated[i]));
          }
        }
        
        readData[0] = date;
        readData[1] = time;
        readData[2] = Integer.toString(datastamp);
        readData[3] = Integer.toString(motorRPM); //Read dc motor rpm
        readData[4] = df.format(inputRPM); //input to odrive
        readData[5] = df.format(outputRPM); //output of odrive
        readData[6] = df.format(outputRPM * 7); //Alternator RPM: Ratio between gears
        readData[7] = df.format(voltage);
        readData[8] = df.format(convert.calculateCurrent(voltage, VOLTAGE_DIVIDER_RESISTANCE)); //Current
        readData[9] = df.format(convert.calculatePower(voltage, VOLTAGE_DIVIDER_RESISTANCE)); //Power
        //readData[9] = "-";//Need to calculate efficiency
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
    * @param filename file name string to read
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
        String[] labels = {"Date", "Time", "Data Stamp", "Motor RPM", "Input RPM", "Output RPM", "Alternator RPM" ,"Voltage(V)", "Current(A)", "Power(W)"}; 
        
        String date = dt.getDateNumOnly();
        String time = dt.getTimeNumOnly();         
        
        workBookName = ("WaveWaterWorks_" + date + "_" + time + ".xls"); //Create new excel file with name and date stamp 
        
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
            
            sheet.setDefaultColumnWidth(15);
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