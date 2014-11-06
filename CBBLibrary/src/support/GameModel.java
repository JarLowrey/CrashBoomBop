package support;

import java.util.ArrayList;

import com.jtronlabs.cbblibrary.R;

public class GameModel{	
	private final int scoreIncreaseFactorForResponseTime=5000;
	private final int MAX_SCORE_INC=35;
	
	public Case currentCase;
	public Case[] allCases = Case.values();
	public int currentPosInCombo=0;
	public int clearScreenTimeLength=1000;
	public boolean isCombo=false;
	
	public int score=0,streak=0,life=0, level=1;
	public int numBonusCases=0,numCasesFailed=0,numBonusShakesOrTaps=0,totalNumBonusPoints=0,
			numCombosOccurred=0,numCombosPassed=0,numCasesPassed=0,highestStreakThisGame;
	
	public Case[] casesInCombo = new Case[3];
	
	/*The different game cases*/
	//NOTE::::::MANYSHAKES,MANYTAPS,MOVINGBTN are considered BONUS Cases
	public enum Case{
		TAP,XSHAKE,YSHAKE,ZSHAKE,CAST,MANYSHAKESX,MANYTAPS,MOVINGBTN
	}
	public ArrayList<Case> bonusCases = new ArrayList<Case>();

	public GameModel(){
		bonusCases.add(Case.MANYSHAKESX);
		bonusCases.add(Case.MANYTAPS);
		bonusCases.add(Case.MOVINGBTN);
		
		life=3;
		
		setNextCase();
	}
	
	public int numTotalNonBonusCases(){
		return (numCasesPassed+numCasesFailed+numBonusCases);
	}
	
	/**
	 * Set the next case to be played in game. 
	 */
	public void setNextCase(){
//		currentCase=bonusCases.get((int)(Math.random()*bonusCases.size()));		//Case debugging
//		currentCase = Case.XSHAKE;
		/*set combo if desired*/
		if(!isCombo && numTotalNonBonusCases()>50 && (numTotalNonBonusCases()%25==0 || (streak!=0 && streak%20==0))){
			numCombosOccurred++;
			resetCombo();
			currentCase=casesInCombo[0];
			currentPosInCombo++;
			isCombo=true;
		}else if(isCombo){
			//Combo passed!
			if(casesInCombo.length==currentPosInCombo){
				score+=(int)(streak*1.5);
				resetCombo();
				currentCase=getRandomNonBonusCase();
				isCombo=false;
				numCombosPassed++;
			}else{
				//increment through the combo
				currentCase=casesInCombo[currentPosInCombo];
				currentPosInCombo++;
			}
		}
		else {
			//set bonus if desired
			if(numTotalNonBonusCases()>20 && (numTotalNonBonusCases()%15==0 || (streak!=0 && streak%10==0))){
				currentCase=getRandomBonusCase();
			}
			//set regular
			else{
				currentCase=getRandomNonBonusCase();
			}
		}
		totalNumBonusPoints+=numBonusShakesOrTaps;
		numBonusShakesOrTaps=0;
	}
	
	public boolean currentCaseIsBonus(){
		return bonusCases.contains(currentCase);
	}
	
	public Case getRandomNonBonusCase(){
		int index = (int)(Math.random()*allCases.length);
		while(bonusCases.contains(allCases[index])){
			index = (int)(Math.random()*allCases.length);
		}
		return allCases[index];
	}
	
	public Case getRandomBonusCase(){
		int index = (int)(Math.random()*bonusCases.size());
		return bonusCases.get(index);
	}
	
	/**
	 * check to see if level should be incremented. If so, increment it
	 */
	public boolean nextLevelIfNeccessary(){
		double check = (level/10)*100+100*level;//Initially, every 100 score level increases. Every 10 levels, score required becomes 100 bigger
		int lvlTemp=level;
		
		if(score>check){
			level++;
		}
		//as level progresses lower the time game spends on a clear screen
		if(level>=5){clearScreenTimeLength=500;}
		else{clearScreenTimeLength=1000-level*100;}
		
		return level>lvlTemp;
	}
	
