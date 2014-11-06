package charts;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.jtronlabs.cbblibrary.R;

//This class can be improved
public class BarChartViewBuilder{
	  private Context myContext;
	  float screenDens;
	  XYMultipleSeriesRenderer mRenderer;
	  XYSeriesRenderer renderer;
	  
	  boolean valuesEmpty;
	  
	  public BarChartViewBuilder(Context ctx,float screenDensity){
		myContext=ctx;
		screenDens=screenDensity;
	  }
		  
	  public GraphicalView getBarChart(String chartTitle,String xAxis,String yAxis,long[] values){
		  int spacing = 5;
		  valuesEmpty=values.length==0;
		  XYSeries series;
		  if(valuesEmpty){
	        	series = new XYSeries("");
	        	series.add(0, 0);
	        }else{
	        	series = createSeries(values,spacing);
	        }
	        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();  // collection of series under one object.,there could any # of series
	        dataSet.addSeries(series);                
	        
	        stylizeRenderer(chartTitle,xAxis,yAxis);
	                
	        //some additional styling-add xlabel (most recent game #), and set pan Limits
	        for(int i =0;i<values.length;i++){//add x labels-which game
	        	mRenderer.addXTextLabel(i*spacing, ""+(values.length-i));
	        }
	        double[] panLimits={-5,(values.length*5),0,0}; // [panMinimumX, panMaximumX, panMinimumY, panMaximumY] 
	        mRenderer.setPanLimits(panLimits);
	        
	        GraphicalView view = ChartFactory.getBarChartView(myContext, dataSet, mRenderer,BarChart.Type.DEFAULT);
	        
        return view;
	  }
	  
	  public GraphicalView getBarChart(String chartTitle,String xAxis,String yAxis,int[] values){
		  int spacing = 5;
		  valuesEmpty=values.length==0;
		  XYSeries series;
		  if(valuesEmpty){
	        	series = new XYSeries("");
	        	series.add(0, 0);
	        }else{
	        	series = createSeries(values,spacing);
	        }
	        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();  // collection of series under one object.,there could any # of series
	        dataSet.addSeries(series);    
	        
	        stylizeRenderer(chartTitle,xAxis,yAxis);
	        
	        //some additional styling-add xlabel (most recent game #), and set pan Limits
	        for(int i =0;i<values.length;i++){
	        	mRenderer.addXTextLabel(i*spacing, ""+(values.length-i));
	        }
	        double[] panLimits={-5,(values.length*5),0,0}; // [panMinimumX, panMaximumX, panMinimumY, panMaximumY] 
	        mRenderer.setPanLimits(panLimits);
	        
	        GraphicalView view = ChartFactory.getBarChartView(myContext, dataSet, mRenderer,BarChart.Type.DEFAULT);
	        
          return view;
	  }
	  
//	  public GraphicalView getBarChart(String chartTitle,String xAxis,String yAxis,int[] values,String[] xLabels){
//		  int spacing = 8;
//		  valuesEmpty=values.length==0;
//		  	XYSeries series = createSeries(values,spacing);
//	        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();  // collection of series under one object.,there could any # of series
//	        dataSet.addSeries(series);                
//	        
//	        stylizeRenderer(chartTitle,xAxis,yAxis);
//	        
//	        //some additional styling-add xlabel (most recent game #), and set pan Limits
//	        for(int i =0;i<values.length;i++){//add x labels-which game
//	        	mRenderer.addXTextLabel(i*spacing, xLabels[i]);
//	        }
//	        
//	        if(xLabels.length>3){
//		        double[] panLimits={-5,(values.length*10),0,0}; // [panMinimumX, panMaximumX, panMinimumY, panMaximumY] 
//		        mRenderer.setPanLimits(panLimits);
//	        }else{
//	        	mRenderer.setPanEnabled(false,false);
//	        }
//	        
//	        GraphicalView view = ChartFactory.getBarChartView(myContext, dataSet, mRenderer,BarChart.Type.DEFAULT);
//	        
//      return view;
//	  }
	  
	  private void stylizeRenderer(String chartTitle,String xLabel,String yLabel){
		  	renderer = new XYSeriesRenderer();     // one renderer for one series
	        mRenderer = new XYMultipleSeriesRenderer();   // collection multiple values for one renderer or series
//	        renderer.setChartValuesSpacing(1F);
	        renderer.setChartValuesTextSize(20*screenDens);
	        renderer.setChartValuesTextAlign(Align.RIGHT);   
	        
	        renderer.setColor(myContext.getResources().getColor(R.color.light_yellow));
	        
	        if(valuesEmpty){
	        	renderer.setDisplayChartValues(false);
	        }else{
	        	renderer.setDisplayChartValues(true);
	        }
	        
	        mRenderer.addSeriesRenderer(renderer);
	        
	        mRenderer.setXLabels(0);//hides x labels
	        mRenderer.setYLabels(0);//hides y labels
	        
	        mRenderer.setYLabelsAlign(Align.RIGHT);
	        mRenderer.setBarWidth(25*screenDens);
	        mRenderer.setBarSpacing(0.5F);
	         
	        mRenderer.setChartTitle(chartTitle);
	        mRenderer.setXTitle(xLabel);
	        mRenderer.setAxisTitleTextSize(20*screenDens);
	        mRenderer.setYTitle(yLabel);
	        mRenderer.setChartTitleTextSize(25*screenDens);
	        mRenderer.setLabelsTextSize(18*screenDens);
	        
	        mRenderer.setLabelsColor(Color.WHITE);
	        mRenderer.setXLabelsColor(Color.WHITE);
	        
	        mRenderer.setZoomButtonsVisible(false);
	        mRenderer.setShowLegend(false);
	        mRenderer.setShowGridX(false);   
	        mRenderer.setShowGridY(false);              
	        mRenderer.setAntialiasing(true);
	        mRenderer.setPanEnabled(true,false);   
	        mRenderer.setZoomEnabled(false,false);
	        mRenderer.setShowAxes(false);
	        mRenderer.setInScroll(true);
	        
	        
	        mRenderer.setApplyBackgroundColor(true);
	        mRenderer.setBackgroundColor(Color.TRANSPARENT);
	        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));//color=transparent (NOTE:Color.Transparent not working)
	        
	        mRenderer.setXAxisMin(-5);
	        mRenderer.setYAxisMin(0);
	        mRenderer.setXAxisMax(20);
//	        mRenderer.setYAxisMax(1000000000);
	        
	        int[] margins = {(int)(50*screenDens),(int)(50*screenDens),(int)(50*screenDens),(int)(50*screenDens)};
	        mRenderer.setMargins(margins);
	  }
	  
	  private XYSeries createSeries(int[] values,int spacing){
		  XYSeries series = new XYSeries("");
	  		for(int i=0;i<values.length;i++){//give some extra space, full words are going on the x labels     
		        series.add(i*spacing,values[i]);
		    }
	  		return series;
	  }
	  
	  private XYSeries createSeries(long[] values,int spacing){
		  	XYSeries series = new XYSeries("");
	  		for(int i=0;i<values.length;i++){//give some extra space, full words are going on the x labels     
		        series.add(i*spacing,values[i]);
		    }
	  		return series;
	  }
	  
}
		  