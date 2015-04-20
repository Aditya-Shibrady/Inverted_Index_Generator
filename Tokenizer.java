import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.terrier.compression.BitOutputStream;

public class Tokenizer {
	
	static HashMap<String,Integer> token_map = new HashMap<String,Integer>();
	static HashMap<String,Integer> stem_map = new HashMap<String,Integer>();
	
	//int tokens =0, avg_tokens = 0, num_of_unique = 0, num_of_once = 0;
	
	
	public void tokenize(Cranfield c,BitOutputStream bos, BitOutputStream bos1,BitOutputStream bos2,BitOutputStream bos3) throws Exception {
		// TODO Auto-generated method stub
				
		StringTokenizer st1 = new StringTokenizer(c.docid,",;+-=&#:()!--?[]@$%^*  ");
		StringTokenizer st1_l = new StringTokenizer(c.docid,",;+-=&#:()!--?[]@$%^*  ");
		token_count(st1,bos,bos1);
		lemmatize(st1_l,bos2,bos3);
		
		StringTokenizer st2 = new StringTokenizer(c.title,",;+-=&#:()!--?[]@$%^*  ");
		StringTokenizer st2_l = new StringTokenizer(c.title,",;+-=&#:()!--?[]@$%^*  ");
		token_count(st2,bos,bos1);
		lemmatize(st2_l,bos2,bos3);
		
		StringTokenizer st3 = new StringTokenizer(c.author,",;+-=&#:()!--?[]@$%^*  ");
		StringTokenizer st3_l = new StringTokenizer(c.author,",;+-=&#:()!--?[]@$%^*  ");
		token_count(st3,bos,bos1);
		lemmatize(st3_l,bos2,bos3);
		
		StringTokenizer st4 = new StringTokenizer(c.biblio,",;+-=&#:()!--?[]@$%^*  ");
		StringTokenizer st4_l = new StringTokenizer(c.biblio,",;+-=&#:()!--?[]@$%^*  ");
		token_count(st4,bos,bos1);
		lemmatize(st4_l,bos2,bos3);
		
		StringTokenizer st5 = new StringTokenizer(c.text,",;+-=&#:()!--?[]@$%^*  ");
		StringTokenizer st5_l = new StringTokenizer(c.text,",;+-=&#:()!--?[]@$%^*  ");
		token_count(st5,bos,bos1);
		lemmatize(st5_l,bos2,bos3);
		
	}
	
	
	
	

