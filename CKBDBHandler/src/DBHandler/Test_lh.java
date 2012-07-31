package DBHandler;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Test_lh {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		long start_t = System.currentTimeMillis();
		JenaDBHandler d = new JenaDBHandler();
		for (int i = 1; i <= 40000; i++) {
			String q = "@" + i+"http://''\"John    Smith";
			d.insert("A", i+"name", q);
		}
		d.close();
		System.out.println((System.currentTimeMillis()-start_t)/1000);
	}

}
