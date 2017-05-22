import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class Node implements Runnable{
    /**
     * Each node has its own message queue. Create thread for each node
     */
    public static final int SLEEP = 0;
    public static final int FIND = 1;
    public static final int FOUND = 2;
    public static final int BASIC = 1;
    public static final int BRANCH = 2;
    public static final int REJECT = 3;

    public static final int CONNECT = 0;
    public static final int INITIATE = 1;
    public static final int TEST = 2;
    // public static final int REJECT = 3;
    public static final int ACCEPT = 4;
    public static final int REPORT = 5;
    public static final int CHANGEROOT = 6;
    
    public boolean finish = false;

    private HashMap<Integer,String> ty = new HashMap<Integer,String>();
    private HashMap<Integer,String> ts = new HashMap<Integer,String>();
    public int nodeId;// Unique node id for each node
    private int state;// 0-sleep, 1-find, 2-found
    public HashMap<Integer,Integer> nbrs = new HashMap<Integer,Integer>(); // key - nodeId, value - edge weight
    private HashMap<Integer,Integer> status = new HashMap<Integer,Integer>(); // 1 - basic, 2 - branch, 3 - reject
    private String name;
    private int level;
    public int parent; // id of parent node
    private int bestWt, bestNode, rec, testNode;

    private static Vector<Node> nodeList = new Vector<Node>();
    private int capacity = 100; 
    public MessageQueue<Message> msg= new MessageQueue<Message>(capacity);

	private	BufferedWriter bw = null;
	private	FileWriter fw = null;
        

    public Node(int id, Vector<String> tokens){
        nodeId = id;
        state = SLEEP;
        level = 0; 
        parent = -1;
        bestNode = -1;
        bestWt = Integer.MAX_VALUE;
        name = Integer.toString(id);

        int wt=0;
        for(int i=0; i<tokens.size(); i++){
        	wt = Integer.parseInt(tokens.elementAt(i));
        	if(wt>0){
        		nbrs.put(i,wt);
        		status.put(i,BASIC);
        		if(wt<bestWt){
        			bestWt = wt;
        			bestNode = i;
        		}
        	} 
        }
        nodeList.addElement(this);
        
		ty.put(0,"CONNECT");
		ty.put(1,"INITIATE");
		ty.put(2,"TEST");
		ty.put(3,"REJECT");
		ty.put(4,"ACCEPT");
		ty.put(5,"REPORT");
		ty.put(6,"CHANGEROOT");

		ts.put(0,"SLEEP");
		ts.put(1,"FIND");
		ts.put(2,"FOUND");

        log("Node Created "+id+"\n");
        // for (Map.Entry<Integer, Integer> entry : nbrs.entrySet()) {
        //     log(entry.getKey()+" "+entry.getValue()+",");
        // }
        // log("\n");


    }

    public void Initialize(){
        int p = nodeId;
        int q = bestNode;
    	Message m = new Message(1,CONNECT,p,level);
        if(q != -1){
        	status.put(q,BRANCH);
        	state = FOUND;
        	rec = 0;
        	sendMessage(m,q);
        }
    }    

    /**
     *Each thread will perform its function inside run
     */
    public void run() {
		while(true){
			// while(msg.empty()){
			// 	Thread.yield();
			// }
			Message m = getNextMessage();
			logmr(m,nodeId);
			switch(m.type){
				case CONNECT:
					Connect(m);
					break;
				case INITIATE:
					Initiate(m);
					break;
				case TEST:
					Test(m);
					break;
				case REJECT:
                    Reject(m);
					break;	
				case ACCEPT:
					Accept(m);
					break;
				case REPORT:
					Report(m);
					break;
				case CHANGEROOT:
                    Changeroot(m);
					break;	
				default:
					log("Unknown Message \n");
			}
		}
    }

    public void Connect(Message m){
        int p = nodeId;
        int q = m.sid;
    	// log("Connect Message Received from "+q+"\n");
        if (m.level < level){
            status.put(q,BRANCH);
            Message n = new Message(1,INITIATE,p,level,name,state);
            sendMessage(n,q);
        }
        else if(status.get(q)==BASIC){
        	sendMessage(m,p); //wait    
        }
        else{
            // log("EQ rule\n");
            Message n;
            if(p < q)
            	n = new Message(1,INITIATE,p,level+1,Integer.toString(p)+Integer.toString(q),FIND);
            else
            	n = new Message(1,INITIATE,p,level+1,Integer.toString(q)+Integer.toString(p),FIND);    

            sendMessage(n,q);
        }
    }

    public void Initiate(Message m){
        int p = nodeId;
        int q = m.sid;
        level = m.level;
        name = m.name;
        state = m.state;
        parent = q;

        bestNode = -1;
        bestWt = Integer.MAX_VALUE;
        testNode = -1;

        for (Map.Entry<Integer, Integer> entry : status.entrySet()) {
            int r = entry.getKey();
            if(entry.getValue()==BRANCH && r!=q){
                Message n = new Message(1,INITIATE,p,level,name,state);
                sendMessage(n,r);
            }
        }

        if(state==FIND){
            rec = 0;
            findMin();
        }    	
    }

    public void findMin(){
        int p = nodeId;
        int q = -1;
        int wt = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : status.entrySet()) {
            int r = entry.getKey();
            int w = nbrs.get(r);
            if(entry.getValue()==BASIC && w<wt){
                q = r;
                wt = w;
            }
        }
        if(q!=-1){
            testNode = q;
            Message n = new Message(1,TEST,p,level,name);
            sendMessage(n,testNode);
        }
        else{
            testNode = -1;
            report();
        }
    }

    public void Test(Message m){
        int p = nodeId;
        int q = m.sid;
        if(m.level > level){
        	sendMessage(m,p); //wait
        }
        else if(name.equals(m.name)){
            if(status.get(q)==BASIC){
                status.put(q,REJECT);
            }
            if(q!=testNode){
                Message n = new Message(1,REJECT,p);
                sendMessage(n,q);
            }
            else{
                findMin();
            }
        }
        else{
            Message n = new Message(1,ACCEPT,p);
            sendMessage(n,q);
        }
    }

    public void Accept(Message m){
        int p = nodeId;
        int q = m.sid;
        testNode = -1;
        int wpq = nbrs.get(q);
        if(wpq < bestWt){
            bestWt = wpq;
            bestNode = q;
        }
        report();
    }

    public void Reject(Message m){
        int p = nodeId;
        int q = m.sid;
        if(status.get(q)==BASIC){
            status.put(q,REJECT);
        }
        findMin();
    }


    public void report(){
        int p = nodeId;
        int i=0;
        for (Map.Entry<Integer, Integer> entry : status.entrySet()) {
            int q = entry.getKey();
            if(entry.getValue()==BRANCH && q!=parent){
                i++;
            }
        }
        if(rec==i && testNode==-1){
            state = FOUND;
            Message n = new Message(1,REPORT,p,bestWt);
            sendMessage(n,parent);
        }
    }

    public void Report(Message m){
        int p = nodeId;
        int q = m.sid;
        int w = m.bestWt;
        if(q!=parent){
            if(w < bestWt){
                bestWt = w;
                bestNode = q;
            }
            rec++;
            report();
        }
        else{
            if(state==FIND){
                sendMessage(m,p); //wait
            }
            else if(w > bestWt){
                changeRoot();
            }
            else if(w==bestWt && bestWt==Integer.MAX_VALUE){
                // stop
                log("####### NodeId"+p+" Parent:"+parent+"\n");
                finish = true;
            }
        }
    	
    }

    public void Changeroot(Message m){
        changeRoot();
    }

    public void changeRoot(){
        int p = nodeId;
        if(status.get(bestNode)==BRANCH){
            Message n = new Message(1,CHANGEROOT,p);
            sendMessage(n,bestNode);
        }
        else{
            status.put(bestNode,BRANCH);
            Message n = new Message(1,CONNECT,p,level);
            sendMessage(n,bestNode);
        }
    }


    public int getNodeListLength(){
        return nodeList.size();
    }
   
    public void addMessage(Message mssg) {
        msg.add(mssg);
    }
   
    /* Blocking Version*/
    private Message getNextMessage(){
        return msg.getMessage();
    }

    private void sendMessage(Message m, int id){
        logm(m,id);
    	for(int i=0;i<nodeList.size();i++){
            if(nodeList.elementAt(i).nodeId == id){
                nodeList.elementAt(i).addMessage(m);
                break;
            }
        }  
    }

	public void log(String content){
        try{
			fw = new FileWriter(Integer.toString(nodeId)+".log",true);
			bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
		}
		catch(Exception e){}
	}
	public void logm(Message m, int des){
		log("Sent:     Type:"+ty.get(m.type)+", SrcID:"+m.sid+", Des:"+des+", Level:"+m.level+", Name:"+m.name+", State:"+ts.get(m.state)+", BestWt:"+m.bestWt+"\n");
	}
	public void logmr(Message m, int des){
		log("Received: Type:"+ty.get(m.type)+", SrcID:"+m.sid+", Des:"+des+", Level:"+m.level+", Name:"+m.name+", State:"+ts.get(m.state)+", BestWt:"+m.bestWt+"\n");
	}

}

