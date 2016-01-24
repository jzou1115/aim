import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import parse.ParseSNP;
import functions.*;
import genome.ChromState;
import genome.Gene;
import genome.SNP;
import parse.ParseChromState;
import parse.ParseMap;
import parse.ParseSNP;


public class ASE {
	
	private void assignChromatin(InputStream states, InputStream genesmap, InputStream variants, int p, String gene, File outdir, String filename) throws IOException{
		ParseMap parsemap = new ParseMap();
		parsemap.parseMap(genesmap, gene);
		Gene g = parsemap.getGene();
		List<SNP> s = parsemap.getSNPs();
		
		List<SNP> var = ParseSNP.readSNPGroup(variants);
		Map<String,SNP> snpmap = parsemap.getSnpMap();
		List<SNP> var2 = new ArrayList<SNP>();
		for(SNP v: var){
			if(snpmap.containsKey(v.getId())){
				var2.add(snpmap.get(v.getId()));
			}
		}
		
		List<ChromState> chrom = ParseChromState.parseChromState(states);
		
		PermuteChromatin perm = new PermuteChromatin(chrom, s, var2, g, outdir, filename);
		
		perm.testEnrichment(p);
		
	}

	
	private void createMap(InputStream snps, InputStream genes, InputStream chrom, File outdir, String filename) throws IOException {
		GenesToSNP map;
		if(chrom==null){
			map = new GenesToSNP(snps, genes);
		}
		else{
			map = new GenesToSNP(snps, genes, chrom);
		}
		
		if(filename!=null){
			map.write(outdir, filename);	
		} else{
			filename = "genestosnps.txt";
			map.write(outdir, filename);	
		}
	}
	

	private void startSimulation(InputStream map2, InputStream genotypes,
			String gene2, int error, int perm, int n, File outdir, String filename) throws IOException {
		Simulation sim = new Simulation();
		sim.setTestGene(map2, gene2, genotypes);
		sim.startRun(error, perm,n, outdir);
		
	}
	
	

	private void mapASE(InputStream map, InputStream genotypes,
			InputStream expressions, String gene, int error, int n, int perm, File outdir, String filename) throws IOException {
		MapASE ase = new MapASE();
		ase.setTestGene(map, gene, genotypes);
		ase.parseGenotypes(genotypes);
		ase.parseExpressions(expressions, outdir);
		ase.startRun(error,n, perm, outdir);
		
	}
	
	private void approxASE(InputStream map, InputStream genotypes,
			InputStream expressions, String gene, int error, int n, int perm, File outdir, String filename) throws IOException {
		MapASE ase = new MapASE();
		ase.setTestGene(map, gene, genotypes);
		ase.parseGenotypes(genotypes);
		ase.parseExpressions(expressions, outdir);
		ase.startRun(error,n, perm, outdir, gene+"_approxASE.txt");
		
	}
	
