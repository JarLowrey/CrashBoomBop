package support;

import support.GameAttributes.Case;

public class CaseChecks {
	
	//Values experimentally determined utilizing  "Physics Toolbox Accelerometer"-Vieyra Software
	public static final double noise = 1.1,
			lightShake = 1.8,
			medShake = 4,
			heavyShake = 11,
			veryHeavyShake = 17;

	private double lastX, lastY, lastZ;
	private boolean accelInitialized=false;
    
    
	public enum CaseResult{
		PASS,FAIL,NONE
	}
	
	public CaseResult checkSensorForPassOrFail(double x, double y, double z,Case currentCase){       
		if(!accelInitialized){
        	lastX=x;
        	lastY=y;
        	lastZ=z;
        	accelInitialized=true;
        }
	
		double[] diffs={(x-lastX),(y-lastY),(z-lastZ)};
		
		CaseResult result = checkAllCasesExceptManyTapAndMovingBtn(currentCase, diffs);
			
		lastX = x;
	    lastY = y;
	    lastZ = z;
	    
	    return result;
	}

	public void resetSensor(){
		accelInitialized=false;
	}
	
	/**
	 * Switch tree to call other methods in this class that check appropriate passedCase
	 * @param passedCase-Case to be checked
	 * @param diffs-parameters to check passedCase 
	 * @return-PASS,FAIL, or NONE for these given Cases
	 */
	public CaseResult checkAllCasesExceptManyTapAndMovingBtn(GameModel.Case passedCase, double[] diffs){
		CaseResult result = CaseResult.NONE;
		switch(passedCase){
		case XSHAKE:
			result=checkXShake(diffs);
        	break;
        case YSHAKE:
        	result=checkYShake(diffs);
        	break;
        case ZSHAKE:
        	result=checkZShake(diffs);
        	break;
        case CAST:
        	result=checkCast(diffs);
        	break;
        case TAP:
            result=checkForSensorMovementWhenExpectingTap(diffs);
           	break;
		default:
			break;
        }
		return result;
	}
	
	public boolean checkManyShakes(GameModel.Case passedCase, double[] diffs){
		switch(passedCase){
			case MANYSHAKESX:
				return checkShakeAlongAxis(diffs[0],medShake);
	//		case MANYSHAKESY:
	//			return checkShakeAlongAxis(diffs[1],medShake);
	//		case MANYSHAKESZ:
	//			return checkShakeAlongAxis(diffs[2],medShake);
			default:
				return false;
			}
	}
	
	public CaseResult checkXShake(double[] diffs){
		CaseResult myResult=CaseResult.NONE;
		//x axis shake. 
		//fail:yDiff>cutoff2Fail
		//pass:xDiff|| zDiff>cutoff2Pass
		boolean xChk=checkShakeAlongAxis(diffs[0],medShake);
		boolean yChk=checkShakeAlongAxis(diffs[1],heavyShake);
		boolean zChk=checkShakeAlongAxis(diffs[2],heavyShake);
		
		if(yChk || zChk){
			myResult=CaseResult.FAIL;
    	}else if(xChk){
    		myResult=CaseResult.PASS;
    	}
		return myResult;
	}
	
	public CaseResult checkYShake(double[] diffs){
		CaseResult myResult=CaseResult.NONE;
		//y axis shake. 
		//fail:xDiff>cutoff2Fail
		//pass:yDiff || zDiff>cutoff2Pass
		boolean xChk=checkShakeAlongAxis(diffs[0],heavyShake);
		boolean yChk=checkShakeAlongAxis(diffs[1],medShake);
		boolean zChk=checkShakeAlongAxis(diffs[2],heavyShake);
		
		if(xChk || zChk){
			myResult=CaseResult.FAIL;
    	}else if(yChk){
    		myResult=CaseResult.PASS;
    	}
		return myResult;
	}
	
	public CaseResult checkZShake(double[] diffs){
		CaseResult myResult=CaseResult.NONE;
		//z axis shake
		//fail:yDiff or xDiff >cutoff2Fail
        //pass:zDiff>cutoff2Pass
		boolean xChk=checkShakeAlongAxis(diffs[0],heavyShake);
		boolean yChk=checkShakeAlongAxis(diffs[1],heavyShake);
		boolean zChk=checkShakeAlongAxis(diffs[2],medShake);
	
	
		if(yChk || xChk){
			myResult=CaseResult.FAIL;
		}else if(zChk){
			myResult=CaseResult.PASS;
		}
		return myResult;
	}
	/**
	 * Check to see if a Flick Case has occurred
	 * generally, Flick is defined as a particularly hard shake in any plane
	 * @param diffs- the 3 membered array of {x-xLast,y-yLast,z-zLast}
	 * @return-PASS if meets pass criteria NONE otherwise
	 */
	public CaseResult checkCast(double[] diffs){
		//fail: Heavy shake in z axis
        //pass: Light Shake in x and y axis
		boolean xChk=checkShakeAlongAxis(diffs[0],heavyShake);
    	boolean yChk=checkShakeAlongAxis(diffs[1],heavyShake);
    	boolean zChk=checkShakeAlongAxis(diffs[2],heavyShake);
    	    	
    	if (xChk || yChk || zChk){
    		return CaseResult.PASS;
    	}else{
    		return CaseResult.NONE;
    	}
	}
	
	/**
	 * Check to see if any shake event has occurred when Case.TAP is in focus.
	 * @param diffs- the 3 membered array of {x-xLast,y-yLast,z-zLast}
	 * @return-FAIL if meets any fail criteria, NONE otherwise
	 */
	public CaseResult checkForSensorMovementWhenExpectingTap(double[] diffs){
		//fail: any shake along an axis
        //pass: the sensor does not detect an Accelerometer Case has occurred
		 	boolean xChk=checkShakeAlongAxis(diffs[0],heavyShake-4);
        	boolean yChk=checkShakeAlongAxis(diffs[1],heavyShake-4);
        	boolean zChk=checkShakeAlongAxis(diffs[2],heavyShake-4);
        	
        	if(xChk || yChk || zChk){
        		return CaseResult.FAIL;
        	}
        	return CaseResult.NONE;
	}
	
	/////////////////SUPPORT METHODS//////////////////////
	/**
	 * check to see if a shake along x,y or x axis has occurred.
	 * See if change in coordinates is > cutoff, then update game State
	 * @param diff-passed in correctly, it is the current coordinate-previous coordingate
	 * @param cutoff-value that is checked against coordinate change
	 * @return-true if a 1D shake has occurred
	 */
	public boolean checkShakeAlongAxis(double diff,double cutoff){
		return Math.abs(diff)>cutoff;
	}
	/*
	private void lessenShakeRequirements(){
		lightShake = (double) 1.4;
	    medShake = (double) 3;
	    heavyShake = (double)9;
	    veryHeavyShake=(double)16;
	}
	
	private void resetShakeRequirements(){
		lightShake = (double) 1.8;
	    medShake = (double) 4;
	    heavyShake = (double)11;
	    veryHeavyShake=(double)17;
	}
	
	private void increaseShakeRequirements(){
		lightShake = (double) 2.3;
	    medShake = (double) 5;
	    heavyShake = (double)12;
	    veryHeavyShake=(double)18;
	}
	*/

}
