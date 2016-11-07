package odrive;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


/**
 *
 * @author Austin Copeman
 * @version 1.1
 */
public class OGraph extends ApplicationFrame{
    private XYSeries series;
  
    /**
     *
     * @param name
     */
    public OGraph(String name) {       
        super(name);
    }
    
    /**
     * Adds data to chart
     * @return a dataset
     */
    private XYDataset createDataset() {
        for (int i = 0; i < 11; i++) {
            series.add(i, 100.00);
        }
        //series.remove(INDEX);
        //series.add(INDEX, 0);
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    
    
    /**
     * Creates a chart using JFreeChart
     * @param dataset Data for X and Y
     * @param chartTitle Name of the chart
     * @param xAxisLabel Label for X Axis
     * @param yAxisLabel label for Y Axis
     * @return a chart
     */
    private JFreeChart createChart(final XYDataset dataset, String chartTitle, String xAxisLabel, String yAxisLabel) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
            chartTitle, // chart title
            xAxisLabel, // x axis label
            yAxisLabel, // y axis label
            dataset, // data
            PlotOrientation.VERTICAL,
            false, // include legend
            true, // tooltips
            false // urls
        );
        //Creates a new series for Y Axis
       series = new XYSeries(yAxisLabel);
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
        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset,chartTitle,xAxisTitle,yAxisTitle);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
        
        return chartPanel;
    }
    
    /**
     * Adds an item to the graph at (x,y)
     * @param x
     * @param y 
     */
    public void addItem(double x, double y){
        series.addOrUpdate(x, y);
    }
    
    
}
