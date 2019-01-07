import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StationTransformer {
	
	private final ArrayList<String> stations;
	private String firstStation;
	private String lastStation;
	private String busLine;
	private boolean isForward;
	
	StationTransformer() {
		this.stations = new ArrayList();
		this.firstStation = null;
		this.lastStation = null;
		this.busLine = null;
		this.isForward = true;
	}
	
	public void readFile(String path) {
		try {
			File file = new File(path);
			String name = file.getName();
			
			this.busLine = name.substring(0, name.indexOf("_"));
			
			if (name.contains("be")) {
				this.isForward = true;
			}
			
			if (name.contains("cf")) {
				this.isForward = false;
			}
			
			this.stations.clear();
			
			String encoding = "UTF-8";
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				while ((lineText = bufferedReader.readLine()) != null) {
					String trimmed = lineText.trim();
					if (trimmed.isEmpty())
						continue;
					this.stations.add(trimmed);
				}
				read.close();
			} else {
				System.out.println("can not find file: " + path);
			}
		} catch (Exception e) {
			System.out.println("error of reading the contents of file: " + path);
			e.printStackTrace();
		}
	}
	
	private String transform() {
		this.firstStation = this.stations.get(0);
		this.lastStation = this.stations.get(this.stations.size() - 1);
		
		String fullText = new String();
		for (int index = 0; index < this.stations.size(); index++) {
			String pStation = this.stations.get(index);
			String nStation = null;
			if (index + 1 < this.stations.size()) {
				nStation = this.stations.get(index + 1);
			}
			
			String pMessage = null;
			String nMessage = null;
			if (pStation.equals(this.firstStation)) {
				pMessage = String.format("%02d%s_%02d.mp3 ：各位乘客，欢迎您乘坐%s路公交车，本班车从%s开往%s。\n", index + 1, this.isForward ? "b" : "c", index + 1, this.busLine, this.firstStation, this.lastStation);
			} else if (pStation.equals(this.lastStation)) {
				pMessage = String.format("%02d%s_%02d.mp3 ：乘客们，终点站%s到了，请携带好你的行李和物品依次从后门下车。感谢你一路对我们的支持，欢迎下次乘坐我们的车！\n", index + 1, this.isForward ? "b" : "c", index + 1, this.lastStation);
			} else {
				pMessage = String.format("%02d%s_%02d.mp3 ：%s到了，请携带好你的行李和物品依次从后门下车。\n", index + 1, this.isForward ? "b" : "c", index + 1, pStation);
			}
			fullText += pMessage;
			
			if (nStation != null) {
				if (nStation.equals(this.lastStation)) {
					nMessage = String.format("%02d%s_%02d.mp3 ：车辆起步请站稳扶好，前方到站终点站%s，下车的乘客请提前做好准备，从后门下车。\n", index + 1, this.isForward ? "e" : "f", index + 1, this.lastStation);
				} else {
					nMessage = String.format("%02d%s_%02d.mp3 ：车辆起步请站稳扶好，前方到站%s，下车的乘客请提前做好准备，从后门下车。\n", index + 1, this.isForward ? "e" : "f", index + 1, nStation);
				}
				fullText += nMessage;
			}
		}
		
		return fullText;
	}
	
	public void writeFile(String path) {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(path);
			output.write(this.transform().getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