	public void stopCombo(){
		isCombo=false;
	}	
	
	//COMBO methods
	public void resetCombo(){
		currentPosInCombo=0;
		for(int i=0;i<3;i++){
        	casesInCombo[i]=getRandomNonBonusCase();
        }
		
	}
	
	/*Support Methods*/
	/**
	 * Based on current level, determine what time yields GameOver status
	 * @return
	 * cutoff time (sec)
	 */
	public int getNonBonusFailTime(){
		int failTime=0;
		//time with formula
		if(level>=10){
			if(level<15){failTime=1100;}
			else if(level<20){failTime= 900;}
			else{failTime=875;}
		}else{
			/* formula that insures linear curve with points at (1,3000) and (10,1300
			 * So, at level 1 there is 5 secs allowed, at level 10, 1.3 sec allowed
			 */
			failTime= (int)(-(1700.0/9.0)*level+28700.0/9.0);
		}
		//optimize
//		switch(level){
//		case 1:
//			break;
//		case 2:
//			break;
//		case 3:
//			break;
//		case 4:
//			break;
//		case 5:
//			break;
//		case 6:
//			break;
//		case 7:
//			break;
//		case 8:
//			break;
//		case 9:
//			break;
//		case 10:
//			break;
//		default:
//			break;
//		}
		if(isCombo && currentPosInCombo==1){//give extra time @ beginning of combo
			failTime=(int)(failTime*1.2);
		}
		return failTime;
	}
	
	public int getBonusEndTime(){
		switch(currentCase){
		case MANYSHAKESX:
			return 6000;
		case MANYTAPS:
			return 6000;
		case MOVINGBTN:
			return 10000;
		default:
			return 0;
		}
	}
	
	public String getTextForCase(Case myCase){
		String str="";
		
		switch(myCase){
			case TAP:
				str="Tap";
				break;
			case XSHAKE:
				str="Side to Side";
				break;
			case YSHAKE:
				str="Up and Down";
				break;
			case ZSHAKE:
				str="In and Out";
				break;
			case CAST:
				str="Cast";
				break;
			case MANYSHAKESX:
				str="BONUS\nShake! Shake! Shake!";
				break;
			case MANYTAPS:
				str="BONUS\nTap! Tap! Tap!";
				break;
			case MOVINGBTN:
				str="BONUS\nTap the buttons!";
				break;
//			case SWIPE:
//				str="SWIPE";
//				break;
			default:
				str="this should never happen";
				break;
		}
		return str;
	}
	
	public String getTextForCase(){
		return getTextForCase(currentCase);
	}
	
	public int getAnimationId(){
		return getAnimationId(currentCase);
	}
	
	public int getAnimationId(Case desiredAnimationCase){
		int animationId;
		switch(desiredAnimationCase){
		case CAST:
			animationId=R.anim.cast;
			break;
		case MANYSHAKESX:
			animationId=R.anim.x_shake;
			break;
//		case MANYTAPS:
//			animationId=R.anim.tap;
//			break;
		case MOVINGBTN:
			animationId=R.anim.tap;
			break;
//		case TAP:
//			animationId=R.anim.tap;
//			break;
		case XSHAKE:
			animationId=R.anim.x_shake;
			break;
		case YSHAKE:
			animationId=R.anim.y_shake;
			break;
		case ZSHAKE:
			animationId=R.anim.z_shake;
			break;
//		case SWIPE:
//			animationId=R.anim.swipe;
//			break;
		default:
			animationId=R.anim.none;
			break;
		}
		return animationId;
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
	
	public boolean passStreakMileStone(){
		boolean passedStreakMilestone=false;
		if(streak>0){
			if(streak<=10){
				passedStreakMilestone = (streak%5==0);
			}else if(streak<=30){
				passedStreakMilestone = (streak%10==0);
			}else{
				passedStreakMilestone = (streak%20==0);
			}
		}		
		return passedStreakMilestone;
	}
	
}
