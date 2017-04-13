package triple;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class mytemp {
	public static String B = "Before";
	public static String M = "Meets";
	public static String Mi = "MetBy";
	public static String O = "Overlaps";
	public static String Oi = "OverlappedBy";
	public static String S = "Starts";
	public static String Si = "StartedBy";
	public static String D = "During";
	public static String Di = "Contains";
	public static String F = "Finishes";
	public static String Fi = "FinishedBy";
	public static String Eq = "Equals";

	public static void main(String arg[]) throws IOException {
		/*
		 * 1.取出文件中的一个个三元组信息放入mytriple数组 2.遍历mytriple数组两两组合成my2triple数组
		 * 3.遍历my2triple数组，用to1223()方法转成1223的形式方便查表，true是成功转成1223，可以继续进行查表前往4；
		 * false是没有关联关系，不查表且移出数组，到5 4.查表：调用方法doCompositionTable()，得到可能的关系集合
		 * 5.对关系集合数组遍历,相同的归到一起，求交集，放入map，交集为空则出现矛盾，清空map；交集不为空整理打印结果
		 */
		/*BUG=.=
		 *1.当只遇到一组ab的可能关系集时，map为空，但实际上这唯一的一组关系集即推理结果。
		 *2.没有判断推理出的结果中是否有已知关系的三元组，有的话即发生了冲突，没有则正常。
		 *3.对结果进行排序。
		 */
		String path = ".//input//";
		String file = "Consistent.txt";
		//String file = "IntervalBased11.txt";
		String filename = path + file;
		String outfile = path + "upd" + file;
		String outsortfile = path + "updSort" + file;
		String stdfile = path + "std" + file;
		File f = new File(stdfile);
		f.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(f);
		PrintStream printStream = new PrintStream(fileOutputStream);
		System.setOut(printStream);
		// String file = ".//inconsistent_exam.owl";
		// ArrayList<mytriple> input = getMytriple_owl(file);
		ArrayList<mytriple> inputl = getMytriple(filename);
		ArrayList<my2triple> m2tl = getMy2triple(inputl);
		ArrayList<mytriple> relal = getRelationList(m2tl);
		Map<mytriple, Set<String>> map = doIntersection(relal);
		if (map.isEmpty()) {
			System.out.println("Inconsistent!");
		} else {
			for (Map.Entry<mytriple, Set<String>> entry : map.entrySet()) {
				//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				mytriple a = entry.getKey();
				System.out.println(a.getSub() + entry.getValue() + a.getObj());
			}
		}
		Set<String> ret = doArrangementStrEquals(inputl, map);
		//getOutputFileStr(ret, outfile);
		getOutputFileStrSort(ret, outsortfile);
	}

	public static void getOutputFile(ArrayList<mytriple> ret, String outfile) throws IOException {
		File file = new File(outfile);
		file.createNewFile();
		FileWriter fw = new FileWriter(outfile);
		BufferedWriter bw = new BufferedWriter(fw);
		for (mytriple my : ret) {
			bw.write(my.getSub() + " " + my.getPred() + " " + my.getObj());
			bw.newLine();
			bw.flush();
		}
		bw.close();
		fw.close();
	}

	public static void getOutputFileStr(Set<String> ret, String outfile) throws IOException {
		File file = new File(outfile);
		file.createNewFile();
		FileWriter fw = new FileWriter(outfile);
		BufferedWriter bw = new BufferedWriter(fw);
		for (String my : ret) {
			bw.write(my);
			bw.newLine();
			bw.flush();
		}
		bw.close();
		fw.close();
	}
	
	public static void getOutputFileStrSort(Set<String> ret, String outfile) throws IOException {
		String[] sret=new String[ret.size()];
		ret.toArray(sret);
		List<String> forsort = Arrays.asList(sret);
		Collections.sort(forsort);
		File file = new File(outfile);
		file.createNewFile();
		FileWriter fw = new FileWriter(outfile);
		BufferedWriter bw = new BufferedWriter(fw);
		for (String my : forsort) {
			bw.write(my);
			bw.newLine();
			bw.flush();
		}
		bw.close();
		fw.close();
	}
	
	public static ArrayList<mytriple> doArrangement(ArrayList<mytriple> mtl, Map<mytriple, Set<String>> map) {
		// Map<String,String> sub=new HashMap<String,String>();
		ArrayList<mytriple> ret = new ArrayList<mytriple>();
		for (mytriple mt : mtl) {
			ret.add(mt);
			ret.add(mt.getInverse());
		}
		for (Map.Entry<mytriple, Set<String>> entry : map.entrySet()) {
			Set<String> set = entry.getValue();
			mytriple my = entry.getKey();
			for (String str : set) {
				my.setPred(str);
				ret.add(my);
				ret.add(my.getInverse());
			}
		}
		return ret;
	}

	public static Set<String> doArrangementStr(ArrayList<mytriple> mtl, Map<mytriple, Set<String>> map) {
		// Map<String,String> sub=new HashMap<String,String>();
		Set<String> ret = new HashSet<String>();
		for (mytriple mt : mtl) {
			ret.add(mt.infoString());
			ret.add(mt.getInverse().infoString());
		}
		for (Map.Entry<mytriple, Set<String>> entry : map.entrySet()) {
			Set<String> set = entry.getValue();
			mytriple my = entry.getKey();
			for (String str : set) {
				my.setType(false);
				my.setPred(str);
				ret.add(my.infoString());
				ret.add(my.getInverse().infoString());
			}
		}
		return ret;
	}

	public static Set<String> doArrangementStrEquals(ArrayList<mytriple> mtl, Map<mytriple, Set<String>> map) {
		// Map<String,String> sub=new HashMap<String,String>();
		Set<String> ret = new HashSet<String>();
		for (mytriple mt : mtl) {
			ret.add(mt.infoString());
			ret.add(mt.getInverse().infoString());
			mytriple e1 = new mytriple(mt.getSub(), mytriple.Eq, mt.getSub());
			mytriple e2 = new mytriple(mt.getObj(), mytriple.Eq, mt.getObj());
			ret.add(e1.infoString());
			ret.add(e2.infoString());
		}
		for (Map.Entry<mytriple, Set<String>> entry : map.entrySet()) {
			Set<String> set = entry.getValue();
			mytriple my = entry.getKey();
			for (String str : set) {
				my.setType(false);
				my.setPred(str);
				ret.add(my.infoString());
				ret.add(my.getInverse().infoString());
			}
		}
		return ret;
	}

	public static Map<mytriple, Set<String>> doIntersection(ArrayList<mytriple> relal) {
		Map<mytriple, Set<String>> map = new HashMap<mytriple, Set<String>>();
		System.out.println("                                                                              here");
		for (int i = 0; i < relal.size(); i++) {
			mytriple one = relal.get(i);
			boolean flag = false;
			for (int j = i + 1; j < relal.size(); j++) {
				mytriple two = relal.get(j);
				//one.printinfo();
				//two.printinfo();
				// System.out.println(" "+" if");
				if (one.equals(two)) {
					flag = true;
					System.out.println("                                                                           if");
					// 判断两个关系集主语宾语一样，再求交集 one and two
					Set<String> ret = new HashSet<String>();
					ret.clear();
					ret.addAll(one.probrela);
					ret.retainAll(two.probrela);
					System.out.println("one haha " + one.probrela);
					System.out.println("two hehe " + two.probrela);
					if (map.containsKey(one)) {
						Set<String> last = map.get(one);
						// 与两条关系的交集作交集运算
						Set<String> ret2 = new HashSet<String>();
						ret2.clear();
						ret2.addAll(ret);
						ret.retainAll(last);
						// 将新的交集覆盖掉旧的
						map.put(one, ret2);
						System.out.println("intersection " + ret2);
						if (ret2.isEmpty()) {
							System.out.println("exists do retain empty!");
							one.printinfo();
							map.clear();
							return map;
						}
					} else {
						System.out.println("intersection " + ret);
						map.put(one, ret);
						if (ret.isEmpty()) {
							System.out.println("Intersection is empty!");
							map.clear();
							return map;
						}
					}
				}
			}
			if (!flag) {
				if (!map.containsKey(one)) {
					one.printinfo();
					map.put(one, one.getProb());
				}
			}
		}
		System.out.println("                    ---------------------in doIntersection end");
		return map;
	}

	public static ArrayList<mytriple> getRelationList(ArrayList<my2triple> m2t) {
		ArrayList<mytriple> ret = new ArrayList<mytriple>();
		for (Iterator<my2triple> iterator1 = m2t.iterator(); iterator1.hasNext();) {
			my2triple one = iterator1.next();
			one.printinfo();
			if (one.to1223()) {
				// search table
				Set<String> prob = one.doCompositionTable();
				mytriple my = new mytriple(one.gettri1().getSub(), prob, one.gettri2().getObj());
				ret.add(my);
				System.out.println("in getRelationList\t\t\t" + my.getSub() + prob + my.getObj());
			} else {
				System.out.println("in getRelationList\t\t\tno association");
				iterator1.remove();
			}
		}
		System.out.println("                    ---------------------in getRelationList end");
		return ret;
	}

	public static ArrayList<my2triple> getMy2triple(ArrayList<mytriple> mt) {
		ArrayList<my2triple> m2t = new ArrayList<my2triple>();
		for (int i = 0; i < mt.size(); i++) {
			mytriple one = mt.get(i);
			for (int j = i + 1; j < mt.size(); j++) {
				mytriple two = mt.get(j);
				my2triple m2 = new my2triple(one, two);
				// m2.printinfo();
				m2t.add(m2);
			}
		}
		System.out.println("                    ---------------------in getMy2triple end");
		return m2t;
	}

	public static ArrayList<mytriple> getMytriple(String filename) throws IOException {
		ArrayList<mytriple> ll = new ArrayList<mytriple>();
		File file = new File(filename);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		// int line = 0;
		while ((tempString = reader.readLine()) != null) {
			// System.out.println("line " + line + ": " + tempString);
			String[] t = tempString.split(" ");
			mytriple mt = new mytriple(t[0], t[1], t[2]);
			// System.out.println("aaa");
			// mt.printinfo();
			ll.add(mt);
			// System.out.print(" "+line);
			// ll.get(line).printinfo();
			// if(line>0)ll.get(line-1).printinfo();
			// line++;
		}
		reader.close();
		return ll;
	}
}
