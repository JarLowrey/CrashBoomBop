package charts;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class PieChartViewBuilder{
	/** Colors to be used for the pie slices. */
	  private int[] COLORS = new int[] { Color.parseColor("#00CC00"), Color.parseColor("#CC0000"),Color.WHITE,Color.LTGRAY };
	  private CategorySeries mSeries;
	  private DefaultRenderer mRenderer;
	  private Context myContext;
	  float screenDens;
	  
	  public PieChartViewBuilder(Context ctx,float screenDensity){
		myContext=ctx;
		screenDens=screenDensity;
	  }
	  
	  public GraphicalView getPieChart(String title,int numPassed,int numFailed){
		  mSeries = new CategorySeries("");
		  mRenderer = new DefaultRenderer();
		  stylizeChart();

		  mRenderer.setChartTitle(title);
	      if(numPassed==0 & numFailed==0){
	    	  mSeries.add("None",1);
		      SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		      renderer.setColor(COLORS[3]);
		      mRenderer.addSeriesRenderer(renderer);
		      mRenderer.setDisplayValues(false);
		      mRenderer.setStartAngle(90);
	      }else{
	    	  if(numPassed>0){
			      mSeries.add("Pass",numPassed);
			      SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			      renderer.setColor(COLORS[0]);
			      mRenderer.addSeriesRenderer(renderer);
		      }
	    	  if(numFailed>0){
			      mSeries.add("Fail ", numFailed);
			      SimpleSeriesRenderer renderer2 = new SimpleSeriesRenderer();
			      renderer2.setColor(COLORS[1]);
			      mRenderer.addSeriesRenderer(renderer2);
	    	  }
	      }
		  return ChartFactory.getPieChartView(myContext, mSeries, mRenderer);

	  }
	  
	  private void stylizeChart(){
		  	//stylize chart
//	        int[] margins = {(int)(50*screenDens),(int)(50*screenDens),(int)(50*screenDens),(int)(50*screenDens)};
//		  	mRenderer.setMargins(margins);
		    mRenderer.setZoomButtonsVisible(false);
		    mRenderer.setStartAngle(180);
		    mRenderer.setDisplayValues(true);
		    mRenderer.setClickEnabled(false);
		           
	        mRenderer.setChartTitleTextSize(22*screenDens);
	        mRenderer.setLabelsTextSize(17*screenDens);
		    mRenderer.setLabelsColor(COLORS[2]);
		    mRenderer.setShowLegend(false);
		    
		    mRenderer.setPanEnabled(false);
		    mRenderer.setZoomEnabled(false);
		    mRenderer.setZoomRate(6.0f);
		    mRenderer.setInScroll(true);
	  }
}
		  