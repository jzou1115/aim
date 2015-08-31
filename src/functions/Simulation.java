package functions;


import genome.Gene;
import genome.SNP;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import parse.ParseMap;
import parse.ParseSNP;

public class Simulation {
	Gene gene;
	List<SNP> snps;

	
	public void setTestGene(Gene g, List<SNP> s){
		gene = g;
		snps = s;
		System.out.println(gene.toString()+"\t"+snps.size());
	}
	
	public void setTestGene(InputStream map2, String gene2, InputStream genotypes) throws IOException{
		ParseMap parsemap = new ParseMap();
		parsemap.parseMap(map2, gene2);
		gene = parsemap.getGene();
		snps = parsemap.getSNPs();
		ParseSNP.parseGenotypes(genotypes,snps);
		System.out.println(gene.toString()+"\t"+snps.size());
	}
	public void startRun(double threshold, int errors, int perms, int n, File outdir) throws IOException{
		Run r = new Run(gene, snps, threshold, errors, perms, n, outdir);
		r.allSimulations();
	}
}
