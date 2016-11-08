package odrive;

import helper.DateTime;
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
    private DateTime dt;
  
    /**
     *
     * @param name
     */
    public OGraph() {       
        //super(name);
    }
    
    private XYDataset createDataset(String seriesName){
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.setDomainIsPointsInTime(true);
        
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
                true, //include a legend 
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
        dt = new DateTime();
        timeSeries.addOrUpdate(new Second(dt.getSecond(), dt.getMinute(), dt.getHour(), dt.getDay(), dt.getMonth(), dt.getYear()), data);
    }
    
    
}
