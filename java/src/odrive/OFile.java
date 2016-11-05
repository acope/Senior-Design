package odrive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
 */
public class OFile {

    private String wkName;  
    private NPOIFSFileSystem fis;
    private HSSFWorkbook hssf;
    private HSSFWorkbook workbook;
    private FileOutputStream newBook;
    private HSSFWorkbook wb; 
    private FileOutputStream out;
    
    
    private String timestamp;  
    private String motor, input, output, voltage;
    private String[] separated; 
    private String[] redData = new String[7]; 
    private int date;
    private String time;
    private String timeDelim;
    private String[] separatedTime;
    private String namePt;
    /**
     * Default Constructor
     */
    public OFile(){
    }
    
    /**
     * Returns the file name of workbook
     * @return 
     */
    public String GetFileName(){
        return wkName; 
    }
    
    /**
     * Takes in raw data from Arduino and writes to Excel sheet
     * rawdata is in form of Arduino Timestamp,MotorRPM,Input RPM, Output RPM, Voltage
     * Other calculations for Power, Current, ect are performed as well
     * @param rawdata 
     */
    public void ExcelWrite(String rawdata){
        
      //Timestamp from Java 
      Calendar calendar = Calendar.getInstance();
      java.util.Date now = calendar.getTime(); 
      java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime()); 
      timestamp = currentTimestamp.toString(); 
      String timedelimit = "[ ]+"; 
      String[] datetime = timestamp.split(timedelimit); 
      redData[0] = datetime[0];
      redData[1] = datetime[1]; 
      
      //Separate raw data by commas
      String rawdelimit = "[,]+"; 
      separated = rawdata.split(rawdelimit); 
       
        //copy separated data, with Java date and time first, into array
        System.arraycopy(separated, 0, redData, 2, separated.length);  
      
       
        wb = readFile(wkName);
        HSSFSheet sheet = wb.getSheetAt(0); 
        int writtenRows = sheet.getPhysicalNumberOfRows();
        int newRowNum = writtenRows+1;
        Row newRow = sheet.createRow(newRowNum);
 
        for(int i=0; i<redData.length; i++){
            Cell c = newRow.createCell(i); 
            c.setCellValue(redData[i]);
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            c.setCellStyle(style);
        } 
        
        try { 
            out = new FileOutputStream(wkName);
            wb.write(out);
            wb.close(); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
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
        String[] labels = {"Date", "Time", "Arduino timestamp", "Motor RPM", "Input RPM", "Output RPM", "Voltage"}; 
        
        date = Calendar.getInstance().get(Calendar.DAY_OF_YEAR); 
        time = Calendar.getInstance().getTime().toString(); 
        timeDelim = "[ :]+"; 
        separatedTime = time.split(timeDelim);
        namePt = date + separatedTime[3] + separatedTime[4];         
        wkName = ("WaveWaterWorks_" + namePt + ".xls"); //Create new excel file with name and date stamp 
        
        try {
            newBook = new FileOutputStream(wkName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        workbook = new HSSFWorkbook(); 
        Sheet sheet = workbook.createSheet("Data Logging"); 
        
        Row headerRow = sheet.createRow(0);  
        
        for(int l=0; l<labels.length; l++){
            Cell c = headerRow.createCell(l); 
            c.setCellValue(labels[l]);
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
            workbook.close();
        } catch (IOException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Closes all instances used in workbook creation for editing
     */
    public void closeWorkBook(){
        try {
            newBook.close();
            hssf.close();
            workbook.close();
            fis.close();
            wb.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(OFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}