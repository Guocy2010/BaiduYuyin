import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static List<Pair<String, String>> readFile(String path) {
		List<Pair<String, String>> stationList = new ArrayList();

		try {
			String encoding = "UTF-8";
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				while ((lineText = bufferedReader.readLine()) != null) {
					if (lineText.isEmpty())
						continue;
					int pos1 = lineText.indexOf(":");
					int pos2 = lineText.indexOf("：");
					int sep = pos1 > pos2 ? pos1 : pos2;
					if (sep >= 0 && sep < lineText.length()) {
						String key = lineText.substring(0, sep).trim();
						String value = lineText.substring(sep + 1).trim();
						stationList.add(new Pair(key, value));
					}
				}
				read.close();
			} else {
				System.out.println("can not find file: " + path);
			}
		} catch (Exception e) {
			System.out.println("error of reading the contents of file: " + path);
			e.printStackTrace();
		}

		return stationList;
	}
	
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		
		if (dir.exists()) {
			return true;
		}
		
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		
		if (dir.mkdirs()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void processCurrentDir() {
		String exePath = System.getProperty("user.dir");
		if (exePath == null) {
			System.out.println("exePath is null!");
			return;
		}
		System.out.println("exePath: " + exePath);
		
		File exeDir = new File(exePath);
		for (File file : exeDir.listFiles()) {
			if (!file.isDirectory()) {
				String name = file.getName();
				if (!name.endsWith(".txt") || name.indexOf("_") <= 0) {
					continue;
				}
				
				String lineDir = exePath + File.separator + "line" + name.substring(0, name.indexOf("_"));
				if (!createDir(lineDir)) {
					continue;
				}
				
				String stationsWithText = exePath + File.separator + name.substring(0, name.lastIndexOf(".txt")) + ".temp";
				StationTransformer transformer = new StationTransformer();
				transformer.readFile(file.getAbsolutePath());
				transformer.writeFile(stationsWithText);
				
				System.out.println();
				System.out.println(stationsWithText + " generated!");
				
				AudioProducer audioProducer = new AudioProducer();
				List<Pair<String, String>> stationList = readFile(stationsWithText);
				for (Pair<String, String> item : stationList) {
					System.out.println(item.getLeft() + "\t" + item.getRight());
					audioProducer.produce(lineDir + File.separator + item.getLeft(), item.getRight());
				}
			}
		}
	}
	
	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("        java -jar BaiduYuyin.jar [options]\n");
		System.out.println("Options:");
		System.out.println("        -h|--help                   print usage information");
		System.out.println("        -s|--station <file>         station file containing all stations, each line for one station");
		System.out.println("        -t|--text <file>            text file containing messages transformed from stations, each ");
		System.out.println("                                    line with one \"xxx_xxx.mp3 : message text\", message text will");
		System.out.println("                                    be made into audio and stored in directory(-d defines) with ");
		System.out.println("                                    filename xxx_xxx.mp3");
		System.out.println("        -d|--directory <directory>  directory to store audio files for mp3 files defined in -t|--text");
		System.out.println("");
		System.out.println("Example:");
		System.out.println("        java -jar BaiduYuyin.jar -s 1_be.txt -t 1_be.temp -d line1");
		System.out.println("        java -jar BaiduYuyin.jar -s 1_be.txt -t 1_be.temp");
		System.out.println("        java -jar BaiduYuyin.jar -t 1_be.temp -d line1");
	}

	public static void main(String[] args) {
		
		String station = null;
		String text = null;
		String directory = null;
		
		// read files from current directory
		if (args.length <= 0) {
			processCurrentDir();
			return;
		}
		
		// process arguments
		for (int i = 0; i < args.length; i++) {
			// -h|--help
			if (args[i].compareToIgnoreCase("-h") == 0 || args[i].compareToIgnoreCase("--help") == 0) {
				printUsage();
				return;
			}
			
			// -s|--station
			if (args[i].compareToIgnoreCase("-s") == 0 || args[i].compareToIgnoreCase("--station") == 0) {
				if (i + 1 < args.length) {
					station = args[i + 1];
				} else {
					System.out.println("error: options -s|--station should be given a file name");
					return;
				}
			}
			
			// -t|--text
			if (args[i].compareToIgnoreCase("-t") == 0 || args[i].compareToIgnoreCase("--text") == 0) {
				if (i + 1 < args.length) {
					text = args[i + 1];
				} else {
					System.out.println("error: options -t|--text should be given a file name");
					return;
				}
			}
			
			// -d|--directory
			if (args[i].compareToIgnoreCase("-d") == 0 || args[i].compareToIgnoreCase("--directory") == 0) {
				if (i + 1 < args.length) {
					directory = args[i + 1];
				} else {
					System.out.println("error: options -d|--directory should be given a directory name");
					return;
				}
			}
		}
		
		if (station != null && text != null) {
			StationTransformer transformer = new StationTransformer();
			transformer.readFile(station);
			transformer.writeFile(text);
			System.out.println(text + " generated!");
		}
		
		if (text != null && directory != null) {
			if (!createDir(directory)) {
				return;
			}
			
			AudioProducer audioProducer = new AudioProducer();
			List<Pair<String, String>> stationList = readFile(text);
			for (Pair<String, String> item : stationList) {
				System.out.println(item.getLeft() + "\t" + item.getRight());
				audioProducer.produce(directory + File.separator + item.getLeft(), item.getRight());
			}
		}
	}
}
