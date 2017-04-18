package triple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class mytriple {
	public static String B = "Before";
	public static String A = "After";
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
	public String sub, pred, obj;
	public boolean type;// 0 right relation; 1 prob relation
	public Set<String> probrela;

	public mytriple(String a, String b, String c) {
		sub = a;
		obj = c;
		pred = b;
		type = false;
		// probrela=new HashSet<String>();
		// probrela.add(pred);
	}

	public mytriple(String a, Set<String> prob, String b) {
		sub = a;
		obj = b;
		type = true;
		probrela = prob;
	}

	public String getSub() {
		return sub;
	}

	public String getObj() {
		return obj;
	}

	public String getPred() {
		return pred;
	}

	public void setPred(String a) {
		pred = a;
	}

	public Set<String> getProb() {
		return probrela;
	}

	public void setProb(String one) {
		if (!type) {
			type=true;
			probrela = new HashSet<String>();
			probrela.add(one);
		}
	}

	public void setProb(Set<String> one) {
		if (!type) {
			type=true;
			probrela = one;
		}
	}
	
	public void setType(boolean a) {
		type = a;
	}

	public boolean getType() {
		return type;
	}

	public void doInverse() {
		String tmp = sub;
		sub = obj;
		obj = tmp;
		tmp = getInverseRelation(pred);
		pred = tmp;
	}

	public mytriple getInverse() {
		String tmp = getInverseRelation(pred);
		return new mytriple(obj, tmp, sub);
	}

	public void doSetInverse() {
		String tmp = sub;
		sub = obj;
		obj = tmp;
		for (Iterator<String> it = probrela.iterator(); it.hasNext();) {
			String str = it.next();
			tmp = getInverseRelation(str);
			it.remove();// probrela.remove(str);
			probrela.add(tmp);
		}
	}

	public static String getInverseRelation(String x) {
		if (x.equals(B)) {
			return A;
		} else if (x.equals(A)) {
			return B;
		} else if (x.equals(M)) {
			return Mi;
		} else if (x.equals(Mi)) {
			return M;
		} else if (x.equals(O)) {
			return Oi;
		} else if (x.equals(Oi)) {
			return O;
		} else if (x.equals(S)) {
			return Si;
		} else if (x.equals(Si)) {
			return S;
		} else if (x.equals(D)) {
			return Di;
		} else if (x.equals(Di)) {
			return D;
		} else if (x.equals(F)) {
			return Fi;
		} else if (x.equals(Fi)) {
			return F;
		} else {
			return Eq;
		}
	}

	public boolean simequals(mytriple mt) {
		return mt.getObj().equals(obj) && mt.getSub().equals(sub);
	}

	public boolean equals(mytriple mt) {
		if (mt.getObj().equals(obj) && mt.getSub().equals(sub)) {
			// System.out.println("in equals =");
			return true;
		} else if (mt.getObj().equals(sub) && mt.getSub().equals(obj)) {
			System.out.println("after dosetinverse");
			doSetInverse();
			System.out.println("after dosetinverse");
			return true;
		} else
			return false;
	}

	public boolean inverse(mytriple mt) {
		return mt.getObj().equals(sub) && mt.getSub().equals(obj);
	}

	public String infoString() {
		String a;
		if (!type)
			a = sub + " " + pred + " " + obj;
		else
			a = sub + " " + probrela + " " + obj;
		return a;
	}

	public void printinfo() {
		if (!type)
			System.out.println(sub + pred + obj);
		else
			System.out.println(sub + probrela + obj);
	}

}
