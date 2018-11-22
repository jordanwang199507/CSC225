/*
* CSC 225 Assignment 3 HashTable.java
* Jordan (Yu-Lin) Wang
* V00786970
*/

/* HashTable.java
   CSC 225 - Spring 2017
   Template for string hashing

   =================

   Modify the code below to use quadratic probing to resolve collisions.

   Your task is implement the hash, insert, find, remove, and resize methods for the hash table.

   The load factor should always remain in the range [0.25,0.75] and the tableSize should always be prime.

   =================

   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java HashTable

   Input data should consist of a list of strings to insert into the hash table, one per line,
   followed by the token "###" on a line by itself, followed by a list of strings to search for,
   one per line, followed by the token "###" on a line by itself, followed by a list of strings to remove,
   one per line.

   To conveniently test the algorithm with a large input, create
   a text file containing the input data and run the program with
	java HashTable file.txt
   where file.txt is replaced by the name of the text file.

   B. Bird - 07/04/2015
   M. Simpson - 21/02/2016
*/

import java.util.Scanner;
import java.util.Vector;
import java.util.Arrays;
import java.io.File;
import java.lang.Math;

public class HashTable{
  
     int TableSize;     
     int NumElements;

	// declare data structure here
     String[] T;
     boolean[] delete;

	public HashTable(){
	   	NumElements = 0;
	   	TableSize = 997;
	   	
	    T = new String[TableSize];
	    delete = new boolean[TableSize];

	    for (int i = 0; i < TableSize; i++){
	      T[i] = null;
    	}
	}

	/* hash(s) = ((3^0)*s[0] + (3^1)*s[1] + (3^2)*s[2] + ... + (3^(k-1))*s[k-1]) mod TableSize
	   (where s is a string, k is the length of s and s[i] is the i^th character of s)
	   Return the hash code for the provided string.
	   The returned value is in the range [0,TableSize-1].

	   NOTE: do NOT use Java's built in pow function to compute powers of 3. To avoid integer
	   overflows, write a method that iteratively computes powers and uses modular arithmetic
	   at each step.
	*/

  	//power
  	public int Pow(int x, int y){
    	if (y == 0){
      		return 1;
    	}
    	if (y == 1){
      		return x;
    	}
    	int answer = x;
    	for (int i = 2; i <= y; i++){
      		answer = (answer * x) % TableSize;
    	}
    	return answer % TableSize;
  	}	 
  	  
	public int hash(String s){
	    int index = 0;
	    for (int i = 0; i < s.length(); i++){
	      index += (int)s.charAt(i) * (Pow(3,i)) % TableSize;
	    }
		return index % TableSize;
	}

	/* insert(s)
	   Insert the value s into the hash table and return the index at
	   which it was stored.
	*/
	public int insert(String s){
    	//get hash value with hash function
    	int hashvalue = hash(s);

    	//check if load factor is greater than 0.75
    	float load = (float)NumElements/(float)TableSize;

    	//resize table if greater than 0.75
    	if (load > 0.75){
      		int newSize = TableSize * 2;
      		while (!PrimeNum(newSize)){
        		newSize++;
      		}
      		resize(newSize);
    	}

    	//if not null, use quadratic probing 
    	int i = 0;
    	while (T[hashvalue] != null){
      		hashvalue = (hashvalue + Pow(i, 2)) % TableSize;
      		hashvalue = hashvalue % TableSize;
      		i++;
    	}
    	//insert element at appropriate location
    	T[hashvalue] = s;
    	delete[hashvalue] = true;
    	NumElements++;
		return hashvalue;
	}

	/* find(s)
	   Search for the string s in the hash table. If s is found, return
	   the index at which it was found. If s is not found, return -1.
	*/
	public int find(String s){
      	//get hashvalue with hash function
      	int hashvalue = hash(s);

      	//iterate through TableSize times
      	int i = 0;

      	while (T[hashvalue] != null || (T[hashvalue] == null && delete[hashvalue] == true)){
        //if element is found, return hashvalue
        	if (T[hashvalue] != null){
          		if (T[hashvalue].equals(s)){
           			return hashvalue;
          		}
        	}
        //use quadratic probing to obtain next hashvalue
        	hashvalue = (hashvalue + Pow(i, 2)) % TableSize;
        	hashvalue = hashvalue % TableSize;
        	i++;
      	}
      	//item not found
      	return -1;
	}

  	//checks whether an int is prime or not
  	boolean PrimeNum(int x) {
    //check if n is a multiple of 2
    	if (x % 2 == 0) return false;
    	//if not, then just check the odds
    	for(int i = 3; i * i <= x; i += 2) {
        	if(x % i == 0)
            	return false;
    	}
    	return true;
	}