	void token_count(StringTokenizer st1, BitOutputStream bos, BitOutputStream bos1) throws Exception
	{
		while (st1.hasMoreTokens())
		{
           
         String token1 = st1.nextToken().toLowerCase();
         //System.out.println(token1);
         String token2 = token1.replaceAll("[^A-Za-z]","");
         //System.out.println(token2);
         //String token2 = filter1(token1);
         //System.out.println("The token is"+token2);
         if(token2!=null && token2.length()>2  && Main.stopwords.contains(token2)!=true)
         {	
 			//System.out.println(token2);
        	Main.stem.add(token2.toCharArray(), token2.length());
 			Main.stem.stem();
 			String token = Main.stem.toString();
 			
 			//String token = Main.stem.stem(token2);
        	//System.out.println("The porter token is"+token);
        	
 			if(Main.hs.contains(token)!=true)
        	 {
 				Main.hs.add(token);
 				Main.uncompmem+=token.toCharArray().length*16;
 				Main.compmem+=token.toCharArray().length*16;
        		 int index = Main.hs.indexOf(token);
        		 Main.hashmap[index][0] = new HashMap();
        		 //Scanner sc = new Scanner(Main.path).useDelimiter("[^0-9]+");
        		 String sc = Main.path.replaceAll("[^0-9]","");
        		 //System.out.println(sc);
        		 Integer docID = Integer.parseInt(sc);
        		 docID = bos1.writeBinary(docID, docID.SIZE);
        		 //System.out.println(docID);
        		 //int docID = sc.nextInt();
        		 Integer n =1;
        		 n = bos1.writeBinary(n, n.SIZE);

        		 Main.hashmap[index][0].put(docID,1);
        		 Main.hashmap1[index] = new HashMap();
        		 Main.hashmap1[index].put(token,n);
        		 int encodeddocID = bos.writeDelta(docID); 
        		 int encodedtf = bos.writeGamma(n);
        		 Main.uncompmem+=((new Integer(docID)).toString().toCharArray().length* 16) + ((new Integer(1).toString().toCharArray().length * 16));
        		 Main.compmem += ((new Integer(encodeddocID).toString().toCharArray().length* 16) + (new Integer(encodedtf).toString().toCharArray().length * 16));        
        	 }
        	 else
        	 {
        		 int index = Main.hs.indexOf(token);
        		 
        		 //System.out.println(Main.path);
        		 //Scanner sc = new Scanner(Main.path).useDelimiter("[^0-9]+");
        		 String sc = Main.path.replaceAll("[^0-9]","");
        		 //System.out.println(sc);
        		 Integer docID = Integer.parseInt(sc);
        		 //System.out.println(docID);

        		 int doc_freq = Main.hashmap1[index].get(token);

        		 for(int m=0;m<doc_freq;m++)
        		 {  
        			 if(Main.hashmap[index][m].containsKey(docID))
        			 {
        				 Main.flag=true;
        				 Main.ind=m;

                    break;
        			 }

        		 }


        		 if(Main.flag==true)
        		 {
        			 Main.flag=false;
        			 Integer tf = Main.hashmap[index][Main.ind].get(docID)+1;
        			 Integer old_tf = tf-1;
        			 int encodedtf1 = bos.writeGamma(old_tf);
        			 int encodedtf = bos.writeGamma(tf);
        			 Main.hashmap[index][Main.ind].remove(docID);
        			 Main.hashmap[index][Main.ind].put(docID,tf);
        			 Main.uncompmem-=((new Integer(old_tf).toString().toCharArray().length * 16));
        			 Main.uncompmem+=((new Integer(tf).toString().toCharArray().length * 16));
        			 Main.compmem -= ((new Integer(encodedtf1).toString().toCharArray().length * 16));
        			 Main.compmem += ((new Integer(encodedtf).toString().toCharArray().length * 16));
        			 tf = bos1.writeBinary(tf, tf.SIZE);
        			 old_tf = bos1.writeBinary(old_tf, old_tf.SIZE);
        		 }
        		 else
        		 {
        			 Main.hashmap[index][doc_freq] = new HashMap();
        			 Main.hashmap[index][doc_freq].put(docID,1);
        			 int new_doc_freq = Main.hashmap1[index].get(token)+1;
        			 Main.hashmap1[index].remove(token);
        			 Main.hashmap1[index].put(token,new_doc_freq);
        			 int encodeddocID = bos.writeDelta(docID); 
        			 int encodedtf = bos.writeGamma(1);
        			 Main.uncompmem+=((new Integer(docID)).toString().toCharArray().length* 16) + ((new Integer(1).toString().toCharArray().length * 16));
        			 Main.compmem += ((new Integer(encodeddocID).toString().toCharArray().length* 16) + (new Integer(encodedtf).toString().toCharArray().length * 16));
        			 
            		 docID = bos1.writeBinary(docID, docID.SIZE);
            		 //System.out.println(docID);
            		 //int docID = sc.nextInt();
            		 Integer n =1;
            		 n = bos1.writeBinary(n, n.SIZE);
        		 }

        	 }
         }                            
		}
			

	}
	
