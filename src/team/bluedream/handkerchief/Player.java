package team.bluedream.handkerchief;

public class Player {
	public int id;
	public String playerName;
	public String identity;
	public int backCount;
	public String color;
	public boolean throwing;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setThrowing(boolean throwing) {
		this.throwing = throwing;
	}

	public Player(String playerName){
		this.playerName=playerName;
		this.backCount=Gloable.timeOfLookBack;
		this.color=PlayerStatus.COLOR_GRAY;
		this.identity=PlayerStatus.IDENTITY_ORDINARY;
		this.throwing=false;
		this.id=0;
	}
	
	public void init(){
		this.backCount=3;
		this.color=PlayerStatus.COLOR_GRAY;
		this.identity=PlayerStatus.IDENTITY_ORDINARY;
		this.throwing=false;
		//this.id=0;
	}
	

	public void setPlayerName(String name){
		
		this.playerName=name;
	}
	public String getPlayerName(){
		
		return this.playerName;
	}
	
	public void setIdentity(String identity){
		
		this.identity=identity;
	}
	public String getIdentity(){
		return this.identity;
	}
	
	public void setBackCount(int count){
		this.backCount=count;
	}
	public int getBackCount(){
		return this.backCount;
	}
	
	public void setColor(String color){
		this.color=color;
	}
	public String getColor(){
		return this.color;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "playerName="+playerName+"\n playerIdentity="+identity+"\n backCount="+backCount+"\n color="+color;
	}
}