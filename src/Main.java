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

	public static void main(String[] args) {
		
		
	}
}
