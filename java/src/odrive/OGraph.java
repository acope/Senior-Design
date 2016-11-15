package odrive;

import helper.DateTime;
import org.jfree.chart.axis.ValueAxis;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;


/**
 * WARNING!!! Causes Thread error, unable to diagnose.
 * Possible fix: SwingWorker
 * What is working: Creation of graph
 * 
 * How to use: Add as a panel in GUI Frame call createGraphPanel which will create the panel
 * @author Austin Copeman
 * @version 1.1
 */
public class OGraph{
    
    private TimeSeries series;//TimeSeries data
    private TimeSeriesCollection tsc;
  
    /**
     *
     * @param name
     */
    public OGraph() {       

    }
    
     /**
     * Creates the graph panel
     * @param chartTitle Title of the chart
     * @param xAxisTitle Title of the X axis
     * @param yAxisTitle Title of the Y axis
     * @param width Width of the chart
     * @param height Height of the chart
     * @return a panel
     */
    public JPanel createGraphPanel(String chartTitle, String xAxisTitle, String yAxisTitle, int width, int height){
        final JFreeChart chart = createChart(createDataset(yAxisTitle),chartTitle,xAxisTitle,yAxisTitle);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
        chartPanel.setMouseZoomable(true, false);
        
        return chartPanel;
    }
    
    private XYDataset createDataset(String seriesName){
        series = new TimeSeries(seriesName); //Line of the series

        addTimeItem(0);

        tsc = new TimeSeriesCollection(series);
        tsc.addSeries(series);
        return tsc;
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
        
        final XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        //axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 100.0); 
        
        return chart;
    }
    
    /**
     * Adds a time item to the graph at system time and location
     * @param data
     */
    public void addTimeItem(double data){
        DateTime dt = new DateTime();
        
        series.addOrUpdate(new Second(dt.getSecond(), dt.getMinute(), dt.getHour(), dt.getDay(), dt.getMonth(), dt.getYear()), data);
        
    }

    

    
    
}
