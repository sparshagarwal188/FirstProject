package com.semanticsquare.thrillio.util;

import java.io.*;
import java.util.List;

public class IOUtil {
	public static void read(List<String> data, String fileName) {
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				data.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String read(InputStream in) {
		StringBuilder text = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader (new InputStreamReader (in, "UTF-8"))) {
			String line;
			while((line = br.readLine()) != null) {
				text.append(line).append("\n");
			}
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return text.toString();
	}
	
	public static void write(String webpage, Integer id) {
		try(BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream("/Users/shrutiagarwal/eclipse-workspace/thrillio/pages/"+ String.valueOf(id) + ".html"), "UTF-8"))) {
			bw.write(webpage);
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