	void lemmatize(StringTokenizer st1, BitOutputStream bos, BitOutputStream bos1) throws Exception
	{
		while (st1.hasMoreTokens())
		{
        //System.out.println(" ---- In Lemmatize ------"); 
		String token1 = st1.nextToken().toLowerCase();
        //System.out.println(token1);
         String token2 = token1.replaceAll("[^A-Za-z]","");
         //System.out.println(token2);
         //String token2 = filter1(token1);
         //System.out.println("The token is"+token2);
         if(token2!=null && token2.length()>2  && Main.stopwords.contains(token2)!=true)
         {	
 			//System.out.println(token2);
        	//Main.stem.add(token2.toCharArray(), token2.length());
 			//Main.stem.stem();
 			//String token = Main.stem.toString();
        	 String token = null;
        	 List<String> lemma = Main.slem.lemmatize(token2);
        	// System.out.println("--------------");
        	 
        	 for (String lem : lemma) {
        		// System.out.println(lem);
        		 token = lem;
        	 }
        	 //System.out.println(token);
        	 
 			//String token = Main.stem.stem(token2);
        	//System.out.println("The porter token is"+token);
        	
 			if(Main.hs_l.contains(token)!=true)
        	 {
 				Main.hs_l.add(token);
 				Main.uncompmem_l+=token.toCharArray().length*16;
 				Main.compmem_l+=token.toCharArray().length*16;
        		 int index = Main.hs_l.indexOf(token);
        		 Main.hashmap_l[index][0] = new HashMap();
        		 //Scanner sc = new Scanner(Main.path).useDelimiter("[^0-9]+");
        		 String sc = Main.path.replaceAll("[^0-9]","");
        		 //System.out.println(sc);
        		 Integer docID = Integer.parseInt(sc);
        		 docID = bos1.writeBinary(docID, docID.SIZE);
        		 //System.out.println(docID);
        		 //int docID = sc.nextInt();
        		 Integer n =1;
        		 n = bos1.writeBinary(n, n.SIZE);

        		 Main.hashmap_l[index][0].put(docID,1);
        		 Main.hashmap1_l[index] = new HashMap();
        		 Main.hashmap1_l[index].put(token,n);
        		 int encodeddocID = bos.writeDelta(docID); 
        		 int encodedtf = bos.writeGamma(n);
        		 Main.uncompmem_l+=((new Integer(docID)).toString().toCharArray().length* 16) + ((new Integer(1).toString().toCharArray().length * 16));
        		 Main.compmem_l += ((new Integer(encodeddocID).toString().toCharArray().length* 16) + (new Integer(encodedtf).toString().toCharArray().length * 16));        
        	 }
        	 else
        	 {
        		 int index = Main.hs_l.indexOf(token);
        		 
        		 //System.out.println(Main.path);
        		 //Scanner sc = new Scanner(Main.path).useDelimiter("[^0-9]+");
        		 String sc = Main.path.replaceAll("[^0-9]","");
        		 //System.out.println(sc);
        		 Integer docID = Integer.parseInt(sc);
        		 //System.out.println(docID);

        		 int doc_freq = Main.hashmap1_l[index].get(token);

        		 for(int m=0;m<doc_freq;m++)
        		 {  
        			 if(Main.hashmap_l[index][m].containsKey(docID))
        			 {
        				 Main.flag_l=true;
        				 Main.ind_l=m;

                    break;
        			 }

        		 }


        		 if(Main.flag_l==true)
        		 {
        			 Main.flag_l=false;
        			 Integer tf = Main.hashmap_l[index][Main.ind_l].get(docID)+1;
        			 Integer old_tf = tf-1;
        			 int encodedtf1 = bos.writeGamma(old_tf);
        			 int encodedtf = bos.writeGamma(tf);
        			 Main.hashmap_l[index][Main.ind_l].remove(docID);
        			 Main.hashmap_l[index][Main.ind_l].put(docID,tf);
        			 Main.uncompmem_l-=((new Integer(old_tf).toString().toCharArray().length * 16));
        			 Main.uncompmem_l+=((new Integer(tf).toString().toCharArray().length * 16));
        			 Main.compmem_l -= ((new Integer(encodedtf1).toString().toCharArray().length * 16));
        			 Main.compmem_l += ((new Integer(encodedtf).toString().toCharArray().length * 16));
        			 tf = bos1.writeBinary(tf, tf.SIZE);
        			 old_tf = bos1.writeBinary(old_tf, old_tf.SIZE);
        		 }
        		 else
        		 {
        			 Main.hashmap_l[index][doc_freq] = new HashMap();
        			 Main.hashmap_l[index][doc_freq].put(docID,1);
        			 int new_doc_freq = Main.hashmap1_l[index].get(token)+1;
        			 Main.hashmap1_l[index].remove(token);
        			 Main.hashmap1_l[index].put(token,new_doc_freq);
        			 int encodeddocID = bos.writeDelta(docID); 
        			 int encodedtf = bos.writeGamma(1);
        			 Main.uncompmem_l+=((new Integer(docID)).toString().toCharArray().length* 16) + ((new Integer(1).toString().toCharArray().length * 16));
        			 Main.compmem_l += ((new Integer(encodeddocID).toString().toCharArray().length* 16) + (new Integer(encodedtf).toString().toCharArray().length * 16));
        			 docID = bos1.writeBinary(docID, docID.SIZE);
            		 //System.out.println(docID);
            		 //int docID = sc.nextInt();
            		 Integer n =1;
            		 n = bos1.writeBinary(n, n.SIZE);
        		 }

        	 }
         }                            
		}
			

	}
	
}
