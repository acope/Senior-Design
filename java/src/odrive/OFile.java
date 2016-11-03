package odrive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dana
 */
public class OFile {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        OFile excel = new OFile();
        CreateWBook();
         
        
        excel.ExcelWrite("1256,2500,10,10,40");   
        excel.ExcelWrite("1111,2222,13,54,11");
        excel.ExcelWrite("23,30,2222,0,45");
        
    }
    String timestamp;  
    String motor, input, output, voltage;
    String[] separated; 
    String[] redData = new String[7]; 
    
    public void ExcelWrite(String rawdata) throws IOException{
        
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
      for (int i=0; i<separated.length; i++){
          redData[2+i]=separated[i]; 
      }  
      
        HSSFWorkbook wb = OFile.readFile("Wave Water Works.xls"); 

        
        HSSFSheet sheet = wb.getSheetAt(0); 
        int writtenRows = sheet.getPhysicalNumberOfRows();
        int newRowNum = writtenRows+1;
        Row newRow = sheet.createRow(newRowNum);

        System.out.println("Physical # of Rows: " + writtenRows);
        System.out.println("Where New Row Should Go: " + newRowNum);
    
 
        for(int i=0; i<redData.length; i++){
            Cell c = newRow.createCell(i); 
            c.setCellValue(redData[i]);
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            c.setCellStyle(style);
        } 
        
        FileOutputStream out = new FileOutputStream("Wave Water Works.xls"); 
        wb.write(out);
        wb.close(); 
        
    } 


	public static HSSFWorkbook readFile(String filename) throws IOException {
	    NPOIFSFileSystem fis = new NPOIFSFileSystem(new File(filename)); 
	    try {
	        return new HSSFWorkbook(fis);
	    } finally {
	        fis.close();
	    }
	} 
public static void CreateWBook() throws IOException{
        String[] labels = {"Date", "Time", "Arduino timestamp", "Motor RPM", "Input RPM", "Output RPM", "Voltage"}; 
        FileOutputStream newBook = new FileOutputStream("Wave Water Works.xls");
        HSSFWorkbook workbook = new HSSFWorkbook(); 
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
        
        
        workbook.write(newBook);
        workbook.close();
}}