import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

public class CommandLine implements CommandLineParams{
	
	private static final String DEFAULT_OUTPUT_DIR = "ase_output";
	
	private static final String SNP_TAG = "-s";
	private static final String GENE_TAG = "-g";
	private static final String GENOTYPES_TAG = "-a";
	private static final String EXPRESSIONS_TAG = "-b";
	private static final String PERM_TAG = "-p";
	private static final String ERROR_TAG = "-e";
	private static final String HELP_TAG = "-h";
	private static final String OUTPUT_TAG = "-o";
	private static final String FILE_TAG = "-f";
	private static final String TEST_TAG = "-t";
	private static final String SAMPLE_TAG = "-n";
	private static final String THRESHOLD_TAG = "-z";
	private static final String MAP_TAG = "-m";
	private static final String MAP_FCN = "genestosnps";
	private static final String SIM_FCN = "simulation";
	private static final String ASE_FCN = "mapase";
	private static final String COMB_FCN = "combinations";
	
	private File output = new File(DEFAULT_OUTPUT_DIR);
	private String outfile;
	private InputStream snps = System.in;
	private InputStream genes = System.in;
	//private InputStream map = System.in;
	private boolean help = false;
	private int perm;
	private int errors;
	private String test;
	private int sampleNum;
	private double threshold;
	private InputStream genotypes;
	private InputStream expressions;
	private InputStream map;
	private String function;
	
	@Override
	public void parse(String ... args) throws Exception{
		if(args.length==0){
			printHelp(System.err);
			System.exit(0);
		}
		for( int i = 0 ; i < args.length ; ++i ){
			String cur = args[i];
			switch(cur){
			case MAP_FCN: 
				//assertNextArg(MAP_FCN, i, args);
				function = MAP_FCN;
				break;
			case SIM_FCN: 
				//assertNextArg(SIM_FCN, i, args);
				function = SIM_FCN;
				break;
			case ASE_FCN:
				//assertNextArg(ASE_FCN, i, args);
				function = ASE_FCN;
				break;
			case COMB_FCN:
				//assertNextArg(ASE_FCN, i, args);
				function = COMB_FCN;
				break;
			case SNP_TAG: 
				assertNextArg(SNP_TAG, i, args);
				snps = parseSNPArg(args[++i]);
				break;
			case GENE_TAG: 
				assertNextArg(GENE_TAG, i, args);
				genes = parseGeneArg(args[++i]);
				break;
			case MAP_TAG: 
				assertNextArg(MAP_TAG, i, args);
				map = parseMapArg(args[++i]);
				break;
			case PERM_TAG: 
				assertNextArg(PERM_TAG, i, args);
				perm = parsePermArg(args[++i]);
				break;
			case ERROR_TAG: 
				assertNextArg(ERROR_TAG, i, args);
				errors = parseERRORArg(args[++i]);
				break;
			case SAMPLE_TAG: 
				assertNextArg(SAMPLE_TAG, i, args);
				sampleNum = parseSAMPLEArg(args[++i]);
				break;
			case TEST_TAG: 
				assertNextArg(TEST_TAG, i, args);
				test = parseTESTArg(args[++i]);
				break;
			case THRESHOLD_TAG: 
				assertNextArg(THRESHOLD_TAG, i, args);
				threshold = parseTHRESHOLDArg(args[++i]);
				break;
			case HELP_TAG:
				help = true;
				return;
			case OUTPUT_TAG:
				assertNextArg(OUTPUT_TAG, i, args);
				output = new File(args[++i]);
				break;
			case FILE_TAG:
				assertNextArg(FILE_TAG, i, args);
				outfile = parseFileTag(args[++i]);
				break;
			case GENOTYPES_TAG:
				assertNextArg(GENOTYPES_TAG, i, args);
				genotypes = parseGenotypesArg(args[++i]);
				break;
			case EXPRESSIONS_TAG:
				assertNextArg(EXPRESSIONS_TAG, i, args);
				expressions = parseExpressionArg(args[++i]);
				break;
			default:
				throw new Exception("Unrecognized flag: "+cur);
			}
		}
	}
	
	private String parseFileTag(String string) {
		return string.trim();
	}

	private InputStream parseMapArg(String string) throws FileNotFoundException {
		return new BufferedInputStream( new FileInputStream(new File(string)));
	}

	private InputStream parseExpressionArg(String string) throws FileNotFoundException {
		return new BufferedInputStream( new FileInputStream(new File(string)));
	}

	private InputStream parseGenotypesArg(String string) throws FileNotFoundException {
		return new BufferedInputStream( new FileInputStream(new File(string)));
	}

	private double parseTHRESHOLDArg(String string) {
		return Double.parseDouble(string);
	}

	private int parseSAMPLEArg(String string) {
		return Integer.parseInt(string);
	}

	private String parseTESTArg(String string) {
		return string.trim();
	}

	private int parseERRORArg(String string) {
		return Integer.parseInt(string);
	}

	private int parsePermArg(String string) {
		return Integer.parseInt(string);
	}

	private void assertNextArg(String flag, int index, String ... args) throws Exception{
		if( index + 1 >= args.length){
			throw new Exception("Flag `"+flag+"' requires an argument.");
		}
	}
	
	@Override
	public void printHelp(PrintStream out){
		out.println("Usage: ase <function> [<args>] \n");
		out.println("Functions:");
		out.println("<genestosnps>\tThis function takes a list of genes and a list of snps.  It creates a map from each gene to a list of snps.");
		out.println("<simulation>\tThis function starts a simulation for one gene.");
		out.println("<mapase>\tThis function maps variants to ASE.");
		out.println("<join>\tThis function joins output files from <simulation> or <mapase>");
		out.println("\nOptions:");
		out.println("-s\tSNP Locations");
		out.println("-g\tGene Locations");
		out.println("-m\tMap from gene to SNPs");
		out.println("-a\tGenotype file");
		out.println("-b\tExpression file");
		out.println("-p\tNumber of permutations for simulation");
		out.println("-e\tMaximum number of errors allowed");
		out.println("-n\tNumber of samples used");
		out.println("-z\tSignificance threshold");
		out.println("-o\tOutput file");
		out.println("-h\thelp statement");

	}
	
	private InputStream parseSNPArg(String s) throws FileNotFoundException {
		return new BufferedInputStream( new FileInputStream(new File(s)));
	}
	
	private InputStream parseGeneArg(String s) throws FileNotFoundException {
		return new BufferedInputStream( new FileInputStream(new File(s)));
	}
	
	@Override
	public InputStream getSNPsInput() {
		return snps;
	}
	
	@Override
	public InputStream getGenesInput() {
		return genes;
	}
	
	@Override
	public File getOutputDir() {
		if(!output.exists()){
			output.mkdirs();
		}
		return output;
	}
	
	@Override
	public boolean help() {
		return help;
	}

	@Override
	public int getPermNum() {
		return perm;
	}

	@Override
	public int getErrorNum() {
		return errors;
	}

	@Override
	public String getTestGene() {
		return test;
	}

	@Override
	public int getSampleNum() {
		return sampleNum;
	}

	@Override
	public double getThreshold() {
		return threshold;
	}

	@Override
	public InputStream getGenotypeData() {
		return genotypes;
	}

	@Override
	public InputStream getExpressionData() {
		return expressions;
	}

	@Override
	public InputStream getMap() {
		return map;
	}

	@Override
	public String getFunction() {
		return function;
	}

	@Override
	public String getFilename() {
		return outfile;
	}


	
}
