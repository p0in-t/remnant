package remnant;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import remnant.Scanner.*;

public class Remnant {
	static boolean foundError = false;
	
	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("At most 1 argument permitted.");
			System.exit(101);
		}
		else if (args.length == 1) {
			sourceThink(args[0]);
		}
		else {
			promptThink();
		}
	}
	
	private static void report(int line, String location, String errorInfo) {
		System.err.println("line : " + line + ", " + location + ", " + errorInfo);
		
		foundError = true;
	}
	
	public static void error(int line, String errorInfo) {
		report(line, "", errorInfo);
	}
	
	private static void exec(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.getTokens();
		
		for (Token token : tokens) {
			System.out.println(token);
		}
	}
	
	private static void promptThink() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		for (;;) {
			System.out.print("»");
			String line = reader.readLine();
			
			if (line == null) 
				break;
			
			exec(line);
			
			foundError = false;
		}
	}
	
	private static void sourceThink(String path) throws IOException {
		byte bytes[] = Files.readAllBytes(Paths.get(path));
		exec(new String(bytes, Charset.defaultCharset()));
		
		if (foundError)
			System.exit(1);
	}
}