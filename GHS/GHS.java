import java.io.*;
import java.math.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit; 

public class GHS{

	public static void main(String[] args) throws IOException{
		
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		Simulator sim = new Simulator();
		String cmd;
		int id=0;
		while((cmd = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(cmd," ");
			Vector<String> tokens = new Vector<String>();
			while(st.hasMoreTokens())
				tokens.addElement(st.nextToken());
			sim.CreateNode(id,tokens);
			id++;
		}

		sim.Init();

		while(!sim.QueueStatus()){
		}
	}
}