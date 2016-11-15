package odrive;

import helper.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;


/**
 *
 * @author Austin Copeman
 * @version 1.1
 */
public class OGraph{
    private TimeSeries timeSeries;
  
    /**
     *
     * @param name
     */
    public OGraph() {       

    }
    
    private XYDataset createDataset(String seriesName){
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        
        timeSeries = new TimeSeries(seriesName);

        dataset.addSeries(timeSeries);
        return dataset;
    }
    
    /**
     * Creates a chart using JFreeChart
     * @param dataset Data for X and Y
     * @param chartTitle Name of the chart
     * @param timeAxisLabel Label for X Axis
     * @param valueAxisLabel label for Y Axis
     * @return a chart
     */
    private JFreeChart createChart(final XYDataset dataset, String chartTitle, String timeAxisLabel, String valueAxisLabel) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                chartTitle, 
                timeAxisLabel, 
                valueAxisLabel, 
                dataset, 
                false, //include a legend 
                true, //include tooltips 
                false //urls
        ); 
        
        return chart;
    }
    
    /**
     * Creates the graph 
     * @param chartTitle Title of the chart
     * @param xAxisTitle Title of the X axis
     * @param yAxisTitle Title of the Y axis
     * @param width Width of the chart
     * @param height Height of the chart
     * @return a panel
     */
    public JPanel createGraphPanel(String chartTitle, String xAxisTitle, String yAxisTitle, int width, int height){
        final XYDataset dataset = createDataset(yAxisTitle);
        final JFreeChart chart = createChart(dataset,chartTitle,xAxisTitle,yAxisTitle);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
        chartPanel.setMouseZoomable(true, false);
        
        return chartPanel;
    }
    
    /**
     * Adds a time item to the graph at system time and location
     * @param data
     */
    public void addTimeItem(double data){
        DateTime dt = new DateTime();
        Date time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        int seconds = dt.getSecond();
        int minutes = dt.getMinute();
        int hours = dt.getHour(); 
        int day = dt.getDay();
        int month = dt.getMonth();
        int year = dt.getYear();
        
        timeSeries.addOrUpdate(new Second(seconds, minutes, hours, day, month, year), data);
        
//        try {
//            
//            timeSeries.addOrUpdate(new Second(sdf.parse(dt.getCustomDate("HH:mm:ss"))), data);
//        } catch (ParseException ex) {
//            Logger.getLogger(OGraph.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    
}
