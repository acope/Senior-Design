package odrive;

import helper.DataConversion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    
    private String datastamp;
    private String motorRPM;
    private String inputRPM;
    private String outputRPM;
    private String voltage;
    private final int VOLTAGE_DIVIDER_RESISTANCE = 1500000;
    
    private final String[] readData = new String[10]; 
    /**
     * Default Constructor
     */
    public OFile(){
        convert = new DataConversion();
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
        String date = getDate();
        String time = getTime();
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
              datastamp = separated[i];
          }
          if(i==1){
              motorRPM = separated[i];
          }
          if(i==2){
              inputRPM = separated[i];
          }
          if(i==3){
              outputRPM = separated[i];
          }
          if(i==4){
              voltage = separated[i];
          }
        }
        readData[7] = df.format(convert.calculateCurrent(Integer.parseInt(voltage), VOLTAGE_DIVIDER_RESISTANCE)); //Current
        readData[8] = df.format(convert.calculatePower(Integer.parseInt(voltage), VOLTAGE_DIVIDER_RESISTANCE)); //Power
        readData[9] = "-";//Need to calcualte efficiency
        wb = readFile(workBookName);
        HSSFSheet = wb.getSheetAt(0); 
        Row newRow = HSSFSheet.createRow(HSSFSheet.getPhysicalNumberOfRows());
 
        for(int i=0; i<readData.length-1; i++){   
            Cell c = newRow.createCell(i); 
            c.setCellValue(readData[i]);
            //Need to figure out why they start out center justified then go left justified
//            CellStyle style = HSSFSheet.getWorkbook().createCellStyle();
//            style.setAlignment(HorizontalAlignment.CENTER);
//            style.setVerticalAlignment(VerticalAlignment.CENTER);
//            c.setCellStyle(style);
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
        String[] labels = {"Date", "Time", "Arduino Datastamp", "Motor RPM", "Input RPM", "Output RPM", "Voltage", "Calc. Current", "Calc. Power", "Calc. Efficiency"}; 
        
        String date = getDateNumOnly();
        String time = getTimeNumOnly();         
        
        workBookName = ("WaveWaterWorks_" + date + time + ".xls"); //Create new excel file with name and date stamp 
        
        try {
            newBook = new FileOutputStream(workBookName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        workbook = new HSSFWorkbook(); 
        Sheet sheet = workbook.createSheet("Data Logging"); 
        
        Row headerRow = sheet.createRow(0);  
        
        for(int i=0; i<labels.length; i++){
            Cell c = headerRow.createCell(i); 
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

    /**
     * Gets time from system
     * @return HH:mm:ss
     */
    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get time number only
     * @return HHmmss
     */
    public String getTimeNumOnly(){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get date 
     * @return MM/dd/yyyy
     */
    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get date numbers only
     * @return yyyyMMdd
     */
    public String getDateNumOnly(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get date and time
     * @return MM/dd/yyyy HH:mm:ss
     */
    public String getDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(Calendar.getInstance().getTime());
    }
}