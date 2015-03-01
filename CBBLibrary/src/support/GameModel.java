package support;

import java.util.ArrayList;

public class GameModel extends GameAttributes{
	
	private Case currentCase;
	public Case[] allCases = Case.values();
	public int currentPosInCombo=0;
	public int clearScreenTimeLength=1000;
	public boolean isCombo=false;
	
	public int streak=0,life=0, level=1;
	public int numBonusCases=0,numCasesFailed=0,numBonusShakesOrTaps=0,totalNumBonusPoints=0,
			numCombosOccurred=0,numCombosPassed=0,numCasesPassed=0,highestStreakThisGame;
	
	public Case[] casesInCombo = new Case[3];
	
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
				increaseScore( (int)(streak*1.5) );
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
		
		if(getScore()>check){
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
	
	public Case getCurrentCase(){ return currentCase; }
	
}
