package parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.AlternativeHypothesis;
import org.apache.commons.math3.stat.inference.BinomialTest;

import genome.Gene;
import sample.ExpSample;

public class ParseExpressions {
	
	public static List<String> parseExpressions(InputStream expressions, Gene g, File outdir) throws IOException{
		System.out.println("Reading expressions");
		BufferedReader br = new BufferedReader(new InputStreamReader(expressions));
		String line = br.readLine(); //skip header
		
		Map<String, Integer> reads = new HashMap<String,Integer>(); //Map GTEx id of individual to total number of reads for individual
		Map<String, Integer> ref = new HashMap<String,Integer>(); //Map GTEx id of individual to number of reference reads for individual
		List<String> ret = new ArrayList<String>();
		try {
			//iterate over lines in file (SNPs used to call ASE)
			while((line = br.readLine()) != null){
				try{
					String[] tokens = line.split("\t");
					String gtexid = tokens[0].trim();
					
					int h1 = Integer.parseInt(tokens[1]);
					int h2 = Integer.parseInt(tokens[2]);
					int totalReads = h1 + h2;
				
					ret.add(gtexid);	
					if(!reads.containsKey(gtexid)){
						reads.put(gtexid, 0);
						ref.put(gtexid, 0);
					}

					int refKey = ref.get(gtexid) + h1;
					ref.put(gtexid, refKey);	
					
					int readsKey = reads.get(gtexid) + totalReads;
					reads.put(gtexid, readsKey);	
					

				} catch (Exception e){
					e.printStackTrace();
					System.exit(1);
				}
			}

			br.close();

			if(reads.size()!=ref.size()){
				System.out.println("Number of samples with total reads does not match number of samples with reference reads");
				System.exit(1);
			}


			//determine whether each individual has ASE or not
			//BufferedWriter outfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outdir+File.separator+g.getId()+"_ase.txt")));		
			
			int totalASE=0;
			for(String sample: reads.keySet()){
				ExpSample expsamp;
				double allelicRatio = 1.0*ref.get(sample) / reads.get(sample);
				int hasASE;
				
				if(allelicRatio>0.65){
					expsamp = new ExpSample(sample, 1);
					hasASE=1;
					totalASE++;
				}
				else if(allelicRatio<0.35){
					expsamp = new ExpSample(sample, 1);
					hasASE=1;
					totalASE++;
				}
				else{
					expsamp = new ExpSample(sample, 0);
					hasASE=0;
				}

				g.addSample(expsamp);
				
				//outfile.write(sample+"\t"+allelicRatio+"\t"+hasASE+"\n");
		
			}
			//outfile.write(g.getId()+"\t"+totalASE+"\t"+reads.size()+"\t"+totalASE*1.0/reads.size());
			//outfile.close();
		
			
			
			//binomial test
			/*int numSamples = reads.keySet().size();
			double alpha = .05/numSamples;
			
			for(String sample: reads.keySet()){
				int refReads = ref.get(sample);
				int totalReads = reads.get(sample);
				
				if(totalReads>20){
					BinomialTest bt = new BinomialTest();
					boolean ase = bt.binomialTest(totalReads, refReads, .5, AlternativeHypothesis.TWO_SIDED, alpha);
					
					ExpSample expsamp;
					if(ase){
						expsamp = new ExpSample(sample, 1);
					}
					else{
						expsamp = new ExpSample(sample, 0);
					}
					
					g.addSample(expsamp);	
				}
				
				//outfile.write(sample+"\t"+allelicRatio+"\t"+hasASE+"\n");	
			}
			*/	

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return ret;
		
	}

	private static boolean isLeft(String string) {
		String[] tokens = string.split(";");
		String[] tokens2 = tokens[1].split("|");
		if(!isInteger(tokens2[0])){
			System.out.println("Error: Cannot parse genotype "+string);
			System.exit(1);
		}
		if(Integer.parseInt(tokens2[0])==1){
			return true;
		}
		return false;
	}
	
	public static boolean isInteger( String input ) {
	    try {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
}
