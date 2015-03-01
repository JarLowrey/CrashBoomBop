package support;

import com.jtronlabs.cbblibrary.R;

public class GameAttributes {

	private static final int scoreIncreaseFactorForResponseTime=5000;
	private static final int MAX_SCORE_INC=35;
	
	private int score=0;
	
	/**
	 * The different game cases
	*/
	public enum Case{
		TAP(R.anim.none,"Tap"),//R.anim.tap????????????
		XSHAKE(R.anim.x_shake,"Side to Side"),
		YSHAKE(R.anim.y_shake,"Up and Down"),
		ZSHAKE(R.anim.z_shake,"In and Out"),
		CAST(R.anim.cast,"Cast"),
		//Bonus cases
		MANYSHAKESX(R.anim.x_shake,"BONUS\nShake! Shake! Shake!"),
		MANYTAPS(R.anim.none,"BONUS\nTap! Tap! Tap!"),
		MOVINGBTN(R.anim.tap,"BONUS\nTap the buttons!");

	    private final int animId;
		private final String stringRepresentation;
		
		private Case(int animationId,String stringRep) { 
			animId = animationId;
			stringRepresentation = stringRep;
		}
		
	    public int getAnimationId() { return animId; }
	    @Override
	    public String toString() { return stringRepresentation; }
	}
	

	public int increaseScore(long responseTime){
		//if the passed value is a long, then it is a time. if score is >35, throttle it to 35
		int tmp=(int)(scoreIncreaseFactorForResponseTime/responseTime);
		score += (tmp>MAX_SCORE_INC) ? (MAX_SCORE_INC):(tmp);
		return tmp;
	}
	
	public int increaseScore(int amountToIncreaseScore){
		//if the passed value is an int then directly increase score by that int
		amountToIncreaseScore=(amountToIncreaseScore>MAX_SCORE_INC) ? (MAX_SCORE_INC):(amountToIncreaseScore);
		score+=amountToIncreaseScore;
		return amountToIncreaseScore;
	}
	
	//GET METHODS
	public int getScore(){ return score; }
}
