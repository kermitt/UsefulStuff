
/*
 * This is supposed to take a .sql file a indent it, sort of SAX style...
 * Why bother?
 * To bother understand big SQL files
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.*;

public class IndentSQL {
	public static void main(String[] args) {
		Map<String, Seen> map = getTables();
		mainLogic(map);
	}

	static String pad(int n) {
		String spacer = "___";
		String result = "";
		if (n > 1) {
			for (int i = 0; i < n; i++) {
				result += spacer;
			}
		}
		return result;
	}

	static void mainLogic(Map<String, Seen> map) {
		int depth = 0;
		Map<String, Seen> over = new HashMap<>();
		for (String key : map.keySet()) {
			over.put(key, new Seen(0));
		}

		// going make rewrite the original misery.sql to show 'depth'
		write_1_of_2();
		String theDepth = "";
		try (BufferedReader br = new BufferedReader(new FileReader("raw.txt"))) {
			String line;
			int lineCount = 0;
			while ((line = br.readLine()) != null) {
				lineCount++; //
				line = line.toLowerCase();
				String key = contains(line, map);
				if (key != null) {
					over.get(key).seen++;

					int oddeven = over.get(key).seen % 2;
					if (oddeven == 0) {
						depth++;
					} else {
						depth--;
					}
					theDepth = pad(depth);
					String verbs = buildVerb(line);
					// log(i + "\t" + p + oddeven + "|" + over.get(key).seen +
					// "|" + key + " | " + verb + " -- " + line );
					log(lineCount + "|\t|" + theDepth + oddeven + "|" + over.get(key).seen + "|" + key + " | " + verbs);
				}
				write_2_of_2(theDepth, line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String buildVerb(String line) {
		String verb = "";

		if (line.contains("select")) {
			verb += "select ";
		}
		if (line.contains("create")) {
			verb += "create ";
		}
		if (line.contains("alter")) {
			verb += "alter ";
		}
		if (line.contains("drop")) {
			verb += "drop ";

		}
		if (line.contains("delete")) {
			verb += "delete ";
		}
		if (line.contains("join")) {
			verb += "join ";
		}
		// if (line.contains(" as ")) {
		// verb += "as ";
		// }

		if (line.contains("insert ")) {
			verb += "insert ";
		}
		if (line.contains("from ")) {
			verb += "from ";
		}
		if (line.contains("update")) {
			verb += "update ";
		}
		return verb;
	}

	static String contains(String line, Map<String, Seen> map) {
		String name = null;
		for (String key : map.keySet()) {
			if (line.contains(key)) {
				name = key;
			}
		}
		return name;
	}

	public static void log(String s) {
		System.out.println(s);
	}

	public static Map<String, Seen> getTables() {
		List<String> tables = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("raw.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.toLowerCase();
				if (line.contains(" table ")) {
					tables.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Seen> map = new HashMap<>();

		for (String s : tables) {
			String result = squish(s);
			if (map.containsKey(result)) {
				map.get(result).seen++;
			} else {
				map.put(result, new Seen());
			}
		}
		return map;
	}

	static String squish(String s) {
		s = s.replaceAll("create table ", "");
		s = s.replaceAll("drop table ", "");
		s = s.replaceAll("alter table ", "");
		s = s.replaceAll(";", "");
		String result = s.split(" ")[0];
		return result;
	}

	static void write_1_of_2() {
		try (FileWriter fw = new FileWriter("misery_shape.sql", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void write_2_of_2(String depth, String line) {
		try (FileWriter fw = new FileWriter("misery_shape.sql", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(depth + "|" + line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Seen {
	public int seen = 0;

	public Seen() {
		seen = 1;
	}

	public Seen(int seen) {
		this.seen = seen = 1;
	}

}
