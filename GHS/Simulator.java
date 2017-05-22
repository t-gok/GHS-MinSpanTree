import java.io.*;
import java.math.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer; 
import java.util.concurrent.TimeUnit;

@SuppressWarnings("fallthrough")
public class Simulator{

	public Vector<Node> nodeList;
	public Vector<Thread> threadList;
	public Simulator()
	{
		nodeList = new Vector<Node>();
		threadList = new Vector<Thread>();
	}

	public boolean QueueStatus(){
		// boolean done = true;
		// int d = 0;
		// for(int i=0;i<nodeList.size();i++){
		// 	done = done && nodeList.elementAt(i).msg.empty();
		// 	if(nodeList.elementAt(i).msg.empty())
		// 		d++;
		// }
		// System.out.print(d+"/"+nodeList.size()+" "+done+"\n");	

		boolean finish = false;
		for(int i=0;i<nodeList.size();i++){
			finish = finish || nodeList.elementAt(i).finish;
		}
		//System.out.print("Finish:"+finish+"\n");
		if(finish){
			for(int i=0;i<nodeList.size();i++){
				System.out.println(nodeList.elementAt(i).nodeId + " "+ nodeList.elementAt(i).parent+" "+nodeList.elementAt(i).nbrs.get(nodeList.elementAt(i).parent));
				threadList.elementAt(i).stop();
			}			
		}


		return finish;

	}

	public void CreateNode(int id, Vector<String> tokens){
		Node n = new Node(id,tokens);
		nodeList.addElement(n);
		Thread t = new Thread(n,Integer.toString(id));
		t.start();
		threadList.addElement(t);
		//System.out.println("Created a new node: "+id);
	}

	public void Init(){
		for(int i=0; i<nodeList.size(); i++){
			//System.out.println("Initialize node: "+i);
			nodeList.elementAt(i).Initialize();
		}
	}


}



// try{
// 	TimeUnit.SECONDS.sleep(1);
// }
// catch(Exception e){}
