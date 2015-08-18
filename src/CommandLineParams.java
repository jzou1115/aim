import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

public interface CommandLineParams {
	
	public void parse(String ... args) throws Exception;
	
	public void printHelp(PrintStream out);

	public InputStream getSNPsInput();
	public InputStream getGenesInput();
	public int getPermNum();
	public int getErrorNum();
	public File getOutputDir();
	public boolean help();
	
}
