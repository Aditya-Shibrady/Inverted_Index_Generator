/*
 * File : Main.java
 * Code written by : Aditya Shibrady
 * 
 *  Program to tokenize the Cranfield Collection and calculate various statistics on the same.
 * 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.terrier.compression.BitOutputStream;


public class Main {
	
	static int tokens =0, avg_tokens = 0, num_unique_token = 0, num_once_token = 0, stems = 0, num_unique_stem = 0, num_once_stem = 0;
	
	//static String filepath = "D:/Fall 2014/Information Retrieval/Homeworks/Cranfield";
	static String filepath = null;
	static String path = null;
	static String stop;
	static ArrayList<String> stopwords = new ArrayList<String>();
	static Stemmer stem = new Stemmer();
	static StanfordLemmatizer slem = new StanfordLemmatizer();
	
	 static HashMap<Integer,Integer> hashmap[][] = new HashMap[100000][1400];
	 static HashMap<String,Integer> hashmap1[] = new HashMap[140000];
	 
	 static ArrayList<String> hs = new ArrayList<String>();
	 
	 static long uncompmem = 0;
	 static long compmem = 0;
	 static long starttime;
	 
	 static boolean flag=false;
	 static int ind=0;
	 
	 static HashMap<Integer,Integer> hashmap_l[][] = new HashMap[100000][1400];
	 static HashMap<String,Integer> hashmap1_l[] = new HashMap[140000];
	 
	 static ArrayList<String> hs_l = new ArrayList<String>();
	 
	 static long uncompmem_l = 0;
	 static long compmem_l = 0;
	 
	 static boolean flag_l=false;
	 static int ind_l=0;
	
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub		 
		//System.out.println(args[0]);
		filepath = "Cranfield/";
		final File folder = new File("Cranfield/");
		listFilesForFolder(folder);
		display();
		
	/*	calculate_token_statistics();
		display_token_statistics();
		calculate_stem_statistics();
		System.out.println ( " ****************************************************** ");
		display_stem_statistics();
	*/

		
		
		
	}
	
	public static void listFilesForFolder(final File folder) throws Exception {
		
		FileInputStream fstream1 = new FileInputStream("stopwords");
		DataInputStream inf1 = new DataInputStream(fstream1);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(inf1));
		
		starttime = System.currentTimeMillis();
		
		while ((stop = br1.readLine()) != null) 	
		{
	         StringTokenizer stp = new StringTokenizer(stop," ");
	                                
	               while (stp.hasMoreTokens())
	                   {
	                      String s = stp.nextToken();
	                 //     System.out.println("The stop word is"+s);
	                      stopwords.add(s);
	                   }
	     }
		
		 FileOutputStream fos = new FileOutputStream("compressedversion2.dat");
		 BitOutputStream bos = new BitOutputStream(fos);
		 FileOutputStream fos1 = new FileOutputStream("uncompressedversion2.dat");
		 BitOutputStream bos1 = new BitOutputStream(fos1);
		 FileOutputStream fos2 = new FileOutputStream("compressedversion1.dat");
		 BitOutputStream bos2 = new BitOutputStream(fos2);
		 FileOutputStream fos3 = new FileOutputStream("uncompressedversion1.dat");
		 BitOutputStream bos3 = new BitOutputStream(fos3);
		 
		 Tokenizer t = new Tokenizer();
		
		for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
            listFilesForFolder(fileEntry);
        } else {
            //System.out.println(fileEntry.getName());
            
            path = filepath + "/" + fileEntry.getName();
            
            Parse p1 = new Parse();
     		Cranfield c = p1.perform_parse(path);
     		
     		t.tokenize(c,bos,bos1,bos2,bos3);
     		
            //System.out.println(path);
                 
            
        }
    }
}
	
	static void display()
	{
		System.out.println("Total number of inverted lists = " + hs.size());
		 long endtime = System.currentTimeMillis();
		 System.out.println("Time to build compressed index: "+(endtime-starttime)/(1000)+" seconds.");
		System.out.println("Memory required for uncompressed index " + uncompmem/8 +" Bytes.");
		System.out.println("Memory required for compressed index " + compmem/8 +" Bytes.");
		System.out.println("To find df,tf and inverted list length(in bytes)for:");
		String[] arrterms = {"reynold","nasa","prandtl","flow","pressure","boundary","shock"};
		for(int i=0;i<arrterms.length;i++)
				{
					if(hs.contains(arrterms[i]))
					{
		System.out.println("\n");                            
		int index1 = hs.indexOf(arrterms[i]);
		 System.out.println("The term is: "+arrterms[i]);
		 int DF = hashmap1[index1].get(arrterms[i]);
		 System.out.println("The Doc Frequency is :"+DF);
		System.out.println("Inverted List length :"+DF*8+" Bytes.");
		 // TODO code application logic here
		 System.out.println("The Doc Id and Term Frequency is:");   
		  
		 
		 
		  for(int x=0;x<DF;x++)
		  {
		  TreeMap<Integer,Integer> result = new TreeMap<Integer,Integer>(hashmap[index1][x]);
						Iterator<Integer> iterator = result.keySet().iterator();
		                                
						while(iterator.hasNext())
						{
							Integer key =  iterator.next();
							Integer value =  (Integer) result.get(key);
							System.out.println(key+"\t\t"+value);
						}      
		        
		        } 
		        
		  }     
		                }
		
		System.out.println("************ After Lemmatization ********************");
		System.out.println("Total number of inverted lists = " + hs_l.size());
		 System.out.println("Time to build compressed index: "+(endtime-starttime)/(1000)+" seconds.");
		System.out.println("Memory required for uncompressed index " + uncompmem_l/8 +" Bytes.");
		System.out.println("Memory required for compressed index " + compmem_l/8 +" Bytes.");
		System.out.println("To find df,tf and inverted list length(in bytes)for:");
		String[] arrterms_l = {"reynold","nasa","prandtl","flow","pressure","boundary","shock"};
		for(int i=0;i<arrterms_l.length;i++)
				{
					if(hs_l.contains(arrterms_l[i]))
					{
		System.out.println("\n");                            
		int index1 = hs_l.indexOf(arrterms_l[i]);
		 System.out.println("The term is: "+arrterms_l[i]);
		 int DF = hashmap1_l[index1].get(arrterms_l[i]);
		 System.out.println("The Doc Frequency is :"+DF);
		System.out.println("Inverted List length :"+DF*8+" Bytes.");
		 // TODO code application logic here
		 System.out.println("The Doc Id and Term Frequency is:");   
		  
		 
		 
		  for(int x=0;x<DF;x++)
		  {
		  TreeMap<Integer,Integer> result = new TreeMap<Integer,Integer>(hashmap_l[index1][x]);
						Iterator<Integer> iterator = result.keySet().iterator();
		                                
						while(iterator.hasNext())
						{
							Integer key =  iterator.next();
							Integer value =  (Integer) result.get(key);
							System.out.println(key+"\t\t"+value);
						}      
		        
		        } 
		        
		  }     
		                }
		
	}
	

}
