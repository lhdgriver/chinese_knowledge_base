package DBHandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
/*		
		String source_path = "source.txt";
		BufferedReader br = new BufferedReader(new FileReader(source_path));
		int i = 0;
		while(true) {
			if (i++ % 50000 == 0) System.out.println(i);
			String s = br.readLine();
			if(s == null) break;
			String[] s_arr = s.split("\t");
			d.insert(s_arr[0], s_arr[1], s_arr[2]);
		}
*/
		
		
///*		
		d.getResourcebyLiteral("liu");
//		d.getDistOne(null);

//*/
		d.close();
		System.out.println((System.currentTimeMillis()-start_t)/1000);

	}

}