	/* remove(s)
	   Remove the value s from the hash table if it is present. If s was removed,
	   return the index at which it was removed from. If s was not removed, return -1.
	*/
	public int remove(String s){
      //check if load factor less than 0.25, resize if it is
      	float load = (float)NumElements/(float)TableSize;
      	if (load < 0.25){
        	int newSize = TableSize / 2;
        	while (!PrimeNum(newSize)){
          		newSize++;
        	}
        	resize(newSize);
     	}

     	int index = find(s);
      	//if item found, remove 
      	if (index != -1){
        	T[index] = null;
        	NumElements--;
        	return index;
      	//else return -1
      	} else {
        	return -1;
      	}
	}



	/* resize()
	   Resize the hash table to be a prime within the load factor requirements.
	*/
	public void resize(int newSize){
    //create temp hash table and initialize it
    	String[] temp = new String[newSize];
    	boolean[] temp1 = new boolean[newSize];
    	int hashvalue = 0;
    	for (int i = 0; i < newSize; i++){
      		temp[i] = null;
    	}
    	//rehash items from T into temp
    	int oldSize = TableSize;
    	TableSize = newSize;

    //go through each index of old table
    	for (int i = 0; i < oldSize; i++){
      		if (T[i] != null){
        		int k = 0;
        		hashvalue = hash(T[i]);
        		//if not null, use quadratic probing to find next empty index
        		while (temp[hashvalue] != null){
          			hashvalue = (hashvalue + Pow(k, 2)) % TableSize;
          			hashvalue = hashvalue % TableSize;
          			k++;
        		}
        		temp[hashvalue] = T[i];
        	temp1[hashvalue] = true;
      		}
    	}

    	T = temp;
    	delete = temp1;
	}

  /* **************************************************** */

	/* main()
	   Contains code to test the hash table methods.
	*/
	public static void main(String[] args){
		Scanner s;
		boolean interactiveMode = false;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			interactiveMode = true;
			s = new Scanner(System.in);
		}
		s.useDelimiter("\n");
		if (interactiveMode){
			System.out.printf("Enter a list of strings to store in the hash table, one per line.\n");
			System.out.printf("To end the list, enter '###'.\n");
		}else{
			System.out.printf("Reading table values from %s.\n",args[0]);
		}

		Vector<String> tableValues = new Vector<String>();
		Vector<String> searchValues = new Vector<String>();
		Vector<String> removeValues = new Vector<String>();

		String nextWord;

		while(s.hasNext() && !(nextWord = s.next().trim()).equals("###"))
			tableValues.add(nextWord);
		System.out.printf("Read %d strings.\n",tableValues.size());

		if (interactiveMode){
			System.out.printf("Enter a list of strings to search for in the hash table, one per line.\n");
			System.out.printf("To end the list, enter '###'.\n");
		}else{
			System.out.printf("Reading search values from %s.\n",args[0]);
		}

		while(s.hasNext() && !(nextWord = s.next().trim()).equals("###"))
			searchValues.add(nextWord);
		System.out.printf("Read %d strings.\n",searchValues.size());

		if (interactiveMode){
			System.out.printf("Enter a list of strings to remove from the hash table, one per line.\n");
			System.out.printf("To end the list, enter '###'.\n");
		}else{
			System.out.printf("Reading remove values from %s.\n",args[0]);
		}

		while(s.hasNext() && !(nextWord = s.next().trim()).equals("###"))
			removeValues.add(nextWord);
		System.out.printf("Read %d strings.\n",removeValues.size());

		HashTable H = new HashTable();
		long startTime, endTime;
		double totalTimeSeconds;

		startTime = System.currentTimeMillis();

		for(int i = 0; i < tableValues.size(); i++){
			String tableElement = tableValues.get(i);
			long index = H.insert(tableElement);
		}
		endTime = System.currentTimeMillis();
		totalTimeSeconds = (endTime-startTime)/1000.0;

		System.out.printf("Inserted %d elements.\n Total Time (seconds): %.2f\n",tableValues.size(),totalTimeSeconds);

		int foundCount = 0;
		int notFoundCount = 0;
		startTime = System.currentTimeMillis();

		for(int i = 0; i < searchValues.size(); i++){
			String searchElement = searchValues.get(i);
			long index = H.find(searchElement);
			if (index == -1)
				notFoundCount++;
			else
				foundCount++;
		}
		endTime = System.currentTimeMillis();
		totalTimeSeconds = (endTime-startTime)/1000.0;

		System.out.printf("Searched for %d items (%d found, %d not found).\n Total Time (seconds): %.2f\n",
							searchValues.size(),foundCount,notFoundCount,totalTimeSeconds);

		int removedCount = 0;
		int notRemovedCount = 0;
		startTime = System.currentTimeMillis();

		for(int i = 0; i < removeValues.size(); i++){
			String removeElement = removeValues.get(i);
			long index = H.remove(removeElement);
			if (index == -1)
				notRemovedCount++;
			else
				removedCount++;
		}
		endTime = System.currentTimeMillis();
		totalTimeSeconds = (endTime-startTime)/1000.0;

		System.out.printf("Tried to remove %d items (%d removed, %d not removed).\n Total Time (seconds): %.2f\n",
							removeValues.size(),removedCount,notRemovedCount,totalTimeSeconds);
	}
}
