package parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import genome.ChromState;
import genome.GenomicCoordinate;
import genome.GenomicRegion;

public class ParseChromState {
	
	public static List<ChromState> parseChromState(InputStream in){
		List<ChromState> ret = new ArrayList<ChromState>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try{
			while((line = reader.readLine()) != null){
				String[] tokens = line.split("\t");
				String tokens2 = tokens[0].substring(3, tokens[0].length());
				if(tokens2.contains("M") || tokens2.contains("X") || tokens2.contains("Y")){
					continue;
				}
				else{
					int chromosome = Integer.parseInt(tokens[0].substring(3, tokens[0].length()));
					GenomicCoordinate start = new GenomicCoordinate(chromosome, Integer.parseInt(tokens[1]));
					GenomicCoordinate end = new GenomicCoordinate(chromosome, Integer.parseInt(tokens[2]));
					
					ret.add(new ChromState(tokens[3].trim(), new GenomicRegion(start, end)));	
				}
			}
			
		} catch (IOException e) {
			System.out.println("no lines");
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static List<String> parseStateID(InputStream in){
		List<String> ret = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try{
			while((line = reader.readLine()) != null){
				ret.add(line.trim());
			}
			
		} catch (IOException e) {
			System.out.println("no lines");
			e.printStackTrace();
		}
		
		return ret;
	}
	
}