	private void generateCombinations(InputStream map, String gene,
			InputStream genotypes, InputStream expression, int n, int e, File out, String f) throws IOException {
		if(f!=null){
			Combinations combinations = new Combinations(map, gene, genotypes, expression, n, e, out, f);
		}
		else{
			f = gene+"_approxASE.txt.txt";
			Combinations combinations = new Combinations(map, gene, genotypes, expression, n, e, out, f);
		}
	}
	
	
	public static void main(String args[]) throws IOException{
		
		CommandLineParams cmdArgs = new CommandLine();
		try{
			cmdArgs.parse(args);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			cmdArgs.printHelp(System.err);
			System.exit(1);
		}
		if(cmdArgs.help()){
			cmdArgs.printHelp(System.err);
			System.exit(0);
		}
		
		ASE a= new ASE();
		String fcn = cmdArgs.getFunction();
		
		if(fcn.equals("genestosnps")){
			InputStream snps = cmdArgs.getSNPsInput();
			InputStream genes = cmdArgs.getGenesInput();
			InputStream chrom = cmdArgs.getChrom();
			File outdir = cmdArgs.getOutputDir();
			String filename = cmdArgs.getFilename();
			if(snps!=null && genes!=null){
				a.createMap(snps, genes, chrom, outdir, filename);	
			}	
			else{
				cmdArgs.printHelp(System.err);
				System.exit(0);
			}
		}
		
		else if(fcn.equals("simulation")){
			InputStream map = cmdArgs.getMap();
			InputStream genotypes = cmdArgs.getGenotypeData();
			String gene = cmdArgs.getTestGene();
			int error = cmdArgs.getErrorNum();
			int perm = cmdArgs.getPermNum();
			int n = cmdArgs.getSampleNum();
			File outdir = cmdArgs.getOutputDir();
			//InputStream samples = cmdArgs.getSamples();
			String filename = cmdArgs.getFilename();
			
			if(map!=null && genotypes!=null && gene!=null){
				a.startSimulation(map, genotypes, gene, error, perm, n, outdir, filename);

			} else{
				cmdArgs.printHelp(System.err);
				System.exit(0);
			}


		}
		
		else if(fcn.equals("mapase")){
			InputStream map = cmdArgs.getMap();
			InputStream genotypes = cmdArgs.getGenotypeData();
			InputStream expressions = cmdArgs.getExpressionData();
			String gene = cmdArgs.getTestGene();
			int error = cmdArgs.getErrorNum();
			int n = cmdArgs.getSampleNum();
			int perm = cmdArgs.getPermNum();
			File outdir = cmdArgs.getOutputDir();
			String filename = cmdArgs.getFilename();
			
			if(map!=null && genotypes!=null && gene!=null){
				a.mapASE(map, genotypes, expressions, gene, error, n, perm, outdir, filename);

			} else{
				cmdArgs.printHelp(System.err);
				System.exit(0);
			}
		}
		
		else if(fcn.equals("approximation")){
			InputStream map = cmdArgs.getMap();
			InputStream genotypes = cmdArgs.getGenotypeData();
			InputStream expressions = cmdArgs.getExpressionData();
			String gene = cmdArgs.getTestGene();
			int error = cmdArgs.getErrorNum();
			int n = cmdArgs.getSampleNum();
			int perm = cmdArgs.getPermNum();
			File outdir = cmdArgs.getOutputDir();
			String filename = cmdArgs.getFilename();
			
			if(map!=null && genotypes!=null && gene!=null){
				a.approxASE(map, genotypes, expressions, gene, error, n, perm, outdir, filename);

			} else{
				cmdArgs.printHelp(System.err);
				System.exit(0);
			}
		}
		
		else if(fcn.equals("chromatin")){
			InputStream chrom = cmdArgs.getChrom();
			InputStream genesmap = cmdArgs.getMap();
			InputStream variants = cmdArgs.getVariants();
			String gene = cmdArgs.getTestGene();
			int p = cmdArgs.getPermNum();
			
			File outdir = cmdArgs.getOutputDir();
			String filename = cmdArgs.getFilename();
			
			if(genesmap!=null && chrom!=null){
				a.assignChromatin(chrom,genesmap, variants, p, gene, outdir, filename);
			}
			
		}
		else if(fcn.equals("combinations")){
			InputStream map = cmdArgs.getMap();
			String gene = cmdArgs.getTestGene();
			InputStream genotypes = cmdArgs.getGenotypeData();
			InputStream expression = cmdArgs.getExpressionData();
			int n = cmdArgs.getSampleNum();
			int e = cmdArgs.getErrorNum();
			File out = cmdArgs.getOutputDir();
			String f = cmdArgs.getFilename();
			
			if(map!=null && gene!=null && genotypes!=null && expression!=null){
				a.generateCombinations(map, gene, genotypes, expression, n, e, out, f);
			} else{
				cmdArgs.printHelp(System.err);
				System.exit(0);
			}
			
		}
		else{
			System.out.println("Function not recognized");
			cmdArgs.printHelp(System.err);
			System.exit(0);
		}

		
	}





}
