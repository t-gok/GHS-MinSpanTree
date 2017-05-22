import java.util.*;

public class Message{
	public int mid; // message id 
	public int type; // 0-connect, 1-initiate, 2-test, 3-reject, 4-accept, 5-report, 6-changeroot
	public int sid; // source id
	public int level;
	public String name;
	public int state; // 0-sleep, 1-find, 2-found
	public int bestWt; // only valid in case of report

	public void printm(){
		System.out.print("("+mid+","+type+","+sid+","+level+","+name+","+state+","+bestWt+") ");	
	}

    public Message(int mid, int type, int sid){
        this.mid = mid;
        this.type = type;
        this.sid = sid;
	}

    public Message(int mid, int type, int sid, int lb){
        this.mid = mid;
        this.type = type;
        this.sid = sid;
        if(type==5){
        	this.bestWt = lb;
        }
        else{
        	this.level = lb;	
        }
	}

    public Message(int mid, int type, int sid, int level, String name){
        this.mid = mid;
        this.type = type;
        this.sid = sid;
        this.level = level;
        this.name = name;
	}


    public Message(int mid, int type, int sid, int level, String name, int state){
        this.mid = mid;
        this.type = type;
        this.sid = sid;
        this.level = level;
        this.name = name;
        this.state = state;
	}
}