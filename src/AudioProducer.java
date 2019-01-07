import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AudioProducer {
	
	private final HttpRequest http_req;
	
	public AudioProducer() {
		http_req = new HttpRequest();
	}
	
	void produce(String filepath, String text) {
		String encodedText = null;
		try {
			encodedText = URLEncoder.encode(text,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (encodedText != null && !encodedText.isEmpty()) {
			String url = "http://tsn.baidu.com/text2audio?lan=zh&ctp=1&cuid=ace0100d3ca5&tok=24.57229bce331265a390cf0809c01b2124.2592000.1549359820.282335-15356866&vol=5&per=0&spd=5&pit=5&aue=3";
			url += "&tex=" + encodedText;
			writeToFile(filepath, http_req.sendGet(url, null));
		}
	}
	
	void writeToFile(String filepath, InputStream input) {
		int size = 0;
		byte[] buffer = new byte[4096];
		OutputStream output = null;
		try {
			output = new FileOutputStream(filepath);
			while ((size = input.read(buffer)) > 0) {
				output.write(buffer, 0, size);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
